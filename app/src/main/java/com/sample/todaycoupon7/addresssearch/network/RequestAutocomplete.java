package com.sample.todaycoupon7.addresssearch.network;

import com.sample.todaycoupon7.addresssearch.dto.AutocompleteDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by chaesooyang on 2018. 1. 31..
 */

public class RequestAutocomplete extends CancelableRequest {

    private final DataManager mDataMgr;

    private OnRequestAutocompleteListener mListener;

    private String mRequestKey;
    private Call<AutocompleteDto> mCall;

    private interface AutocompleteService {
        @GET("/suggest")
        Call<AutocompleteDto> get(
                @Query("q") String q,
                @Query("id") String id);
    }

    public RequestAutocomplete(DataManager dm) {
        mDataMgr = dm;
    }

    @Override
    public void cancel() {
        // listener
        mListener = null;
        // request
        if(mCall != null &&
                !mCall.isCanceled()) {
            mCall.cancel();
        }
    }

    public String get(String q, OnRequestAutocompleteListener listener) {
        mListener = listener;
        mRequestKey = mDataMgr.pushRequest(this);
        mCall = mDataMgr.getRetrofitForAutoComplete().create(AutocompleteService.class).get(q, "zipcode");
        mCall.enqueue(new Callback<AutocompleteDto>() {
            @Override
            public void onResponse(Call<AutocompleteDto> call, Response<AutocompleteDto> response) {
                if(mListener != null) {
                    if(response != null &&
                            response.isSuccessful()) {
                        mListener.onCompleted(response.body());
                    } else {
                        mListener.onError();
                    }
                }
                mDataMgr.popRequest(mRequestKey);
            }

            @Override
            public void onFailure(Call<AutocompleteDto> call, Throwable t) {
                if(mListener != null) {
                    mListener.onError();
                }
                mDataMgr.popRequest(mRequestKey);
            }
        });
        return mRequestKey;
    }
}
