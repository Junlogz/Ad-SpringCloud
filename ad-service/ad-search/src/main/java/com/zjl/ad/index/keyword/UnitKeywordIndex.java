package com.zjl.ad.index.keyword;

import com.zjl.ad.index.IndexAware;
import com.zjl.ad.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author: JunLog
 * @Description: 关键词限制索引
 * Date: 2022/7/26 19:26
 */
@Slf4j
@Component
public class UnitKeywordIndex implements IndexAware<String, Set<Long>> {

    private static Map<String, Set<Long>> keywordUnitMap;
    private static Map<Long, Set<String>> unitKeywordMap;

    static {
        keywordUnitMap = new ConcurrentHashMap<>();
        unitKeywordMap = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Long> get(String key) {
        if (StringUtils.isEmpty((key))) {
            return Collections.emptySet();
        }
        Set<Long> result = keywordUnitMap.get(key);
        if (result == null) {
            return Collections.emptySet();
        }
        return result;
    }

    @Override
    public void add(String key, Set<Long> value) {

        log.info("UnitKeywordIndex, before add: {}", unitKeywordMap);

        Set<Long> unitIdSet = CommonUtils.getorCreate(key, keywordUnitMap, ConcurrentSkipListSet::new);
        unitIdSet.addAll(value);

        for (Long unitId : value) {
            Set<String> keywordSet = CommonUtils.getorCreate(unitId, unitKeywordMap, ConcurrentSkipListSet::new);
            keywordSet.add(key);
        }

        log.info("UnitKeywordIndex, after add: {}", unitKeywordMap);

    }

    @Override
    public void update(String key, Set<Long> value) {
        log.error("keyword index can not support update");
    }


    /**
     * 删除索引
     * 删除倒序的话 需要先拿unitIds 不能直接根据key进行remove，因为如果 key对应[1,2,3] 需要删除的是[1,2] 根据key将会把3也删除了
     * @param key
     * @param value
     */
    @Override
    public void delete(String key, Set<Long> value) {

        log.info("UnitKeywordIndex, before delete: {}", unitKeywordMap);

        Set<Long> unitids = CommonUtils.getorCreate(key, keywordUnitMap, ConcurrentSkipListSet::new);
        unitids.removeAll(value);

        for (Long unitId :
                value) {
            Set<String> keywordSet = CommonUtils.getorCreate(unitId, unitKeywordMap, ConcurrentSkipListSet::new);
            keywordSet.remove(key);
        }

        log.info("UnitKeywordIndex, after delete: {}", unitKeywordMap);

    }

    /**
     * 匹配某个推广单元是否包含这些关键词
     * @param unitId
     * @param keywords
     * @return
     */
    public boolean match(Long unitId, List<String> keywords) {
        // 先判断unitKeywordMap索引是否包含unitId再判断unitKeywordMap里面的unitId不为空
        if (unitKeywordMap.containsKey(unitId) && CollectionUtils.isNotEmpty(unitKeywordMap.get(unitId))) {
            Set<String> unitKeywords = unitKeywordMap.get((unitId));
            // 判断keywords是不是unitKeywords的子集
            return CollectionUtils.isSubCollection(keywords, unitKeywords);
        }
        return false;
    }
}
