package com.chl.couponserviceapi.service;

import com.chl.couponserviceapi.model.UserCouponModel;

/**
 * @author admin
 */
public interface ICouponService {

    /***
     * 获取有效时间的可用优惠券列表
     * // 1、是否存在远程调用 HTTP、RPC Metrics
     * // 2、大量内存处理  list.contain() ==>set.contain
     * @return
     */
    //public List<TCoupon> getCouponList();

    /**
     * 用户领取优惠劵
     * @param userCouponModel
     * @return
     */
    public String saveUserCoupon(UserCouponModel userCouponModel);
}
