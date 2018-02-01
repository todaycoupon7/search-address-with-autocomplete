package com.sample.todaycoupon7.addresssearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by chaesooyang on 2018. 1. 31..
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AddressDto {
    public String address_name;
    public String region_1depth_name;
    public String region_2depth_name;
    public String region_3depth_name;
    public String region_3depth_h_name;
    public String h_code;
    public String b_code;
    public String mountain_yn;
    public String main_address_no;
    public String zip_code;
    public String x;
    public String y;
}
