package com.sample.todaycoupon7.addresssearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by chaesooyang on 2018. 1. 31..
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MetaDto {
    public int total_count;
    public int pageable_count;
    public boolean is_end;
}
