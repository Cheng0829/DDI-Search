package com.ddisearch.mapper;

import java.util.ArrayList;
import com.ddisearch.entity.Drug;
import com.ddisearch.entity.batchDrugResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
@Mapper
public interface DrugInfoMapper {
    void batchInsertDrugInfo(ArrayList<Drug> drugs);
    Drug selectDrugInfoByName(String name);
    void batchInsertAllDrugInfo(ArrayList<Drug> drugs);
    ArrayList<Drug> batchSelectAllDrug();
    ArrayList<batchDrugResult> batchSelectDrug(@Param("offset") int offset, @Param("limit") int limit);
}
