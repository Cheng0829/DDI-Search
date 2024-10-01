package com.ddisearch.service.impl;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
import com.ddisearch.entity.DDI;
import com.ddisearch.entity.Drug;
import com.ddisearch.entity.batchDDIResult;
import com.ddisearch.mapper.DDIMapper;
import com.ddisearch.mapper.DrugInfoMapper;
import com.ddisearch.service.BasicService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import org.apache.commons.csv.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.PathVariable;

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
                put("description", getDrugBankDDIDescription(drugAName, drugBName, ddi.getDdiType(), ddis.size()));
                put("confidence", String.valueOf(ddi.getConfidence()));
            }});
        }

        return new HashMap<String, Object>(){{
            put("drugA", drugAResult);
            put("drugB", drugBResult);
            put("ddi", ddiResultList);
        }};
    }

    public String getDrugBankDDIDescription(String drugAName, String drugBName, String ddiType, int n) {
        String drugADrugBankId = replaceDrugNameToDrugBankID(drugAName);
        String url = "https://go.drugbank.com/drugs/" + drugADrugBankId + "/drug_interactions.json?search%5Bvalue=" + drugBName;
        String cookie = "hubspotutk=b214ced45a953849626f3112c0566792; _ga=GA1.1.1085880825.1727508634; cookieyes-consent=consentid:S3FPMDByWHhybXE0NmZ3V1N6WWlPNVdLNXRBelJIN0Q,consent:yes,action:no,necessary:yes,functional:yes,analytics:yes,performance:yes,advertisement:yes,other:yes; _gcl_au=1.1.1013946945.1727508635; _hjSessionUser_191585=eyJpZCI6IjQwMjViZGVmLTc2MmQtNTZhMS1hNDg4LTQ1ZTFlNjFjZTBjNyIsImNyZWF0ZWQiOjE3Mjc1MDg2MzMxMjEsImV4aXN0aW5nIjp0cnVlfQ==; __hssrc=1; remember_public_user_token=eyJfcmFpbHMiOnsibWVzc2FnZSI6Ilcxc3pNamd6TURSZExDSWtNbUVrTVRFa1NDNXJhbWhSVFZCbGFUVk5MeTR3VjJKSGNYRXlUeUlzSWpFM01qYzJNVFl3TWpZdU56ZzFOelF5SWwwPSIsImV4cCI6IjIwMjQtMTAtMTNUMTM6MjA6MjYuNzg1WiIsInB1ciI6ImNvb2tpZS5yZW1lbWJlcl9wdWJsaWNfdXNlcl90b2tlbiJ9fQ%3D%3D--cfa5febb2690387d0f1f9afeb65209ca73a12234; _clck=1qhlmtk%7C2%7Cfpm%7C0%7C1732; __hstc=49600953.b214ced45a953849626f3112c0566792.1727508633128.1727665697965.1727676334763.8; _omx_drug_bank_session=ILP51QHXZIuDQ16LNqJmJQ3A1SeTGymENlg1VCVn4I%2BgXVYrNsGngnyGBdIygPXYfO1OOrk6p8n02dDZyN8eaKyvqpCVBAWqip1SDvBDKX40SodDM8Ajz6h9zE17d9OGLOk0WgqqI1KVGaqhGqW%2F3MBF3wUYtHhOY7sIu6TsEhObgNB1hNync1t8RAEdFQERSCzcaZlTv6mLKagcq5TJx9X36wVQ2mr5rrCMAdGr7cZkr7ewr11eeNx3a00kSVLeeCkTzo5xloUdom8TzynQpBqBhEc645DGmocXdtf3FjFadrxniGQPmXy9Kc%2BOViJMWDsE4%2B7x7Tzl%2FuPVIjTaUY1gCBt0ITtAHoqoQhHSeUJsrmPKsitcSAEdwjTgoMxLNXojIiG8yT6sonMKK4%2BO4bWOHdl712gKV7mQTRid6iVj6cpgMpgIhNaPAmAPoDlkdPGG8LGSfZzHNSL%2FL2AgRA2f2UrhUg%3D%3D--WDw5IAxZIA28TKya--BgnLmhaQalnqku3fEUw5Ug%3D%3D; _clsk=1m7ytop%7C1727683357840%7C2%7C1%7Cr.clarity.ms%2Fcollect; _ga_DDLJ7EEV9M=GS1.1.1727683363.11.0.1727683363.60.0.0";
        // 添加代理
        String proxyHost = "127.0.0.1";
        int proxyPort = 7890;

        if(n == 1 && 1==0){
            try {
                // 使用 Jsoup 发送 HTTP GET 请求
                Document document = Jsoup.connect(url)
                        .proxy(proxyHost, proxyPort)
                        .header("Cookie", cookie)
                        .ignoreContentType(true) // 忽略内容类型
                        .get();

                // 获取响应体
                String responseBody = document.body().text();

                // 解析 JSON 数据
                JSONObject jsonObject = new JSONObject(responseBody);

                // 获取 data 数组中的第一个元素
                if (jsonObject.has("data") && jsonObject.getJSONArray("data").length() > 0) {
                    String interactionDescription = jsonObject.getJSONArray("data")
                            .getJSONArray(0)
                            .getString(1);
                    System.out.println(interactionDescription);
                    return interactionDescription;
                } else {
                    String filePath = "D:\\Java\\code\\DDI-Search\\src\\main\\java\\com\\ddisearch\\data\\DDI-DescriptionTemplate.json";
                    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                        StringBuilder jsonBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            jsonBuilder.append(line);
                        }

                        // 将文件内容转换为 JSONObject
                        JSONObject jsonObject1 = new JSONObject(jsonBuilder.toString());
                        if(jsonObject1.has(ddiType)){
                            // 提取所需信息
                            String ddiDescriptionTemplate = jsonObject1.getString(ddiType);
                            return ddiDescriptionTemplate.replace("drugAName", drugAName).replace("drugBName", drugBName);
                        }
                        else {
                            return "";
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        else {
            String filePath = "D:\\Java\\code\\DDI-Search\\src\\main\\java\\com\\ddisearch\\data\\DDI-DescriptionTemplate.json";
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                // 将文件内容转换为 JSONObject
                JSONObject jsonObject1 = new JSONObject(jsonBuilder.toString());
                if(jsonObject1.has(ddiType)){
                    // 提取所需信息
                    String ddiDescriptionTemplate = jsonObject1.getString(ddiType);
                    return ddiDescriptionTemplate.replace("drugAName", drugAName).replace("drugBName", drugBName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String replaceDrugNameToDrugBankID(String drugAName){
        String filePath = "D:\\Java\\code\\DDI-Search\\src\\main\\java\\com\\ddisearch\\data\\DrugBankId_DrugName.json";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            // 将文件内容转换为 JSONObject
            JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
            String drugBankId = jsonObject.getString(drugAName);
            // 提取所需信息
            return drugBankId;

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
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
