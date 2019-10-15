package com.chris.ad.service.impl;

import com.chris.ad.Dao.AdUserRepository;
import com.chris.ad.Dao.CreativeRepository;
import com.chris.ad.constant.Constans;
import com.chris.ad.entity.AdUser;
import com.chris.ad.entity.Creative;
import com.chris.ad.exception.AdException;
import com.chris.ad.service.ICreativeService;
import com.chris.ad.vo.AdCreativeRequest;
import com.chris.ad.vo.AdCreativeResponse;
import jdk.nashorn.internal.runtime.options.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CreativeServiceImpl implements ICreativeService{

    private CreativeRepository creativeRepository;
    private AdUserRepository userRepository;

    public CreativeServiceImpl(CreativeRepository creativeRepository, AdUserRepository userRepository) {
        this.creativeRepository = creativeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AdCreativeResponse createCreative(AdCreativeRequest request) throws AdException{

        Optional<AdUser> user = userRepository.findById(request.getUserId());
        if(!user.isPresent())
            throw new AdException(Constans.ErrorMsg.REQUEST_PARAM_ERROR);
        Creative creative = creativeRepository.save(
                request.convertToEntity()
        );
        return new AdCreativeResponse(creative.getId(), creative.getName());
    }
}
