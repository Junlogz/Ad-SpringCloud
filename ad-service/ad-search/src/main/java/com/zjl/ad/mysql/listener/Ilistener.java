package com.zjl.ad.mysql.listener;

import com.zjl.ad.mysql.dto.BinlogRowData;

/**
 * 对binlog进行操作 例如增量索引的更新
 */
public interface Ilistener {

    // 对不同的表进行操作 注册不同的监听器
    void register();

    void onEvent(BinlogRowData eventData);
}
