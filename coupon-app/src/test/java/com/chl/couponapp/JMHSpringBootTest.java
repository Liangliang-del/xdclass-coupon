package com.chl.couponapp;

import com.chl.couponapp.service.CouponService;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class JMHSpringBootTest {

    //private CouponService couponService;

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
        ConfigurableApplicationContext context = SpringApplication.run(CouponAppApplication.class, arg);
        //couponService = context.getBean(CouponService.class);
    }


    /**
     * benchmark执行多次，此注解代表触发我们所要进行基准测试的方法
     */
    @Benchmark
    public void test(){
        //System.out.println(couponService.getCouponList());
    }
}
