package com.ddisearch.service.impl;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
import java.util.*;

import com.ddisearch.entity.batchDrugResult;
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

    public ArrayList<Map<String, String>> pagesDDISearch(int index, int limit){
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

    public ArrayList<Map<String, String>> pagesDrugSearch(int index, int limit){
        int offset = (index-1)*limit;
        ArrayList<batchDrugResult> batchDrugs = drugInfoMapper.batchSelectDrug(offset, limit);
        ArrayList<Map<String, String>> result = new ArrayList<>();

        for(batchDrugResult drug : batchDrugs){
            // ddi: [药物A1, 药物B1, DDI描述1]
            Map<String, String> drugInfoMap = new HashMap<>();
            drugInfoMap.put("name", drug.getName());
            drugInfoMap.put("description", drug.getDescription());
            result.add(drugInfoMap);
        }
        return result;
    }

public Map<String, Object> handleDrugSearch(String drugName) {
    Drug drug = selectDrugInfoByName(drugName);
    Map<String, Object> drugResult = new HashMap<>();
    if(drug == null){
        return null;
    }
    else{
        drugResult.put("orderId", drug.getOrderId());
        drugResult.put("drugbankId", drug.getDrugbankId());
        drugResult.put("name", drug.getName());
        drugResult.put("category", drug.getCategory());
        drugResult.put("chemicalFormula", drug.getChemicalFormula());
        drugResult.put("smiles", drug.getSmiles());
        drugResult.put("description", drug.getDescription());
        drugResult.put("relatedDrugs", drug.getRelatedDrugs());
        drugResult.put("pharmacodynamics", drug.getPharmacodynamics());
        drugResult.put("actionMechanism", drug.getActionMechanism());
        drugResult.put("proteinBinding", drug.getProteinBinding());
        drugResult.put("metabolism", drug.getMetabolism());
    }
    return drugResult;
}

    // 查找单个药物
    public Map<String, Object> singleDrugSearch(String drugName) {
        Drug drug = selectDrugInfoByName(drugName);
        Map<String, Object> drugResult = new HashMap<>();
        if(drug == null){
            return null;
        }
        else{
            drugResult.put("orderId", drug.getOrderId());
            drugResult.put("drugbankId", drug.getDrugbankId());
            drugResult.put("name", drug.getName());
            drugResult.put("category", drug.getCategory());
            drugResult.put("chemicalFormula", drug.getChemicalFormula());
            drugResult.put("smiles", drug.getSmiles());
            drugResult.put("description", drug.getDescription());
            drugResult.put("relatedDrugs", drug.getRelatedDrugs());
            drugResult.put("pharmacodynamics", drug.getPharmacodynamics());
            drugResult.put("actionMechanism", drug.getActionMechanism());
            drugResult.put("proteinBinding", drug.getProteinBinding());
            drugResult.put("metabolism", drug.getMetabolism());
        }
        return drugResult;
    }


    public Map<String, Object> handleDDISearch(String drugAName, String drugBName) {
        Map<String, Object> drugAResult = singleDrugSearch(drugAName);
        Map<String, Object> drugBResult = singleDrugSearch(drugBName);
        Map<String, Map> ddiResultList = new HashMap<>();

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
