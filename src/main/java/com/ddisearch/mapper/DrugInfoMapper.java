package com.ddisearch.mapper;

import com.ddisearch.entity.DrugInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */

@Mapper
public interface DrugInfoMapper {
    void singleInsertDrugInfo(DrugInfo drugInfos);
    void batchInsertDrugInfo(ArrayList<DrugInfo> drugInfos);
    DrugInfo selectDrugInfoByName(String name);
}

