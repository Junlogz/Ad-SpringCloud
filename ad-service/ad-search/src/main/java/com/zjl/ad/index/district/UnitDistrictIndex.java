package com.zjl.ad.index.district;

import com.zjl.ad.index.IndexAware;
import com.zjl.ad.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author: JunLog
 * @Description: 地域限制索引
 * Date: 2022/7/27 17:48
 */
@Slf4j
@Component
public class UnitDistrictIndex implements IndexAware<String, Set<Long>> {

    private static Map<String, Set<Long>> districtUnitMap;
    private static Map<Long, Set<String>> unitDistrictMap;

    static {
        districtUnitMap = new ConcurrentHashMap<>();
        unitDistrictMap = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Long> get(String key) {
        return districtUnitMap.get(key);
    }

    @Override
    public void add(String key, Set<Long> value) {
        log.info("UnitDistrictIndex, before add: {}", unitDistrictMap);

        Set<Long> unitIds = CommonUtils.getorCreate(key, districtUnitMap, ConcurrentSkipListSet::new);
        unitIds.addAll(value);

        for (Long unitId : value) {
            Set<String> districts = CommonUtils.getorCreate(unitId, unitDistrictMap, ConcurrentSkipListSet::new);
            districts.add(key);
        }

        log.info("UnitDistrictIndex, before add: {}", unitDistrictMap);
    }

    @Override
    public void update(String key, Set<Long> value) {
        log.error("district index can not support update");
    }

    @Override
    public void delete(String key, Set<Long> value) {

        log.info("UnitDistrictIndex, before delete: {}", unitDistrictMap);

        Set<Long> unitIds = CommonUtils.getorCreate(key, districtUnitMap, ConcurrentSkipListSet::new);
        unitIds.removeAll(value);

        for (Long unitId : value) {

            Set<String> districts = CommonUtils.getorCreate(unitId, unitDistrictMap, ConcurrentSkipListSet::new);
            districts.remove(key);
        }

        log.info("UnitDistrictIndex, after delete: {}", unitDistrictMap);
    }

}
