package com.chl.couponapp.service;

import com.chl.couponapp.constant.Constant;
import com.chl.couponapp.domain.TCoupon;
import com.chl.couponapp.domain.TCouponExample;
import com.chl.couponapp.mapper.TCouponMapper;
import com.chl.userserviceapi.service.IUserService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Service
public class CouponService {

    //@Reference
    //private IUserService iUserService;

    @Resource
    private TCouponMapper tCouponMapper;

    /***
     * 获取有效时间的可用优惠券列表
     * @return
     */
    public List<TCoupon> getCouponList(){
        TCouponExample example = new TCouponExample();
        example.createCriteria().andStatusEqualTo(Constant.USERFUL)
                .andStartTimeLessThan(new Date()).andEndTimeGreaterThan(new Date());
        return tCouponMapper.selectByExample(example);
    }

    //public String getUserById(int id){
    //    return iUserService.getUserById(id).toString();
    //}

    public void print(){
        System.out.println("first service");
    }



}
