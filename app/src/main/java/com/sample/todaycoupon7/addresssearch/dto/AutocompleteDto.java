package com.sample.todaycoupon7.addresssearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by chaesooyang on 2018. 1. 31..
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AutocompleteDto {
    public String q;
    public String rq;
    public ArrayList<String> items;
    public ArrayList<String> r_items;
}
