package com.chl.couponapp.service.schedule;

import com.chl.couponapp.service.CouponService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author admin
 */
@Service
public class UpdateCouponJob {

    @Resource
    private CouponService couponService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void updateCoupon(){
        System.out.println("enter updateCoupon Job");
        couponService.updateCouponMap();
    }
}
