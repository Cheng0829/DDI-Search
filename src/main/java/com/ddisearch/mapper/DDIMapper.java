package com.ddisearch.mapper;

import com.ddisearch.entity.DDI;
import com.ddisearch.entity.batchDDIResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Junkai Cheng
 * @date 2024/9/28 12:33
 */
@Mapper
public interface DDIMapper {
//    ArrayList<DDI> selectDDIByName(String drugAName, String drugBName);
    void batchInsertDDI(ArrayList<DDI> ddis);
    ArrayList<DDI> selectDDIByName(@Param("drugAName") String drugAName, @Param("drugBName") String drugBName);
    ArrayList<batchDDIResult> batchSelectDDI(@Param("offset") int offset, @Param("limit") int limit);
}
