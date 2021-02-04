package com.chl.couponapp;

import com.chl.couponapp.service.CouponService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CouponAppApplication.class)
class CouponAppApplicationTests {

    @Resource
    private CouponService couponService;

    @Test
    void contextLoads() {
        couponService.print();
        System.out.println("first Test method");
    }

}
