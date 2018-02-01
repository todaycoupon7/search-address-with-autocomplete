package com.sample.todaycoupon7.addresssearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by chaesooyang on 2018. 1. 31..
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class RoadAddressDto {
    public String address_name;
    public String region_1depth_name;
    public String region_2depth_name;
    public String region_3depth_name;
    public String road_name;
    public String underground_yn;
    public String main_building_no;
    public String sub_building_no;
    public String building_name;
    public String zone_no;
    public String x;
    public String y;
}
