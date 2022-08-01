package com.zjl.ad.mysql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  模板 database tableList
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Template {

    private String database;
    private List<JsonTable> tableList;
}
