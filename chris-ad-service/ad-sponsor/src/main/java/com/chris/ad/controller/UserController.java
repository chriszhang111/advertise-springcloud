package com.chris.ad.controller;

import com.alibaba.fastjson.JSON;
import com.chris.ad.exception.AdException;
import com.chris.ad.service.IUserService;
import com.chris.ad.vo.CreateUserRequest;
import com.chris.ad.vo.CreateUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {
    private IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create/user")
    public CreateUserResponse createUser(@RequestBody CreateUserRequest request) throws AdException{
        log.info("ad-sponsor:createUser->{}", JSON.toJSONString(request));

        return userService.createUser(request);
    }
}
