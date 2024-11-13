package com.ddisearch.service;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
import com.ddisearch.entity.Drug;
import com.ddisearch.entity.DDI;
import java.util.*;

import com.ddisearch.entity.batchDDIResult;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public interface BasicService {
    Map<String, Object> handleDDISearch(String drugAName, String drugBName);
    Drug handleDrugSearch(String drugName);
    String batchInsertDrugInfo();
    String batchInsertDDI();
    ArrayList<Map<String, String>> pagesDDISearch(int index, int limit);
    ArrayList<Map<String, String>> pagesDrugSearch(int index, int limit);
    String batchInsertAllDrugInfoAndDDI();
    ArrayList<Drug> batchSelectAllDrug();
    ArrayList<batchDDIResult> batchSelectAllDDI();
}
