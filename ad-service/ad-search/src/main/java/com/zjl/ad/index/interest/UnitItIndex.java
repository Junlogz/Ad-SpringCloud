package com.zjl.ad.index.interest;

import com.zjl.ad.index.IndexAware;
import com.zjl.ad.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author: JunLog
 * @Description: 兴趣限制索引
 * Date: 2022/7/27 16:05
 */
@Slf4j
@Component
public class UnitItIndex implements IndexAware<String, Set<Long>> {

    // 正向索引 例如关键字a -> [1,2,3]
    private static Map<String, Set<Long>> itUnitMap;

    // 方向索引 例如1 -> [a, dd, ccc]
    private static Map<Long, Set<String>> unitItMap;

    @Override
    public Set<Long> get(String key) {
        return itUnitMap.get(key);
    }

    @Override
    public void add(String key, Set<Long> value) {
        log.info("UnitItIndex, before add: {}", unitItMap);

        Set<Long> unitIds = CommonUtils.getorCreate(key, itUnitMap, ConcurrentSkipListSet::new);
        unitIds.addAll(value);

        for (Long unitId : value) {
            Set<String> its = CommonUtils.getorCreate(unitId, unitItMap, ConcurrentSkipListSet::new);
            its.add(key);
        }

        log.info("UnitItIndex, after add: {}", unitItMap);
    }

    @Override
    public void update(String key, Set<Long> value) {
        log.error("it index can not support update");
    }

    /**
     * 删除索引
     * 删除倒序的话 需要先拿unitIds 不能直接根据key进行remove，因为如果 key对应[1,2,3] 需要删除的是[1,2] 根据key将会把3也删除了
     * @param key
     * @param value
     */
    @Override
    public void delete(String key, Set<Long> value) {

        log.info("UnitItIndex, before delete: {}", unitItMap);

        Set<Long> unitIds = CommonUtils.getorCreate(key, itUnitMap, ConcurrentSkipListSet::new);
        unitIds.removeAll(value);

        for (Long unitId :
                value) {
            Set<String> itTagSet = CommonUtils.getorCreate(unitId, unitItMap, ConcurrentSkipListSet::new);
            itTagSet.remove(key);
        }

        log.info("UnitItIndex, after delete: {}", unitItMap);
    }


    /**
     * 匹配某个推广单元是否包含这些关键词
     * @param unitId
     * @param itTags
     * @return
     */
    public boolean match(Long unitId, List<String> itTags) {
        if (unitItMap.containsKey(unitId) && CollectionUtils.isNotEmpty(unitItMap.get(unitId))) {
            Set<String> unitKeywords = unitItMap.get(unitId);
            return CollectionUtils.isSubCollection(itTags, unitKeywords);
        }
        return false;
    }
}
