package com.ddisearch.mapper;

import com.ddisearch.entity.DDI;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

/**
 * @author Junkai Cheng
 * @date 2024/9/28 12:33
 */
@Mapper
public interface DDIMapper {
//    ArrayList<DDI> selectDDIByName(String drugAName, String drugBName);
    void batchInsertDDI(ArrayList<DDI> ddis);
    ArrayList<DDI> selectDDIByName(@Param("drugAName") String drugAName, @Param("drugBName") String drugBName);
}
