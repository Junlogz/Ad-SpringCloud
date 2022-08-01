package com.zjl.ad.service;

import com.zjl.ad.exception.AdException;
import com.zjl.ad.vo.CreateUserRequest;
import com.zjl.ad.vo.CreateUserResponse;

/**
 *
 */
public interface IUserService {

    /**
     * <h2>创建用户</h2>
     * */
    CreateUserResponse createUser(CreateUserRequest request)
            throws AdException;
}
