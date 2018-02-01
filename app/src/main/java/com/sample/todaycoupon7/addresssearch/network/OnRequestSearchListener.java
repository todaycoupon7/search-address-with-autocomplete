package com.sample.todaycoupon7.addresssearch.network;

import com.sample.todaycoupon7.addresssearch.dto.ResultsDto;

/**
 * Created by chaesooyang on 2018. 1. 31..
 */

public interface OnRequestSearchListener extends OnBaseRequestListener {
    void onCompleted(ResultsDto dto);
}
