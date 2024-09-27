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

    public void singleInsertDrugInfo(){
        ArrayList<DrugInfo> drugInfos = JsonReader();
        DrugInfo drugInfo = drugInfos.get(0);
        drugInfoMapper.singleInsertDrugInfo(drugInfo);
    }

    public void batchInsertDrugInfo(){
        ArrayList<DrugInfo> drugInfos = JsonReader();
        drugInfoMapper.batchInsertDrugInfo(drugInfos);
    }

    public DrugInfo singleSelectDrugInfoByName(String name){
        return drugInfoMapper.selectDrugInfoByName(name);
    }

    public String handleSearch(String drugA, String drugB) {
//        singleInsertDrugInfo();
//        batchInsertDrugInfo();
        DrugInfo drugAInfo = singleSelectDrugInfoByName("Verteporfin");

        // drugA的不为空判断逻辑由前端控制
        if(drugB.isEmpty()) {
            return singleDrugSearch(drugA);
        } else {
            return twoDrugSearch(drugA, drugB);
        }
    }

    // 查找单个药物
    public String singleDrugSearch(String drugA) {
        String drugAInfo = drugInfoSearch(drugA);
        return "cjk";
    }

    // 查找两个药物
    public String twoDrugSearch(String drugA, String drugB) {
        String drugAInfo = drugInfoSearch(drugA);
        String drugBInfo = drugInfoSearch(drugB);
        String ddiInfo = ddiSearch(drugA, drugB);
        return "cjk";
    }

    // 查找药物信息
    public String drugInfoSearch(String drugName) {
        // 在drug_info_1710_crawl.json中通过药物英文名可以找到对应的drug信息
        String drugInfo = "cjk";
        return drugInfo;
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
