package com.monkey.chapter8.saturationPolicy;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description: CallerRunsPolicy策略
 * 线程池中所有线程被占用，并且工作队列被填满后，下一个任务会在调用execute时在主线程中执行，由于执行任务需要一定的时间，
 * 因此主线程至少在一段时间内不能提交任务（此处是需要等待MyThread的sleep时间）
 * @author: JJC
 * @createTime: 2018/11/27
 */
public class SaturationPolicy {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 0L,
                TimeUnit.MICROSECONDS, new LinkedBlockingDeque<Runnable>(5));
        /**
         * 设置饱和策略为CallerRunsPolicy
         * 饱和策略：
         * AbortPolicy(抛出RejectExecutionException异常)
         * CallerRunsPolicy
         * DiscardPolicy(抛弃该任务)
         * DiscardOldestPolicy(抛弃最旧任务)
         */
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        for (int i = 0; i < 20; i++) {
            threadPoolExecutor.execute(new MyThread(String.valueOf(i)));
        }
        threadPoolExecutor.shutdown();
    }
}
