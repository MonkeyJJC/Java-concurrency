package com.monkey.chapter8.timingthreadpool;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: JJC
 * @createTime: 2018/11/27
 */
class DoSomeThing implements Runnable {

    private int sleepTime;

    public DoSomeThing(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        System.out.printf(Thread.currentThread().getName() + "is running");
        try {
            TimeUnit.SECONDS.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Test {
    public static void main(String[] args) {
        /**
         * SynchronousQueue避免任务排队，不是一个真正队列，而是一种在线程之间进行移交的机制
         */
        ThreadPoolExecutor threadPoolExecutor = new TimingThreadPool(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        for (int i = 0; i < 5; i++) {
            threadPoolExecutor.execute(new DoSomeThing(i));
        }
        threadPoolExecutor.shutdown();
    }
}
