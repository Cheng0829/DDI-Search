package com.ddisearch.service.impl;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
import java.util.*;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ddisearch.data.DrugBankXMLHandler;
import com.ddisearch.entity.DDI;
import com.ddisearch.entity.Drug;
import com.ddisearch.entity.batchDDIResult;
import com.ddisearch.mapper.DDIMapper;
import com.ddisearch.mapper.DrugInfoMapper;
import com.ddisearch.service.BasicService;

import java.io.*;

@Service
public class BasicServiceImpl implements BasicService {

    @Autowired
    private DrugInfoMapper drugInfoMapper;
    @Autowired
    private DDIMapper ddiMapper;
    public String batchInsertAllDrugInfoAndDDI(){
        ArrayList<Drug> drugs = DrugBankXMLHandler.parseDrugBankXML();
        ArrayList<DDI> ddis = DrugBankXMLHandler.ddiList;
        if (drugs == null) {
            return "暂无数据";
        }
        System.out.println("开始插入...");
        int location = 0;
        int n = 1000; // 性能限制：单次最大操作条数
        if(drugs.size() < n) {
            drugInfoMapper.batchInsertAllDrugInfo(drugs);
        }

        else{
            for(int i = 0; i < drugs.size(); i += n){
                if(i%1000 == 0){
                    System.out.println("已插入" + location + "条数据");
                }
                ArrayList<Drug> subList = new ArrayList<>(drugs.subList(i, Math.min(i + n, drugs.size())));
                drugInfoMapper.batchInsertAllDrugInfo(subList);

                location += n;
                System.out.println("已插入" + location + "条数据");
            }
        }

        location = 0;
        if(ddis.size() < n) {
            ddiMapper.batchInsertAllDDI(ddis);
        }
        else{
            for(int i = 0; i < ddis.size(); i += n){
                ArrayList<DDI> subList = new ArrayList<>(ddis.subList(i, Math.min(i + n, ddis.size())));

                ddiMapper.batchInsertAllDDI(subList);
//                drugInfoMapper.batchInsertDrugInfo((ArrayList) drugs.subList(i, Math.min(i + 100, drugs.size())));
                location += n;
                System.out.println("已插入" + location + "条数据");
            }
        }
        System.out.println("插入完成");
        return "插入完成";
    }

    public String batchInsertDrugInfo(){
        ArrayList<Drug> drugs = drugInfoCsvReader();
        if (drugs == null) {
            return "暂无数据";
        }
        int location = 0;
        int n = 1000; // 性能限制：单次最大操作条数
        if(drugs.size() < n) {
            drugInfoMapper.batchInsertDrugInfo(drugs);
        }
        else{
            for(int i = 0; i < drugs.size(); i += n){
                ArrayList<Drug> subList = new ArrayList<>(drugs.subList(i, Math.min(i + n, drugs.size())));
                drugInfoMapper.batchInsertDrugInfo(subList);
//                drugInfoMapper.batchInsertDrugInfo((ArrayList) drugs.subList(i, Math.min(i + 100, drugs.size())));
                location += n;
                System.out.println("已插入" + location + "条数据");
            }
        }
        return "插入完成";
    }

    public String batchInsertDDI(){
        ArrayList<DDI> ddis = ddiCsvReader();
        if (ddis == null) {
            return "暂无数据";
        }
        int location = 0;
        int n = 1000; // 性能限制：单次最大操作条数
        if(ddis.size() < n) {
            ddiMapper.batchInsertDDI(ddis);
        }
        else{
            for(int i = 0; i < ddis.size(); i += n){
                ArrayList<DDI> subList = new ArrayList<>(ddis.subList(i, Math.min(i + n, ddis.size())));

                ddiMapper.batchInsertDDI(subList);
//                drugInfoMapper.batchInsertDrugInfo((ArrayList) drugs.subList(i, Math.min(i + 100, drugs.size())));
                location += n;
                System.out.println("已插入" + location + "条数据");
            }
        }
        return "插入完成";
    }

    public ArrayList<Drug> batchSelectAllDrug(){
        ArrayList<Drug> drugs = drugInfoMapper.batchSelectAllDrug();
        return drugs;
    }

    public ArrayList<batchDDIResult> batchSelectAllDDI(){
        ArrayList<batchDDIResult> ddis = ddiMapper.batchSelectAllDDI();
        return ddis;
    }
    public Drug selectDrugInfoByName(String name){
        Drug drug = drugInfoMapper.selectDrugInfoByName(name);
        return drug;
    }

    public ArrayList<DDI> selectDDIByName(String drugAName, String drugBName){
        // 可能同时存在多个ddi
        ArrayList<DDI> ddis = ddiMapper.selectDDIByName(drugAName, drugBName);
        // A和B没有先后次序之分
        if(ddis.isEmpty()){
            ddis = ddiMapper.selectDDIByName(drugBName, drugAName);
        }

        return ddis;
    }

    public ArrayList<Map<String, String>> batchSelectDDI(int index, int limit){
        int offset = (index-1)*limit;
        ArrayList<batchDDIResult> batchDDIs = ddiMapper.batchSelectDDI(offset, limit);
        // batchDDIs: [[药物A1, 药物B1, DDI描述1], [药物A2, 药物B2, DDI描述2]...]
        // result: [[drugAName: 药物A1, drugBName: 药物B1, ddiDescription: DDI描述1],[drugAName: 药物A2, drugAName: 药物B2, ddiDescription: DDI描述2]...]

        ArrayList<Map<String, String>> result = new ArrayList<>();

        for(batchDDIResult ddi : batchDDIs){
            // ddi: [药物A1, 药物B1, DDI描述1]
            Map<String, String> ddiMap = new HashMap<>();
            ddiMap.put("drugAName", ddi.getDrugAName());
            ddiMap.put("drugBName", ddi.getDrugBName());
            ddiMap.put("ddiDescription", ddi.getDdiDescription());
            // ddiMap: [drugAName: 药物A1, drugBName: 药物B1, ddiDescription: DDI描述1]
            result.add(ddiMap);
        }
        return result;
    }

    public ArrayList<Map<String, String>> pagesSearch(int index, int limit){
        ArrayList<Map<String, String>> result = batchSelectDDI(index, limit);
        return result;
    }

    public Map<String, Object> handleSearch(String drugAName, String drugBName) {
        // drugA的不为空判断逻辑由前端控制
        if(drugBName.isEmpty()) {
            return singleDrugSearch(drugAName);
        } else {
            return twoDrugSearch(drugAName, drugBName);
        }
    }

    // 查找单个药物
    public Map<String, Object> singleDrugSearch(String drugAName) {
        Drug drug = selectDrugInfoByName(drugAName);
        if(drug == null) {
            return new HashMap<String, Object>(){{
                put("drugA", null);
            }};
        }

        Map<String, Object> DrugResultList = new HashMap<>();
        DrugResultList.put("orderId", drug.getOrderId());
        DrugResultList.put("drugbankId", drug.getDrugbankId());
        DrugResultList.put("name", drug.getName());
        DrugResultList.put("category", drug.getCategory());
        DrugResultList.put("chemicalFormula", drug.getChemicalFormula());
        DrugResultList.put("smiles", drug.getSmiles());
        DrugResultList.put("description", drug.getDescription());
        DrugResultList.put("relatedDrugs", drug.getRelatedDrugs());
        DrugResultList.put("pharmacodynamics", drug.getPharmacodynamics());
        DrugResultList.put("actionMechanism", drug.getActionMechanism());
        DrugResultList.put("proteinBinding", drug.getProteinBinding());
        DrugResultList.put("metabolism", drug.getMetabolism());
//        return DrugResultList;//.toString();
        return new HashMap<String, Object>(){{
            put("drugA", DrugResultList);
//            put("drugB", new HashMap<>());
        }};
    }

    // 查找两个药物
    public Map<String, Object> twoDrugSearch(String drugAName, String drugBName) {
        Map<String, Object> nullResult = new HashMap<>();
        Map<String, Object> drugAResult = new HashMap<>();
        Map<String, Object> drugBResult = new HashMap<>();
        Map<String, Map> ddiResultList = new HashMap<>();

        Drug drugA = selectDrugInfoByName(drugAName);
        if(drugA == null){
            nullResult.put("drugA", null);
        }
        else{
            drugAResult.put("orderId", drugA.getOrderId());
            drugAResult.put("drugbankId", drugA.getDrugbankId());
            drugAResult.put("name", drugA.getName());
            drugAResult.put("category", drugA.getCategory());
            drugAResult.put("chemicalFormula", drugA.getChemicalFormula());
            drugAResult.put("smiles", drugA.getSmiles());
            drugAResult.put("description", drugA.getDescription());
            drugAResult.put("relatedDrugs", drugA.getRelatedDrugs());
            drugAResult.put("pharmacodynamics", drugA.getPharmacodynamics());
            drugAResult.put("actionMechanism", drugA.getActionMechanism());
            drugAResult.put("proteinBinding", drugA.getProteinBinding());
            drugAResult.put("metabolism", drugA.getMetabolism());
            nullResult.put("drugA", drugAResult);
        }

        Drug drugB = selectDrugInfoByName(drugBName);
        if(drugB == null){
            nullResult.put("drugB", null);
        }
        else{
            drugBResult.put("orderId", drugB.getOrderId());
            drugBResult.put("drugbankId", drugB.getDrugbankId());
            drugBResult.put("name", drugB.getName());
            drugBResult.put("category", drugB.getCategory());
            drugBResult.put("chemicalFormula", drugB.getChemicalFormula());
            drugBResult.put("smiles", drugB.getSmiles());
            drugBResult.put("description", drugB.getDescription());
            drugBResult.put("relatedDrugs", drugB.getRelatedDrugs());
            drugBResult.put("pharmacodynamics", drugB.getPharmacodynamics());
            drugBResult.put("actionMechanism", drugB.getActionMechanism());
            drugBResult.put("proteinBinding", drugB.getProteinBinding());
            drugBResult.put("metabolism", drugB.getMetabolism());
            nullResult.put("drugB", drugBResult);
        }
        if(drugA == null || drugB == null){
            return nullResult;
        }

        ArrayList<DDI> ddis = selectDDIByName(drugAName, drugBName);
        for(DDI ddi : ddis){
            ddiResultList.put(ddi.getDdiType(), new HashMap<String, String>(){{
                put("description", ddi.getDescription());
//                put("description", getDrugBankDDIDescription(drugAName, drugBName, ddi.getDdiType(), ddis.size()));
                put("confidence", String.valueOf(ddi.getConfidence()));
            }});
        }

        return new HashMap<String, Object>(){{
            put("drugA", drugAResult);
            put("drugB", drugBResult);
            put("ddi", ddiResultList);
        }};
    }

    public static ArrayList<Drug> drugInfoCsvReader() {

        try (Reader reader = new FileReader("D:\\Java\\code\\DDI-Search\\src\\main\\java\\com\\ddisearch\\data\\drugInfo_1710_crawl.csv");
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            ArrayList<Drug> drugs = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                // 获取每一列的数据
                String orderId = csvRecord.get("orderId");
                String drugbankId = csvRecord.get("drugbankId");
                String name = csvRecord.get("name");
                String category = csvRecord.get("category");
                String chemicalFormula = csvRecord.get("chemicalFormula");
                String smiles = csvRecord.get("smiles");
                String description = csvRecord.get("description");
                String relatedDrugs = csvRecord.get("relatedDrugs");
                String pharmacodynamics = csvRecord.get("pharmacodynamics");
                String actionMechanism = csvRecord.get("actionMechanism");
                String proteinBinding = csvRecord.get("proteinBinding");
                String metabolism = csvRecord.get("metabolism");


                Drug drug = new Drug(orderId, drugbankId, name, category, chemicalFormula, smiles, description, relatedDrugs, pharmacodynamics, actionMechanism, proteinBinding, metabolism);
                drugs.add(drug);
            }
            return drugs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static ArrayList<DDI> ddiCsvReader() {

        try (Reader reader = new FileReader("D:\\Java\\code\\DDI-Search\\src\\main\\java\\com\\ddisearch\\data\\ddi.csv");
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            ArrayList<DDI> ddis = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                // 获取每一列的数据
                String drugA = csvRecord.get("drugA");
                String drugB = csvRecord.get("drugB");
                String ddiType = csvRecord.get("ddiType");
                String description = csvRecord.get("description");
                float confidence = Float.parseFloat(csvRecord.get("confidence"));
                DDI ddi = new DDI(drugA, drugB, ddiType, description, confidence);
                ddis.add(ddi);
            }
            return ddis;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
