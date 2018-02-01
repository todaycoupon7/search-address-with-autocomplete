package com.sample.todaycoupon7.addresssearch.network;

import com.sample.todaycoupon7.addresssearch.AppMain;
import com.sample.todaycoupon7.addresssearch.dto.ResultsDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by chaesooyang on 2018. 1. 31..
 */

public class RequestSearch extends CancelableRequest {

    private final DataManager mDataMgr;

    private OnRequestSearchListener mListener;

    private String mRequestKey;
    private Call<ResultsDto> mCall;

    private interface SearchService {
        @GET("/v2/local/search/address.json")
        Call<ResultsDto> get(
                @Header("Authorization") String authorization,
                @Query("query") String query,
                @Query("page") int page);
    }

    public RequestSearch(DataManager dm) {
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

    public String get(String query, int page, OnRequestSearchListener listener) {
        mListener = listener;
        mRequestKey = mDataMgr.pushRequest(this);
        mCall = mDataMgr.getRetrofitForSearch().create(SearchService.class)
                .get("KakaoAK " + AppMain.getKakaoKey(), query, page);
        mCall.enqueue(new Callback<ResultsDto>() {
            @Override
            public void onResponse(Call<ResultsDto> call, Response<ResultsDto> response) {
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
            public void onFailure(Call<ResultsDto> call, Throwable t) {
                if(mListener != null) {
                    mListener.onError();
                }
                mDataMgr.popRequest(mRequestKey);
            }
        });


        return mRequestKey;
    }
}
