package com.sample.todaycoupon7.addresssearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by chaesooyang on 2018. 1. 31..
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DocumentDto {
    public String address_name;
    public String x;
    public String y;
    public String address_type;
    public AddressDto address;
    public RoadAddressDto road_address;
}
