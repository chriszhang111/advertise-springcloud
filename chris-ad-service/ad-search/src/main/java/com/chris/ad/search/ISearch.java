package com.chris.ad.search;

import com.chris.ad.search.vo.SearchRequest;
import com.chris.ad.search.vo.SearchResponse;

public interface ISearch {

    SearchResponse fetchAds(SearchRequest request);
}
