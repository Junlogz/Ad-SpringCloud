package com.zjl.ad.index.district;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: JunLog
 * @Description: *
 * Date: 2022/7/27 17:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDistrictObject {

    private Long unitId;
    private String province;
    private String city;

    // <String, Set<Long>>
    // province-city 省和市直接放一起
}
