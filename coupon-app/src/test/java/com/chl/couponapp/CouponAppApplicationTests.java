package com.chl.couponapp;

import com.chl.couponapp.domain.TCoupon;
import com.chl.couponapp.domain.TCouponExample;
import com.chl.couponapp.mapper.TCouponMapper;
import com.chl.couponapp.service.CouponService;
import com.chl.couponserviceapi.model.UserCouponModel;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CouponAppApplication.class)
class CouponAppApplicationTests {

    @Resource
    private CouponService couponService;

    @Resource
    private TCouponMapper tCouponMapper;

    @Test
    void contextLoads() {
        couponService.print();
        System.out.println("first Test method");
    }

    @Test
    public void insertCoupon(){
        TCoupon tCoupon = new TCoupon();
        tCoupon.setAchieveAmount(500);
        tCoupon.setReduceAmount(50);
        tCoupon.setCreateTime(new Date());
        tCoupon.setCode(UUID.randomUUID().toString());
        tCoupon.setPicUrl("1.png");
        tCoupon.setStock(10);
        tCoupon.setTitle("满500减50");
        tCoupon.setStatus(0);
        tCouponMapper.insert(tCoupon);
    }

    @Test
    public void delete(){
        tCouponMapper.deleteByPrimaryKey(1);
    }

    @Test
    public void update(){
        TCoupon tCoupon = new TCoupon();
        tCoupon.setId(1);
        tCoupon.setCode("9527");
        tCouponMapper.updateByPrimaryKeySelective(tCoupon);
        //tCouponMapper.updateByPrimaryKey(tCoupon);
    }


    @Test
    public void select(){
        // select * from t_coupon where code = "00415d96-49bd-4cce-83e3-08302b9aa084" and status=0 and achieve_amount between (100,1000) and title not like '%111%';
        TCouponExample example = new TCouponExample();
        example.createCriteria().andCodeEqualTo("9527").andStatusEqualTo(0)
                .andAchieveAmountBetween(100,1000).andTitleNotLike("111");
        List<TCoupon> tCoupon =  tCouponMapper.selectByExample(example);
        System.err.println(tCoupon);
    }

    @Test
    public void testQuery(){
        List<TCoupon> tCoupon = couponService.getCouponList();
        System.out.println(tCoupon);
    }

    @Test
    public void testSaveUserCoupon(){
        UserCouponModel dto = new UserCouponModel();
        dto.setUserId(1234);
        dto.setCouponId(1);
        dto.setOrderId(10086);
        System.err.println(couponService.saveUserCoupon(dto));
    }

}
