package com.chl.couponapp.service;

import com.alibaba.fastjson.JSON;
import com.chl.couponapp.constant.Constant;
import com.chl.couponapp.domain.TCoupon;
import com.chl.couponapp.domain.TCouponExample;
import com.chl.couponapp.domain.TUserCoupon;
import com.chl.couponapp.mapper.TCouponMapper;
import com.chl.couponapp.mapper.TUserCouponMapper;
import com.chl.couponapp.util.SnowflakeIdWorker;
import com.chl.couponserviceapi.model.UserCouponModel;
import com.chl.couponserviceapi.service.ICouponService;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class CouponService implements ICouponService {

    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);

    //@Reference
    //private IUserService iUserService;

    @Resource
    private TCouponMapper tCouponMapper;

    @Resource
    private TUserCouponMapper tUserCouponMapper;

    com.github.benmanes.caffeine.cache.LoadingCache<Integer, List<TCoupon>> couponCaffeine = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(new com.github.benmanes.caffeine.cache.CacheLoader<Integer, List<TCoupon>>() {
                @Override
                public List<TCoupon> load(Integer o) throws Exception {
                    return loadCoupon(o);
                }
            });

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
        List<TCoupon> tCouponList = Lists.newArrayList();
        try {
            tCouponList = this.loadCoupon(1);
            couponMapOne.put(1, tCouponList);
            couponMap = couponMapOne;
            logger.info("updateCouponList:{}, size:{}", JSON.toJSONString(tCouponList), tCouponList.size());
        } catch (Exception e) {
            logger.error("更新失败",e);
        }
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

    public List<TCoupon> loadCoupon(Integer o) {
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

    /***
     * 获取有效时间的可用优惠券列表
     * @return
     */
    public List<TCoupon> getCouponList4CacheLoader(){
        List<TCoupon> couponList = Lists.newArrayList();
        try {
            couponList = couponCaffeine.get(1);
        } catch (Exception e) {
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


    @Override
    public String saveUserCoupon(UserCouponModel userCouponModel) {
        String result = check(userCouponModel);
        if(result!=null){
            return result;
        }
        TCoupon coupon = tCouponMapper.selectByPrimaryKey(userCouponModel.getCouponId());
        if(coupon==null){
            return "coupon无效";
        }
        return save2DB(userCouponModel, coupon);
    }

    private String check(UserCouponModel userCouponModel){
        Integer couponId =  userCouponModel.getCouponId();
        Integer userId = userCouponModel.getUserId();
        if(couponId== null||userId == null){
            return "couponId或者userId为空";
        }
        return null;
    }

    private String save2DB(UserCouponModel dto,TCoupon coupon){
        TUserCoupon userCoupon = new TUserCoupon();
        BeanUtils.copyProperties(dto,userCoupon);
        userCoupon.setPicUrl(coupon.getPicUrl());
        userCoupon.setCreateTime(new Date());
        SnowflakeIdWorker worker = new SnowflakeIdWorker(0,0);
        userCoupon.setUserCouponCode(worker.nextId()+"");
        tUserCouponMapper.insertSelective(userCoupon);
        return "领取成功";
    }
}
