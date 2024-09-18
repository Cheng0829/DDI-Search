package com.ddisearch.service.impl;

import com.ddisearch.service.BasicService;
import org.springframework.stereotype.Service;

@Service
public class BasicServiceImpl implements BasicService {
    public String handleSearch(String drugA, String drugB) {
        // drugA的不为空判断逻辑由前端控制
        if(drugB.equals("")) {
            return singleDrugSearch(drugA);
        } else {
            return twoDrugSearch(drugA, drugB);
        }
    }

    // 查找单个药物
    public String singleDrugSearch(String drugA) {
        String drugAInfo = drugInfoSearch(drugA);
        return "single: Drug A: " + drugA;
    }

    // 查找两个药物
    public String twoDrugSearch(String drugA, String drugB) {
        String drugAInfo = drugInfoSearch(drugA);
        String drugBInfo = drugInfoSearch(drugB);
        return "two: Drug A: " + drugA + ", Drug B: " + drugB;
    }

    // 查找单个药物信息
    public String drugInfoSearch(String drugA) {
        return "info: Drug A: " + drugA;
    }

    // 查找与药物有相互作用的其他药物
    public String drugRelatedSearch(String drugA) {
        return "interaction: Drug A: " + drugA;
    }

    // 查找两个药物之间的多种副作用
    public String ddiSearch(String drugA, String drugB) {
        return "two interaction: Drug A: " + drugA + ", Drug B: " + drugB;
    }

}
