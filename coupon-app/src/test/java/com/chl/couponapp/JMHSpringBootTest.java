package com.chl.couponapp;

import com.chl.couponapp.service.CouponService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@State(Scope.Thread)
public class JMHSpringBootTest {

    private ConfigurableApplicationContext context;
    private CouponService couponService;

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(JMHSpringBootTest.class.getName()+".*")
                .warmupIterations(2)
                .measurementIterations(2)
                .forks(1).build();
        new Runner(options).run();
    }

    /**
     * setup初始化容器的时候只执行一次
     */
    @Setup(Level.Trial)
    public void init(){
        String arg = "";
        context = SpringApplication.run(CouponAppApplication.class, arg);
        couponService = context.getBean(CouponService.class);
    }


    /**
     * benchmark执行多次，此注解代表触发我们所要进行基准测试的方法
     */
    @Benchmark
    public void test(){
        System.out.println(couponService.getCouponList());
    }

    @Benchmark
    public void testDB(){
        System.out.println(couponService.loadCoupon(1));
    }

    @Benchmark
    public void testCache(){
        System.out.println(couponService.getCouponList4Map());
    }
}
