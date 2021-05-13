package com.chl.couponapp.service.schedule;

import com.chl.couponapp.service.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author admin
 */
@Service
public class UpdateCouponJob {

    private static final Logger logger = LoggerFactory.getLogger(UpdateCouponJob.class);

    @Resource
    private CouponService couponService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void updateCoupon(){
        logger.info("enter updateCoupon Job");
        couponService.updateCouponMap();
    }
}
