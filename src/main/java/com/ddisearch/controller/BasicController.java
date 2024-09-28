package com.ddisearch.controller;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.ddisearch.service.BasicService;

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

    // http://127.0.0.1:8080/search/111&222
    @GetMapping("/search/{drugAName}&{drugBName}")
    @ResponseBody
    public String handleSearch(@PathVariable String drugAName, @PathVariable String drugBName) {
        return basicService.handleSearch(drugAName, drugBName);
    }

    // http://127.0.0.1:8080/search/insert/drug
    @GetMapping("/insert/drug")
    @ResponseBody
    public String batchInsertDrugInfo() {
        return basicService.batchInsertDrugInfo();
    }

    // http://127.0.0.1:8080/search/insert/ddi
    @GetMapping("/insert/ddi")
    @ResponseBody
    public String batchInsertDDI() {
        return basicService.batchInsertDDI();
    }

    @GetMapping("/select/drug/{name}")
    @ResponseBody
    public String selectDrugInfoByName(@PathVariable String name) {
        return basicService.singleDrugSearch(name);
    }

    @GetMapping("/select/ddi/{drugAName}&{drugBName}")
    @ResponseBody
    public String selectDrugInfoByName(@PathVariable String drugAName, @PathVariable String drugBName) {
        return basicService.ddiSearch(drugAName, drugBName);
    }

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

}
