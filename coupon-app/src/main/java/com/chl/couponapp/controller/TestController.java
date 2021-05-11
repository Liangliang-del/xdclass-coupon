package com.chl.couponapp.controller;

import com.chl.couponapp.service.CouponService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class TestController {

    @Resource
    private CouponService couponService;

    @RequestMapping("/test")
    public String test(){
        return "test";
    }

    @RequestMapping("/getUser")
    public String getUser(){
        return couponService.getUserById(5);
    }

}
