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
    @GetMapping("/search/{drugA}&{drugB}")
    @ResponseBody
    public String handleSearch(@PathVariable String drugA, @PathVariable String drugB) {
        return basicService.handleSearch(drugA, drugB);
    }

    // http://127.0.0.1:8080/search/insert
    @GetMapping("/search/insert")
    @ResponseBody
    public String batchInsertDrugInfo() {
        return basicService.batchInsertDrugInfo();
    }

    @GetMapping("/search/select/{name}")
    @ResponseBody
    public String selectDrugInfoByName(@PathVariable String name) {
        return basicService.selectDrugInfoByName(name);
    }



    // http://127.0.0.1:8080/hello?name=lisi
    @RequestMapping("/hello")
    @ResponseBody
    public String hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
        return "Hello " + name;
    }

    // http://127.0.0.1:8080/user
    @RequestMapping("/user")
    @ResponseBody
    public User user() {
        User user = new User();
        user.setName("theonefx");
        user.setAge(666);
        return user;
    }

    // http://127.0.0.1:8080/save_user?name=newName&age=11
    @RequestMapping("/save_user")
    @ResponseBody
    public String saveUser(User u) {
        return "user will save: name=" + u.getName() + ", age=" + u.getAge();
    }

    // http://127.0.0.1:8080/html
    @RequestMapping("/html")
    public String html() {
        return "index.html";
    }

    @ModelAttribute
    public void parseUser(@RequestParam(name = "name", defaultValue = "unknown user") String name
            , @RequestParam(name = "age", defaultValue = "12") Integer age, User user) {
        user.setName("zhangsan");
        user.setAge(18);
    }
}
