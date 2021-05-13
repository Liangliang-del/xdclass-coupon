package com.chl.couponapp.service;

import com.chl.couponapp.constant.Constant;
import com.chl.couponapp.domain.TCoupon;
import com.chl.couponapp.domain.TCouponExample;
import com.chl.couponapp.mapper.TCouponMapper;
import com.chl.userserviceapi.service.IUserService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Service
public class CouponService {

    //@Reference
    //private IUserService iUserService;

    @Resource
    private TCouponMapper tCouponMapper;

    LoadingCache<Integer, List<TCoupon>> couponCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<Integer, List<TCoupon>>() {
                @Override
                public List<TCoupon> load(Integer o) throws Exception {
                    return loadCoupon(o);
                }
            });

    LoadingCache<Integer, TCoupon> couponIdsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<Integer, TCoupon>() {
                @Override
                public TCoupon load(Integer o) throws Exception {
                    return loadIdCoupon(o);
                }
            });

    private Map couponMap = new ConcurrentHashMap();

    public void updateCouponMap(){
        Map couponMapOne = new ConcurrentHashMap();
        List<TCoupon> tCouponList = this.loadCoupon(1);
        couponMapOne.put(1, tCouponList);
        couponMap = couponMapOne;
    }

    /***
     * 获取有效时间的可用优惠券列表
     * @return
     */
    public List<TCoupon> getCouponList4Map(){
        return (List<TCoupon>)couponMap.get(1);
    }

    private TCoupon loadIdCoupon(Integer id) {
        return tCouponMapper.selectByPrimaryKey(id);
    }

    private List<TCoupon> loadCoupon(Integer o) {
        TCouponExample example = new TCouponExample();
        example.createCriteria().andStatusEqualTo(Constant.USERFUL)
                .andStartTimeLessThan(new Date()).andEndTimeGreaterThan(new Date());
        return tCouponMapper.selectByExample(example);
    }

    /***
     * 获取有效时间的可用优惠券列表
     * @return
     */
    public List<TCoupon> getCouponList(){
        List<TCoupon> couponList = Lists.newArrayList();
        try {
            couponList = couponCache.get(1);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return couponList;
    }

    public List<TCoupon> getCouponListByIds(String ids){
        String[] idStr = ids.split(",");
        List<Integer> loadFromDB = Lists.newArrayList();
        List<TCoupon> tCouponList = Lists.newArrayList();
        List<String> idList = Lists.newArrayList(idStr);
        for ( String id : idList ) {
            TCoupon tCoupon = couponIdsCache.getIfPresent(id);
            if(null == tCoupon){
                loadFromDB.add(Integer.parseInt(id));
            }else{
                tCouponList.add(tCoupon);
            }
        }
        List<TCoupon> tCouponsDB = couponByIds(loadFromDB);
        Map<Integer, TCoupon> tCouponMap = tCouponsDB.stream().collect(Collectors.toMap(TCoupon::getId, tCoupon -> tCoupon));
        //将返回结果会写到缓存里面
        couponIdsCache.putAll(tCouponMap);
        tCouponList.addAll(tCouponsDB);
        return tCouponList;
    }

    private List<TCoupon> couponByIds(List<Integer> ids) {
        TCouponExample example = new TCouponExample();
        example.createCriteria().andIdIn(ids);
        return tCouponMapper.selectByExample(example);
    }

    //public String getUserById(int id){
    //    return iUserService.getUserById(id).toString();
    //}

    public void print(){
        System.out.println("first service");
    }



}
