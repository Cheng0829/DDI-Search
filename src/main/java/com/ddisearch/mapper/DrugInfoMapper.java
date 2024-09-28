package com.ddisearch.mapper;

import com.ddisearch.entity.Drug;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */

@Mapper
public interface DrugInfoMapper {
    void singleInsertDrugInfo(Drug drugInfos);
    void batchInsertDrugInfo(ArrayList<Drug> drugs);
    Drug selectDrugInfoByName(String name);
}

