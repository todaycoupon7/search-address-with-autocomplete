package com.sample.todaycoupon7.addresssearch.network;

import com.sample.todaycoupon7.addresssearch.dto.AutocompleteDto;

/**
 * Created by chaesooyang on 2018. 1. 31..
 */

public interface OnRequestAutocompleteListener extends OnBaseRequestListener {
    void onCompleted(AutocompleteDto dto);
}
