package com.zjl.ad.mysql.dto;

import com.zjl.ad.mysql.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableTemplate {

    private String tableName;
    private String level;

    /**
     * 操作类型对应的列
     *  "insert": [
     *         {"column": "id"},
     *         {"column": "user_id"},
     *         {"column": "plan_status"},
     *         {"column": "start_date"},
     *         {"column": "end_date"}
     *       ],
     */
    private Map<OpType, List<String>> opTypeFieldSetMap = new HashMap<>();

    /**
     * 字段索引 -> 字段名
     * */
    private Map<Integer, String> posMap = new HashMap<>();
}
