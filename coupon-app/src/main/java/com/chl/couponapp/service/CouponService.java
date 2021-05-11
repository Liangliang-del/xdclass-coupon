package com.chl.couponapp.service;

import com.chl.userserviceapi.service.IUserService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Service
public class CouponService {

    @Reference
    private IUserService iUserService;

    public String getUserById(int id){
        return iUserService.getUserById(id).toString();
    }

    public void print(){
        System.out.println("first service");
    }



}
