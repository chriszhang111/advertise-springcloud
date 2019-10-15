package com.chris.ad.service;

import com.chris.ad.exception.AdException;
import com.chris.ad.vo.AdCreativeRequest;
import com.chris.ad.vo.AdCreativeResponse;

public interface ICreativeService {

    AdCreativeResponse createCreative(AdCreativeRequest request) throws AdException;
}
