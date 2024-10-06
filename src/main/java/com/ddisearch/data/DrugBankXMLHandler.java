package com.ddisearch.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ddisearch.entity.DDI;
import com.ddisearch.entity.Drug;
import com.ddisearch.mapper.DDIMapper;
import com.ddisearch.mapper.DrugInfoMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import static java.util.Collections.max;

class Property {
    private String kind;
    private String value;

    // Getters and Setters
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

public class DrugBankXMLHandler {
    public static long length = 0;
    public static ArrayList<DDI> ddiList = new ArrayList<>();

    public static void main(String[] args) {
        a();
    }

    public static ArrayList<Drug> a() {
        try {
            System.out.println("开始解析XML文件...");
            // 创建一个DocumentBuilderFactory对象
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 创建一个DocumentBuilder对象
            DocumentBuilder builder = factory.newDocumentBuilder();
            // 解析XML文件并获取Document对象
            Document document = builder.parse(new File("D:\\Study\\drugbank_all_full_database\\full database.xml"));

            // 获取根元素
            Element root = document.getDocumentElement();
            NodeList drugNodes = root.getElementsByTagName("drug");

            // 用于存储解析后的药物列表
            ArrayList<Drug> drugs = new ArrayList<>();
            ArrayList<Integer> orderIdList = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            System.out.println("开始处理元素...");
            System.out.println("Total number of drugs: " + drugNodes.getLength());
            System.out.print("Processed: ");
            for (int i = 0; i < drugNodes.getLength(); i++) {
                if (i % 100 == 0) {
                    System.out.print(i + ", ");
                    if (i % 1000 == 0) {
                        System.out.println("time: " + (int) ((System.currentTimeMillis() - startTime) / 1000) + "s.");
                    }
                }
                Element drugElement = (Element) drugNodes.item(i);
//                Drug drug = parseDrug(drugElement);
                ArrayList<Drug> drugsList = parseDrug(drugElement);
                if (drugsList.isEmpty()) {
                    continue;
                }
                if (drugsList.get(0).getOrderId().isEmpty()) {
                    continue;
                }
//                drugs.add(drug);
                drugs.addAll(drugsList);

                for (Drug drug : drugsList) {
                    String orderId = drug.getOrderId();
                    orderIdList.add(Integer.parseInt(orderId));
                }
            }

            for (int i = 1; i < max(orderIdList) - 1; i++) {
//                if(i % 1000 == 0){
//                    System.out.println("Processed " + i + " drugs.");
//                }
                if (!orderIdList.contains(i)) {
                    Drug drug = new Drug();
                    drug.setOrderId(String.valueOf(i));
//                    DB00006
                    int num = 100000 + i;
                    String drugbankId = "DB" + String.valueOf(num).substring(1);
                    drug.setDrugbankId(drugbankId);
                    drugs.add(drug);
                }
            }

            Collections.sort(drugs, new Comparator<Drug>() {
                @Override
                public int compare(Drug d1, Drug d2) {
                    return Integer.compare(Integer.parseInt(d1.getOrderId()), Integer.parseInt(d2.getOrderId()));
                }
            });
            System.out.println("解析结束");
            return drugs;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isChildOfExcludedElements(Node node) {
        Node parent = node.getParentNode();
        while (parent != null) {
            if (parent.getNodeName().equals("drug-interaction") || parent.getNodeName().equals("pathway") || parent.getNodeName().equals("reactions")) {
                return true;
            }
            parent = parent.getParentNode();
        }
        return false;
    }

    private static ArrayList<Drug> parseDrug(Element drugElement) {
        ArrayList<Drug> drugsList = new ArrayList<>();

        // 获取drugbank-id
        NodeList drugbankIdNodes = drugElement.getElementsByTagName("drugbank-id");

        List<String> drugbankIds = new ArrayList<>();
        for (int i = 0; i < drugbankIdNodes.getLength(); i++) {
            Node drugbankIdNode = drugbankIdNodes.item(i);
            String regex = "^DB\\d+$";
            // 创建 Pattern 对象
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
            // 创建 Matcher 对象
            java.util.regex.Matcher matcher = pattern.matcher(drugbankIdNode.getTextContent().trim());
            // 检查drugbank-id是否是drug-interaction的子节点
            if (matcher.matches() && !isChildOfExcludedElements(drugbankIdNode)) {
                String drugbankId = drugbankIdNode.getTextContent().trim();
                if (!drugbankIds.contains(drugbankId)) {
                    drugbankIds.add(drugbankId);
                }
            }
        }

        Drug drug = new Drug();
        String drugAName;
        // 获取name
        NodeList nameNodes = drugElement.getElementsByTagName("name");
        if (nameNodes.getLength() > 0) {
            drugAName = nameNodes.item(0).getTextContent().trim();
            drug.setName(drugAName);
        } else {
            drugAName = "";
        }

        // 获取description
        NodeList descriptionNodes = drugElement.getElementsByTagName("description");
        if (descriptionNodes.getLength() > 0) {
            drug.setDescription(descriptionNodes.item(0).getTextContent().trim().replace("\n", " "));
        }

        // 获取pharmacodynamics
        NodeList pharmacodynamicsNodes = drugElement.getElementsByTagName("pharmacodynamics");
        if (pharmacodynamicsNodes.getLength() > 0) {
            drug.setPharmacodynamics(pharmacodynamicsNodes.item(0).getTextContent().trim());
        }

        // 获取mechanism-of-action
        NodeList mechanismOfActionNodes = drugElement.getElementsByTagName("mechanism-of-action");
        if (mechanismOfActionNodes.getLength() > 0) {
            drug.setActionMechanism(mechanismOfActionNodes.item(0).getTextContent().trim());
        }

        // 获取metabolism
        NodeList metabolismNodes = drugElement.getElementsByTagName("metabolism");
        if (metabolismNodes.getLength() > 0) {
            drug.setMetabolism(metabolismNodes.item(0).getTextContent().trim());
        }

        // 获取protein-binding
        NodeList proteinBindingNodes = drugElement.getElementsByTagName("protein-binding");
        if (proteinBindingNodes.getLength() > 0) {
            drug.setProteinBinding(proteinBindingNodes.item(0).getTextContent().trim());
        }

        // 获取categories
        ArrayList<String> categories = new ArrayList<>();
        NodeList categoryNodes = drugElement.getElementsByTagName("category");
        for (int j = 0; j < categoryNodes.getLength(); j++) {
            Element categoryElement = (Element) categoryNodes.item(j);
            String category = categoryElement.getTextContent().trim();
            if (category.contains("\n") || categories.contains(category)) {
                continue;
            }
            categories.add(category);
            if (categories.size() >= Math.min(3, categoryNodes.getLength())) {
                break;
            }
        }
        drug.setCategory(String.join(", ", categories));

        // 获取calculated-properties
        NodeList calculatedPropertiesNodes = drugElement.getElementsByTagName("calculated-properties");
        if (calculatedPropertiesNodes.getLength() > 0) {
            NodeList propertyNodes = ((Element) calculatedPropertiesNodes.item(0)).getElementsByTagName("property");
            for (int j = 0; j < propertyNodes.getLength(); j++) {
                Element propertyElement = (Element) propertyNodes.item(j);
                Property property = new Property();
                property.setKind(propertyElement.getElementsByTagName("kind").item(0).getTextContent().trim());
                property.setValue(propertyElement.getElementsByTagName("value").item(0).getTextContent().trim());
                if ("SMILES".equals(property.getKind())) {
                    drug.setSmiles(property.getValue());
                    break;
                }
            }
        }
        // 获取experimental-properties
        NodeList experimentalPropertiesNodes = drugElement.getElementsByTagName("experimental-properties");
        if (experimentalPropertiesNodes.getLength() > 0) {
            NodeList propertyNodes = ((Element) experimentalPropertiesNodes.item(0)).getElementsByTagName("property");
            for (int j = 0; j < propertyNodes.getLength(); j++) {
                Element propertyElement = (Element) propertyNodes.item(j);
                Property property = new Property();
                property.setKind(propertyElement.getElementsByTagName("kind").item(0).getTextContent().trim());
                property.setValue(propertyElement.getElementsByTagName("value").item(0).getTextContent().trim());
                if ("Molecular Formula".equals(property.getKind())) {
                    drug.setChemicalFormula(property.getValue());
                    break;
                }
            }
        }

        // 获取drug-interactions
        NodeList drugInteractionsNodes = drugElement.getElementsByTagName("drug-interaction");
        ArrayList<String> relatedDrugsNameList = new ArrayList<>();
//        length = length + drugInteractionsNodes.getLength();
        for (int i = 0; i < drugInteractionsNodes.getLength(); i++) {
            Element interactionElement = (Element) drugInteractionsNodes.item(i);
            DDI ddi = new DDI();
            ddi.setDrugAName(drugAName);
            String drugBName = interactionElement.getElementsByTagName("name").item(0).getTextContent().trim();
            ddi.setDrugBName(drugBName);
            String description = interactionElement.getElementsByTagName("description").item(0).getTextContent().trim();
            ddi.setDescription(description);
            ddi.setDdiType(getDDITypeByDescription(drugAName, drugBName, description));
            ddi.setConfidence((float) Math.random());
            ddiList.add(ddi);
            if (relatedDrugsNameList.size() <= Math.min(5, relatedDrugsNameList.size())) {
                relatedDrugsNameList.add(ddi.getDrugBName());
            }
//            else {
//                break;
//            }
        }
        drug.setRelatedDrugs(String.join(", ", relatedDrugsNameList));

        for (String drugbankId : drugbankIds) {
            Drug everySameDrug = new Drug();
            everySameDrug.setOrderId(drugbankId);
            everySameDrug.setDrugbankId(drugbankId);
            everySameDrug.setName(drug.getName());
            everySameDrug.setDescription(drug.getDescription());
            everySameDrug.setCategory(drug.getCategory());
            everySameDrug.setChemicalFormula(drug.getChemicalFormula());
            everySameDrug.setSmiles(drug.getSmiles());
            everySameDrug.setPharmacodynamics(drug.getPharmacodynamics());
            everySameDrug.setActionMechanism(drug.getActionMechanism());
            everySameDrug.setProteinBinding(drug.getProteinBinding());
            everySameDrug.setMetabolism(drug.getMetabolism());
            everySameDrug.setRelatedDrugs(drug.getRelatedDrugs());
            drugsList.add(everySameDrug);
        }

        return drugsList;
    }

    private static String getDDITypeByDescription(String drugAName, String drugBName, String description) {
        String descriptionTemplate1 = description.replace(drugAName, "Drug a").replace(drugBName, "Drug b").toLowerCase();
        String descriptionTemplate2 = description.replace(drugBName, "Drug a").replace(drugAName, "Drug b").toLowerCase();
        String filePath = "D:\\Java\\code\\DDI-Search\\src\\main\\java\\com\\ddisearch\\data\\ddiTypeTemplate.json";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            // 将文件内容转换为 JSONObject
            JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
            for (String key : jsonObject.keySet()) {
                String value = jsonObject.getString(key).toLowerCase();
                if (value.equals(descriptionTemplate1) || value.equals(descriptionTemplate2)) {
                    return key;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }
}