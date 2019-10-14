package com.chris.ad.service;

import com.chris.ad.exception.AdException;
import com.chris.ad.vo.CreateUserRequest;
import com.chris.ad.vo.CreateUserResponse;

public interface IUserService {

    CreateUserResponse createUser(CreateUserRequest request) throws AdException;
}
