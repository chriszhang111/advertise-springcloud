package com.chris.ad.service.impl;

import com.chris.ad.Dao.AdUserRepository;
import com.chris.ad.constant.Constans;
import com.chris.ad.entity.AdUser;
import com.chris.ad.exception.AdException;
import com.chris.ad.service.IUserService;
import com.chris.ad.utils.CommonUtils;
import com.chris.ad.vo.CreateUserRequest;
import com.chris.ad.vo.CreateUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {


    private AdUserRepository userRepository;

    @Autowired
    public UserServiceImpl(AdUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) throws AdException {
        if(!request.validate()){
            throw new AdException(Constans.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        AdUser olduser = userRepository.findByUsername(request.getUsername());
        if(olduser != null){
            throw new AdException(Constans.ErrorMsg.SAME_NAME_ERROR);
        }

        AdUser newuser = userRepository.save(new AdUser(
                request.getUsername(),
                CommonUtils.md5(request.getUsername())
        ));

        return new CreateUserResponse(newuser.getId(),
                newuser.getUsername(),
                newuser.getToken(),
                newuser.getCreateTime(),
                newuser.getUpdateTime());
    }
}
