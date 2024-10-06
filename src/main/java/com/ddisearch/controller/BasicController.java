package com.ddisearch.controller;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */

import com.ddisearch.entity.DDI;
import com.ddisearch.entity.DDITriplet;
import com.ddisearch.entity.Drug;
import com.ddisearch.entity.batchDDIResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.ddisearch.service.BasicService;

import java.io.*;
import java.util.*;

/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
@CrossOrigin
@RestController
public class BasicController {

    @Autowired
    private BasicService basicService;

    @Autowired
    public BasicController(BasicService basicService) {
        this.basicService = basicService;
    }


    // http://127.0.0.1:8080/LLM/DDI/No/111&222
    @GetMapping("/LLM/DDI/No/{drugAName}&{drugBName}")
    @ResponseBody
    public String notDDISearchLLM(@PathVariable String drugAName, @PathVariable String drugBName) {
        return "mock数据：" + drugAName + "和" + drugBName + "同时使用可能导致腹泻。";
    }

    // http://127.0.0.1:8080/LLM/DDI/Yes/111&222
    @GetMapping("/LLM/DDI/Yes/{drugAName}&{drugBName}")
    @ResponseBody
    public String yesDDISearchLLM(@PathVariable String drugAName, @PathVariable String drugBName) {
        return "mock数据：" + drugAName + "和" + drugBName + "之间存在联合作用的原因是XXXXX。";
    }

    // http://127.0.0.1:8080/pageSearch/index={index}&limit={limit}
    // http://127.0.0.1:8080/pageSearch/index=1&limit=10
    @GetMapping("/pageSearch/index={index}&limit={limit}")
    @ResponseBody
    public ArrayList<Map<String, String>> pagesSearch(@PathVariable int index, @PathVariable int limit) {
        return basicService.pagesSearch(index, limit);
    }

    @GetMapping("/loginVerify/{username}&{password}")
    @ResponseBody
    public String loginVerify(@PathVariable String username, @PathVariable String password) {
        if (Objects.equals(username, "root") && Objects.equals(password, "123456")) {
            return "yes";
        } else {
            return "no";
        }
    }

    // http://127.0.0.1:8080/search/111&222
    // 返回json格式数据
    @GetMapping("/search/{drugAName}&{drugBName}")
    @ResponseBody
    public Map<String, Object> handleSearchJson(@PathVariable String drugAName, @PathVariable String drugBName) {
        return basicService.handleSearch(drugAName, drugBName);
    }

    // http://127.0.0.1:8080/insert/drug
    @GetMapping("/insert/drug")
    @ResponseBody
    public String batchInsertDrugInfo() {
        return basicService.batchInsertDrugInfo();
    }

    // http://127.0.0.1:8080/insert/all
    @GetMapping("/insert/all")
    @ResponseBody
    public String batchInsertAllDrugInfoAndDDI() {
        return basicService.batchInsertAllDrugInfoAndDDI();
    }

    // http://127.0.0.1:8080/insert/ddi
    @GetMapping("/insert/ddi")
    @ResponseBody
    public String batchInsertDDI() {
        return basicService.batchInsertDDI();
    }

    // http://127.0.0.1:8080/write/drug/json
    @GetMapping("/write/drug/json")
    @ResponseBody
    public void writeDrugToJsonFiles() throws IOException {
        ArrayList<Drug> drugList = basicService.batchSelectAllDrug();
        // 创建 ObjectMapper 实例
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // 格式化输出

        // 准备两个 TreeMap 来存储数据并保持有序
        TreeMap<String, String> node2id = new TreeMap<>(Comparator.comparingInt(s -> Integer.parseInt(s.substring(2))));
        TreeMap<String, String> node2idReverse = new TreeMap<>(Comparator.comparingInt(s -> Integer.parseInt(s)));

        TreeMap<String, String> nameToDrugBankId = new TreeMap<>();
        TreeMap<String, String> drugBankIdToName = new TreeMap<>();

        TreeMap<String, String> nameToOrderId = new TreeMap<>();
        TreeMap<String, String> orderIdToName = new TreeMap<>(Comparator.comparingInt(s -> Integer.parseInt(s)));

        // 遍历 drugList 并填充两个 TreeMap
        for (Drug drug : drugList) {
            if (drug.getOrderId() != null && drug.getDrugbankId() != null) {
                node2id.put(drug.getDrugbankId(), drug.getOrderId());
                node2idReverse.put(drug.getOrderId(), drug.getDrugbankId());
                nameToDrugBankId.put(drug.getName(), drug.getDrugbankId());
                drugBankIdToName.put(drug.getDrugbankId(), drug.getName());
                nameToOrderId.put(drug.getName(), drug.getOrderId());
                orderIdToName.put(drug.getOrderId(), drug.getName());
            }
        }

        // 将 Map 转换为 JSON 字符串
        String node2idJson = objectMapper.writeValueAsString(node2id);
        String node2idReverseJson = objectMapper.writeValueAsString(node2idReverse);
        String nameToDrugBankIdJson = objectMapper.writeValueAsString(nameToDrugBankId);
        String drugBankIdToNameJson = objectMapper.writeValueAsString(drugBankIdToName);
        String nameToOrderIdJson = objectMapper.writeValueAsString(nameToOrderId);
        String orderIdToNameJson = objectMapper.writeValueAsString(orderIdToName);

        // 写入文件
        try (FileWriter fwNode2id = new FileWriter("src/main/java/com/ddisearch/data/All_node2id.json");
             FileWriter fwNode2idReverse = new FileWriter("src/main/java/com/ddisearch/data/All_node2id_reverse.json");
             FileWriter fwNameToDrugBankId = new FileWriter("src/main/java/com/ddisearch/data/All_name2drugbankid.json");
             FileWriter fwDrugBankIdToName = new FileWriter("src/main/java/com/ddisearch/data/All_drugbankid2name.json");
             FileWriter fwNameToOrderId = new FileWriter("src/main/java/com/ddisearch/data/All_name2orderid.json");
             FileWriter fwOrderIdToName = new FileWriter("src/main/java/com/ddisearch/data/All_orderid2name.json")
        ) {
            fwNode2id.write(node2idJson);
            fwNode2idReverse.write(node2idReverseJson);
            fwNameToDrugBankId.write(nameToDrugBankIdJson);
            fwDrugBankIdToName.write(drugBankIdToNameJson);
            fwNameToOrderId.write(nameToOrderIdJson);
            fwOrderIdToName.write(orderIdToNameJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // http://127.0.0.1:8080/write/ddi/txt
    @GetMapping("/write/ddi/txt")
    @ResponseBody
    public void writeDDIToTxtFiles() throws IOException {
        ArrayList<batchDDIResult> ddiList = basicService.batchSelectAllDDI();

        String fileName = "src/main/java/com/ddisearch/data/ALL_ddi.txt";
        String nameToIdFilePath = "src/main/java/com/ddisearch/data/All_name2drugbankid.json";
        String ddiTypeTemplateFilePath = "D:\\Java\\code\\DDI-Search\\src\\main\\java\\com\\ddisearch\\data\\ddiTypeTemplate.json";

        // 读取并缓存 JSON 文件内容
        JSONObject nameToIdJson = readJsonFromFile(nameToIdFilePath);
        JSONObject ddiTypeTemplateJson = readJsonFromFile(ddiTypeTemplateFilePath);
        int num = 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // 写入文件头
            writer.write("drug1\tdrug2\tLabel\n");
            ArrayList<DDITriplet> ddiTriplets = new ArrayList<>();
            for (batchDDIResult ddi : ddiList) {

                String drugAName = ddi.getDrugAName();
                String drugBName = ddi.getDrugBName();
                String description = ddi.getDdiDescription();
                String drugAId = null;
                String drugBId = null;
                String ddiType = null;

                // 从缓存的 JSON 中获取 drugAId 和 drugBId
                try {
                    drugAId = nameToIdJson.getString(drugAName);
                    drugBId = nameToIdJson.getString(drugBName);
//                    num++;
                } catch (Exception e) {
//                    num++;
//                    System.err.println("Failed to get drug IDs: " + drugAName + ", " + drugBName);
                    continue; // 跳过这条记录
                }

                String descriptionTemplate1 = description.replace(drugAName, "Drug a").replace(drugBName, "Drug b").toLowerCase();
                String descriptionTemplate2 = description.replace(drugBName, "Drug a").replace(drugAName, "Drug b").toLowerCase();

                // 查找匹配的 ddiType
                int flag = 0;
                for (String key : ddiTypeTemplateJson.keySet()) {
                    String value = ddiTypeTemplateJson.getString(key).toLowerCase();
                    if (value.equals(descriptionTemplate1) || value.equals(descriptionTemplate2)) {
                        ddiType = key;
                        flag = 1;
                        break;
                    }
                }

                if (flag == 0) {
                    ddiType = "-1";
                }

                if (!ddiType.equals("-1")) {
                    // 写入三元组
                    DDITriplet ddiTriplet = new DDITriplet(drugAId, drugBId, ddiType);
                    ddiTriplets.add(ddiTriplet);
                }
            }
            Collections.sort(ddiTriplets, new Comparator<DDITriplet>() {
                @Override
                public int compare(DDITriplet ddi1, DDITriplet ddi2) {
                    return Integer.compare(Integer.parseInt(ddi1.getDdiType()), Integer.parseInt(ddi2.getDdiType()));
                }
            });
            for(DDITriplet ddiTriplet : ddiTriplets){
                String drugAId = ddiTriplet.getDrugA();
                String drugBId = ddiTriplet.getDrugB();
                String ddiType = ddiTriplet.getDdiType();
                writer.write(drugAId + "\t" + drugBId + "\t" + ddiType);
                writer.newLine(); // 换行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject readJsonFromFile(String filePath) {
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            return new JSONObject(jsonBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
