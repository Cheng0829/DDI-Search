package com.ddisearch.controller;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
import com.ddisearch.entity.DDI;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.ddisearch.service.BasicService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
         if (Objects.equals(username, "root") && Objects.equals(password, "123456")){
             return "yes";
         }
         else{
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

    // http://127.0.0.1:8080/insert/ddi
    @GetMapping("/insert/ddi")
    @ResponseBody
    public String batchInsertDDI() {
        return basicService.batchInsertDDI();
    }
}
