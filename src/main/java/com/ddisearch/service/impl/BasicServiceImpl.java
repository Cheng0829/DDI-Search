package com.ddisearch.service.impl;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
import com.ddisearch.entity.DDI;
import com.ddisearch.entity.Drug;
import com.ddisearch.mapper.DDIMapper;
import com.ddisearch.mapper.DrugInfoMapper;
import com.ddisearch.service.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import org.apache.commons.csv.*;
import java.io.*;

@Service
public class BasicServiceImpl implements BasicService {

    @Autowired
    private DrugInfoMapper drugInfoMapper;
    @Autowired
    private DDIMapper ddiMapper;

    public String batchInsertDrugInfo(){
        ArrayList<Drug> drugs = drugInfoCsvReader();
        if (drugs == null) {
            return "暂无数据";
        }
        drugInfoMapper.batchInsertDrugInfo(drugs);

        StringBuilder drugStrs = new StringBuilder();

        for(Drug drug : drugs){
            drugStrs.append(drug.toString()).append("<br>");
        }
        return drugStrs.toString();
    }

    public Drug selectDrugInfoByName(String name){
        Drug drug = drugInfoMapper.selectDrugInfoByName(name);
        return drug;
    }

    public String batchInsertDDI(){
        ArrayList<DDI> ddis = ddiCsvReader();
        if (ddis == null) {
            return "暂无数据";
        }
        ddiMapper.batchInsertDDI(ddis);

        StringBuilder ddiStrs = new StringBuilder();

        for(DDI ddi : ddis){
            ddiStrs.append(ddi.toString()).append("<br>");
        }
        return ddiStrs.toString();
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

    public String handleSearch(String drugAName, String drugBName) {
        // drugA的不为空判断逻辑由前端控制
        if(drugBName.isEmpty()) {
            return singleDrugSearch(drugAName);
        } else {
            return twoDrugSearch(drugAName, drugBName);
        }
    }

    // 查找单个药物
    public String singleDrugSearch(String drugAName) {
        Drug drug = selectDrugInfoByName(drugAName);
        if (drug == null) {
            return "暂无数据";
        }
        return drug.toString();
    }

    // 查找两个药物
    public String twoDrugSearch(String drugAName, String drugBName) {
        Drug drugA = selectDrugInfoByName(drugAName);
        Drug drugB = selectDrugInfoByName(drugBName);
        ArrayList<DDI> ddis = selectDDIByName(drugAName, drugBName);

        // ddi存在则两个药物必存在
        if(ddis.isEmpty()){
            return "暂无数据";
        }

        StringBuilder ddiStrs = new StringBuilder();

        for(DDI ddi : ddis){
            ddiStrs.append(ddi.toString()).append("<br>");
        }
        return drugA.toString() + "<br>" + drugB.toString() + "<br>" + ddiStrs.toString();
    }

    // 查找两个药物之间的多种副作用
    public String ddiSearch(String drugAName, String drugBName) {
        ArrayList<DDI> ddis = selectDDIByName(drugAName, drugBName);
        StringBuilder ddiStrs = new StringBuilder();
        for(DDI ddi : ddis){
            ddiStrs.append(ddi.toString()).append("<br>");
        }
        return ddiStrs.toString();
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

                Drug drug = new Drug(orderId, drugbankId, name, category, chemicalFormula, smiles, description, relatedDrugs);
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
