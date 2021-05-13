package com.chl.couponapp.service.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Service
public class HelloSchedule {

    //@Scheduled(cron = "0/10 * * * * ?")
    public void hello(){
        System.out.println("HelloSchedule...");
    }
}
