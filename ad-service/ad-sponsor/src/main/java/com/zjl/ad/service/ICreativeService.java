package com.zjl.ad.service;

import com.zjl.ad.vo.CreativeRequest;
import com.zjl.ad.vo.CreativeResponse;

/**
 *
 */
public interface ICreativeService {

    CreativeResponse createCreative(CreativeRequest request);
}
