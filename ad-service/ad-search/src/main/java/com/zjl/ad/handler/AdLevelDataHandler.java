package com.zjl.ad.handler;

import com.alibaba.fastjson.JSON;
import com.zjl.ad.DataTable;
import com.zjl.ad.creativeunit.CreativeUnitIndex;
import com.zjl.ad.creativeunit.CreativeUnitObject;
import com.zjl.ad.dump.table.*;
import com.zjl.ad.index.IndexAware;
import com.zjl.ad.index.adplan.AdPlanIndex;
import com.zjl.ad.index.adplan.AdPlanObject;
import com.zjl.ad.index.adunit.AdUnitIndex;
import com.zjl.ad.index.adunit.AdUnitObject;
import com.zjl.ad.index.creative.CreativeIndex;
import com.zjl.ad.index.creative.CreativeObject;
import com.zjl.ad.index.district.UnitDistrictIndex;
import com.zjl.ad.index.interest.UnitItIndex;
import com.zjl.ad.index.keyword.UnitKeywordIndex;
import com.zjl.ad.mysql.constant.OpType;
import com.zjl.ad.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: JunLog
 * @Description: 处理对象 例如AdPlanTable->AdPlanObject
 * 索引之间存在着层级的划分, 也就是依赖关系的划分
 * 例如推广计划和创意不需要依赖别的 所以他们是做2级索引 下面依赖他们的就是三级索引等等
 * Date: 2022/7/29 14:50
 */
@Slf4j
public class AdLevelDataHandler {

    /**
     * 对推广单元unit的处理 AdUnitTable-》AdPlanObject
     * @param unitTable
     * @param type
     */
    public static void handleLevel3(AdUnitTable unitTable, OpType type) {

        // 先判断在二级索引中是否未空
        AdPlanObject adPlanObject = DataTable.of(
                AdPlanIndex.class
        ).get(unitTable.getPlanId());
        if (null == adPlanObject) {
            log.error("handleLevel3 found AdPlanObject error: {}",
                    unitTable.getPlanId());
            return;
        }

        AdUnitObject unitObject = new AdUnitObject(
                unitTable.getUnitId(),
                unitTable.getUnitStatus(),
                unitTable.getPositionType(),
                unitTable.getPlanId(),
                adPlanObject
        );

        handleBinlogEvent(
                DataTable.of(AdUnitIndex.class),
                unitTable.getUnitId(),
                unitObject,
                type
        );
    }

    /**
     * 对创意单元creatid和推广单元的关联的索引处理 AdCreativeUnitTable-》CreativeUnitObject
     * @param creativeUnitTable
     * @param type
     */
    public static void handleLevel3(AdCreativeUnitTable creativeUnitTable,
                                    OpType type) {

        if (type == OpType.UPDATE) {
            log.error("CreativeUnitIndex not support update");
            return;
        }

        // 先判断在二级索引中是否为空 因为这是关联的 需要判断两个
        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(creativeUnitTable.getUnitId());
        CreativeObject creativeObject = DataTable.of(
                CreativeIndex.class
        ).get(creativeUnitTable.getAdId());

        if (null == unitObject || null == creativeObject) {
            log.error("AdCreativeUnitTable index error: {}",
                    JSON.toJSONString(creativeUnitTable));
            return;
        }

        CreativeUnitObject creativeUnitObject = new CreativeUnitObject(
                creativeUnitTable.getAdId(),
                creativeUnitTable.getUnitId()
        );
        handleBinlogEvent(
                DataTable.of(CreativeUnitIndex.class),
                // 使用了字符串拼接在util工具中将创意id和推广单元id拼接起来做key
                CommonUtils.stringConcat(
                        creativeUnitObject.getAdId().toString(),
                        creativeUnitObject.getUnitId().toString()
                ),
                creativeUnitObject,
                type
        );
    }

    /**
     * 地域索引处理 四级索引
     * @param unitDistrictTable
     * @param type
     */
    public static void handleLevel4(AdUnitDistrictTable unitDistrictTable,
                                    OpType type) {

        if (type == OpType.UPDATE) {
            log.error("district index can not support update");
            return;
        }

        // 在三级索引里判断是否为空
        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(unitDistrictTable.getUnitId());
        if (unitObject == null) {
            log.error("AdUnitDistrictTable index error: {}",
                    unitDistrictTable.getUnitId());
            return;
        }

        // 同样这个省市是需要拼接的 同样使用util方法
        String key = CommonUtils.stringConcat(
                unitDistrictTable.getProvince(),
                unitDistrictTable.getCity()
        );
        Set<Long> value = new HashSet<>(
                Collections.singleton(unitDistrictTable.getUnitId())
        );
        handleBinlogEvent(
                DataTable.of(UnitDistrictIndex.class),
                key, value,
                type
        );
    }

    /**
     * 兴趣索引处理 四级索引
     * @param unitItTable
     * @param type
     */
    public static void handleLevel4(AdUnitItTable unitItTable, OpType type) {

        if (type == OpType.UPDATE) {
            log.error("it index can not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(unitItTable.getUnitId());
        if (unitObject == null) {
            log.error("AdUnitItTable index error: {}",
                    unitItTable.getUnitId());
            return;
        }

        Set<Long> value = new HashSet<>(
                Collections.singleton(unitItTable.getUnitId())
        );
        handleBinlogEvent(
                DataTable.of(UnitItIndex.class),
                unitItTable.getItTag(),
                value,
                type
        );
    }

    /**
     * 关键字索引 四级索引
     * @param keywordTable
     * @param type
     */
    public static void handleLevel4(AdUnitKeywordTable keywordTable,
                                    OpType type) {

        if (type == OpType.UPDATE) {
            log.error("keyword index can not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(keywordTable.getUnitId());
        if (unitObject == null) {
            log.error("AdUnitKeywordTable index error: {}",
                    keywordTable.getUnitId());
            return;
        }

        Set<Long> value = new HashSet<>(
                Collections.singleton(keywordTable.getUnitId())
        );
        handleBinlogEvent(
                DataTable.of(UnitKeywordIndex.class),
                keywordTable.getKeyword(),
                value,
                type
        );
    }


    /**
     * 对AdPlanTable->AdPlanObject的处理 二级索引
     * @param planTable
     * @param type
     */
    public static void handleLevel2(AdPlanTable planTable, OpType type) {

        // 将planTable填充到planObject
        AdPlanObject planObject = new AdPlanObject(
                planTable.getId(),
                planTable.getUserId(),
                planTable.getPlanStatus(),
                planTable.getStartDate(),
                planTable.getEndDate()
        );
        handleBinlogEvent(
                // 前面写好的索引服务的缓存
                DataTable.of(AdPlanIndex.class),
                planObject.getPlanId(),
                planObject,
                type
        );
    }

    /**
     * 对AdCreativeTable->CreativeObject的处理 二级索引
     * @param creativeTable
     * @param type
     */
    public static void handleLevel2(AdCreativeTable creativeTable,
                                    OpType type) {

        CreativeObject creativeObject = new CreativeObject(
                creativeTable.getAdId(),
                creativeTable.getName(),
                creativeTable.getType(),
                creativeTable.getMaterialType(),
                creativeTable.getHeight(),
                creativeTable.getWidth(),
                creativeTable.getAuditStatus(),
                creativeTable.getAdUrl()
        );
        handleBinlogEvent(
                DataTable.of(CreativeIndex.class),
                creativeObject.getAdId(),
                creativeObject,
                type
        );
    }


    private static <K, V> void handleBinlogEvent(
            IndexAware<K, V> index,
            K key,
            V value,
            OpType type) {

        switch (type) {
            case ADD:
                index.add(key, value);
                break;
            case UPDATE:
                index.update(key, value);
                break;
            case DELETE:
                index.delete(key, value);
                break;
            default:
                break;
        }
    }

}
