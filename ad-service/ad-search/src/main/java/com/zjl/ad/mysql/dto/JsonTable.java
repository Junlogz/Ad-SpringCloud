package com.zjl.ad.mysql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  对应模板文件中的表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonTable {

    private String tableName;
    private Integer level;

    private List<Column> insert;
    private List<Column> update;
    private List<Column> delete;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Column {

        private String column;
    }
}
