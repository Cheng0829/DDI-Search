package com.ddisearch.service.impl;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
import com.ddisearch.entity.DrugInfo;
import com.ddisearch.mapper.DrugInfoMapper;
import com.ddisearch.service.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
public class BasicServiceImpl implements BasicService {

    @Autowired
    private DrugInfoMapper drugInfoMapper;

    public String singleInsertDrugInfo(){
        ArrayList<DrugInfo> drugInfos = JsonReader();
        if (drugInfos == null) {
            return "暂无数据";
        }
        DrugInfo drugInfo = drugInfos.get(0);
        drugInfoMapper.singleInsertDrugInfo(drugInfo);
        return drugInfos.toString();
    }

    public String batchInsertDrugInfo(){
        ArrayList<DrugInfo> drugInfos = JsonReader();
        if (drugInfos == null) {
            return "暂无数据";
        }
        drugInfoMapper.batchInsertDrugInfo(drugInfos);
        return drugInfos.toString();
    }

    public String selectDrugInfoByName(String name){
        DrugInfo drugInfo = drugInfoMapper.selectDrugInfoByName(name);
        if (drugInfo == null) {
            return "暂无数据";
        }
        return drugInfo.toString();
    }

    public String handleSearch(String drugA, String drugB) {

        // drugA的不为空判断逻辑由前端控制
        if(drugB.isEmpty()) {
            return singleDrugSearch(drugA);
        } else {
            return twoDrugSearch(drugA, drugB);
        }
    }

    // 查找单个药物
    public String singleDrugSearch(String drugA) {
        return selectDrugInfoByName(drugA);
    }

    // 查找两个药物
    public String twoDrugSearch(String drugA, String drugB) {
//        DrugInfo drugAInfo = selectDrugInfoByName(drugA);
//        DrugInfo drugBInfo = selectDrugInfoByName(drugB);
        String ddiInfo = ddiSearch(drugA, drugB);
        return "cjk";
    }


    // 查找两个药物之间的多种副作用
    public String ddiSearch(String drugA, String drugB) {
        return "cjk";
    }

    public static ArrayList<DrugInfo> JsonReader() {
        try {
            // 从文件读取字符串
            String content = new String(Files.readAllBytes(Paths.get("D:\\Java\\code\\DDI-Search\\src\\main\\java\\com\\ddisearch\\data\\drugInfo_1710_crawl.json")));

            // 将字符串转换为JSONObject
            JSONObject jsonObject = new JSONObject(content);

            ArrayList<DrugInfo> drugInfos = new ArrayList<>();
            // 遍历JSONObject中的每一个键（即每个药物的名称）
            for (String drugName : jsonObject.keySet()) {
                // 获取当前药物的对象
                JSONObject drug = jsonObject.getJSONObject(drugName);
                // 从药物对象中获取各个字段的值
                int orderId = drug.getInt("orderId");
                String drugbankId = drug.getString("drugbankId");
                String name = drug.getString("name");
                String category = drug.getString("category");
                String chemicalFormula = drug.getString("chemicalFormula");
                String description = drug.getString("description");
                String relatedDrugs = drug.getString("relatedDrugs");

                // 把json数据赋给DrugInfo对象
                DrugInfo drugInfo = new DrugInfo(orderId, drugbankId, name, category, chemicalFormula, description, relatedDrugs);
                drugInfos.add(drugInfo);
            }
            return drugInfos;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
