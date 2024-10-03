package com.ddisearch.data;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

// 定义一个 DrugBankXMLHandler 类,继承自 DefaultHandler
public class DrugBankXMLHandler extends DefaultHandler {

    // 布尔变量,用于标记是否当前正在处理 drug 元素
    private boolean inDrug = false;

    // 字符串变量,用于存储当前正在处理的药品名称
    private String currentDrugName;

    // 一些变量用于计算时间
    private static int interaction_num = 0;
    private static int interactions_num = 0;
    private static int total_num = 0;
    private static int drug_num = 0;
    private static int iteration = 0;
    long startTime = System.nanoTime(); // 获取开始时间戳
    long endTime; // 获取结束时间戳
    long totalTime = 0; // 计算总时长

    // 重写 startElement 方法,用于处理元素的开始事件
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // 打印出元素的 URI、本地名称、限定名称和属性
//        System.out.println(uri + "," + localName + "," + qName + "," + attributes);

        // 如果当前元素是 drug 元素,设置 inDrug 为 true
        if (qName.equals("drug")) {
            inDrug = true;
        }
        // 如果当前元素是 name 元素,且正在处理 drug 元素,则初始化 currentDrugName
        else if (inDrug && qName.equals("name")) {
            currentDrugName = "";
        }
    }

    // 重写 endElement 方法,用于处理元素的结束事件
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // 如果当前元素是 drug 元素
        if (qName.equals("drug")) {
            drug_num++;
            inDrug = false; // 设置 inDrug 为 false
//            System.out.println("Drug Name: " + currentDrugName); // 打印出药品名称
//            if (iteration++%1000 == 0) {
//                endTime = System.nanoTime(); // 获取结束时间戳
//                long singleRoundTime = (endTime - startTime) / 1000000000; // 秒
//                totalTime = totalTime + singleRoundTime;
//                System.out.print("iteration="+(iteration-1)+", "+"iteration: "+singleRoundTime+"s, total time: "+totalTime+"s\n");
//                startTime = System.nanoTime(); // 获取开始时间戳
//            }
        }
        if (qName.equals("drug-interaction")) {
            interaction_num++;
        }
        if (qName.equals("drug-interactions")) {
            interactions_num++;
        }
        if(total_num++%1000000==0){
            endTime = System.nanoTime(); // 获取结束时间戳
            long singleRoundTime = (endTime - startTime) / 1000000000; // 秒
            totalTime = totalTime + singleRoundTime;
            System.out.print("iteration="+((total_num-1)+", "+"time: "+singleRoundTime+"s, total time: "+totalTime+"s\n"));
            startTime = System.nanoTime(); // 获取开始时间戳
        }
    }

    // 重写 characters 方法,用于处理元素中的字符数据
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // 如果正在处理 drug 元素,则将字符数据追加到 currentDrugName
        if (inDrug) {
            currentDrugName += new String(ch, start, length);
        }
    }

    public static void main(String[] args) {
        String xmlFilePath = "D:\\Study\\drugbank_all_full_database\\full database.xml"; // 指定 XML 文件路径

        try {
            // 创建 SAXParserFactory 实例
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // 通过 SAXParserFactory 创建 SAXParser 实例
            SAXParser saxParser = factory.newSAXParser();
            // 创建 DrugBankXMLHandler 实例
            DrugBankXMLHandler handler = new DrugBankXMLHandler();
            // 调用 SAXParser 的 parse 方法,传入 XML 文件路径和事件处理器,开始解析 XML 文件
            saxParser.parse(xmlFilePath, handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // 捕获异常并打印堆栈跟踪
            e.printStackTrace();
        }
        System.out.println("\n总条目数："+total_num); // 总条目数：29846478
        System.out.println("DDI总数："+interaction_num); // DDI总数：2839610
        System.out.println("有DDI的药品数量："+interactions_num); // 有DDI的药品数量：16581
        System.out.println("药品数量："+drug_num); // 药品数量：72838
    }
}