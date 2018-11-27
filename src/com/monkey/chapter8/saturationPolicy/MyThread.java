package com.monkey.chapter8.saturationPolicy;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: JJC
 * @createTime: 2018/11/27
 */
public class MyThread implements Runnable {
    public String name;
    public MyThread(String name) {
        this.name = name;
    }
    @Override
    public void run() {
        System.out.println("Thread-" + name + "is running");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
