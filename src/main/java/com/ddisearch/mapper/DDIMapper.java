package com.ddisearch.mapper;

import java.util.ArrayList;
import com.ddisearch.entity.DDI;
import com.ddisearch.entity.batchDDIResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Junkai Cheng
 * @date 2024/9/28 12:33
 */
@Mapper
public interface DDIMapper {
    void batchInsertDDI(ArrayList<DDI> ddis);
    ArrayList<DDI> selectDDIByName(@Param("drugAName") String drugAName, @Param("drugBName") String drugBName);
    ArrayList<batchDDIResult> batchSelectDDI(@Param("offset") int offset, @Param("limit") int limit);
    void batchInsertAllDDI(ArrayList<DDI> ddis);
    ArrayList<batchDDIResult> batchSelectAllDDI();
}
