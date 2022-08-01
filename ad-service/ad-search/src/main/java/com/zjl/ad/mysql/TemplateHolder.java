package com.zjl.ad.mysql;

import com.alibaba.fastjson.JSON;
import com.zjl.ad.mysql.constant.OpType;
import com.zjl.ad.mysql.dto.ParseTemplate;
import com.zjl.ad.mysql.dto.TableTemplate;
import com.zjl.ad.mysql.dto.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @author: JunLog
 * @Description: *
 * Date: 2022/7/30 14:08
 */
@Slf4j
@Component
public class TemplateHolder {

    private ParseTemplate template;
    private final JdbcTemplate jdbcTemplate;

    private String SQL_SCHEMA = "select table_schema, table_name, " +
            "column_name, ordinal_position from information_schema.columns " +
            "where table_schema = ? and table_name = ?";

    @Autowired
    public TemplateHolder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    private void init() {
        loadJson("template.json");
    }

    // 提供对外服务
    public TableTemplate getTable(String tableName) {
        return template.getTableTemplateMap().get(tableName);
    }

    // 载入解析模板文件
    private void loadJson(String path) {

        // 当前线程上下文
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        // 获取输入流
        InputStream inStream = cl.getResourceAsStream(path);

        try {
            Template template = JSON.parseObject(
                    inStream,
                    Charset.defaultCharset(),
                    Template.class
            );
            this.template = ParseTemplate.parse(template);
            loadMeta();
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException("fail to parse json file");
        }
    }


    private void loadMeta() {
        // 对每张表进行for循环
        for (Map.Entry<String, TableTemplate> entry : template.getTableTemplateMap().entrySet()) {

            TableTemplate table = entry.getValue();

            // 对应各个类型 各个列的自身字段
            /**
             *  "insert": [
             *         {"column": "id"},
             *         {"column": "user_id"},
             *         {"column": "plan_status"},
             *         {"column": "start_date"},
             *         {"column": "end_date"}
             *       ],
             */
            List<String> updateFields = table.getOpTypeFieldSetMap().get(
                    OpType.UPDATE
            );
            List<String> insertFields = table.getOpTypeFieldSetMap().get(
                    OpType.ADD
            );
            List<String> deleteFields = table.getOpTypeFieldSetMap().get(
                    OpType.DELETE
            );

            // 实现查询
            /** TABLE_SCHEMA       TABLE_NAME         COLUMN_NAME       ORDINAL_POSITION
             *      ad	       ad_unit_keyword	        id	                  1
             *      ad	       ad_unit_keyword	        unit_id	              2
             *      ad	       ad_unit_keyword	        keyword	              3
             */

            jdbcTemplate.query(SQL_SCHEMA, new Object[]{
                    template.getDatabase(), table.getTableName()
            }, (rs, i) -> {

                // 对应上面注释上表查询的拿结果
                int pos = rs.getInt("ORDINAL_POSITION");
                String colName = rs.getString("COLUMN_NAME");

                if ((null != updateFields && updateFields.contains(colName))
                        || (null != insertFields && insertFields.contains(colName))
                        || (null != deleteFields && deleteFields.contains(colName))) {
                    table.getPosMap().put(pos - 1, colName);
                    // 每张表的schema语句 拿到列数和列明 实现了索引对列的映射
                }

                return null;
            });
        }
    }
}
