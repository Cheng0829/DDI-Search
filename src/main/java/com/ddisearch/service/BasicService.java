package com.ddisearch.service;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
import com.ddisearch.entity.Drug;
import com.ddisearch.entity.DDI;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public interface BasicService {
    Map<String, Object> handleSearch(String drugAName, String drugBName);
    String batchInsertDrugInfo();
    String batchInsertDDI();
}
