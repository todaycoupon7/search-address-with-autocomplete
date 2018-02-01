package com.sample.todaycoupon7.addresssearch.support;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;

/**
 * Created by chaesooyang on 2018. 1. 31..
 */

public class AnimRecyclerView extends RecyclerView {

    public AnimRecyclerView(Context context) {
        super(context);
    }

    public AnimRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count) {
        if(getAdapter() != null && getLayoutManager() instanceof LinearLayoutManager) {
            LayoutAnimationController.AnimationParameters animationParams = params.layoutAnimationParameters;
            if(animationParams == null) {
                animationParams = new LayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParams;
            }

            animationParams.count = count;
            animationParams.index = index;
        } else {
            super.attachLayoutAnimationParameters(child, params, index, count);
        }
    }

}
