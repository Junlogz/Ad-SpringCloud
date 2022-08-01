package com.zjl.ad.client;

import com.zjl.ad.client.vo.AdPlan;
import com.zjl.ad.client.vo.AdPlanGetRequest;
import com.zjl.ad.vo.CommonResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Component
public class SponsorClientHystrix implements SponsorClient {

    @Override
    public CommonResponse<List<AdPlan>> getAdPlans(AdPlanGetRequest request) {
        return new CommonResponse<>(-1, "eureka-client-ad-sponsor error");
    }
}
