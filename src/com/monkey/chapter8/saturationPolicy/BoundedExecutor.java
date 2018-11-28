package com.monkey.chapter8.saturationPolicy;

import java.util.concurrent.*;

/**
 * @description: 在没有预定义的饱和策略来阻塞execute的情况下，通过使用Semaphore信号量来限制任务的到达率
 * https://www.jianshu.com/p/8cd5d3a20352
 * http://blog.sina.com.cn/s/blog_6145ed8101011e19.html
 * @author: JJC
 * @createTime: 2018/11/27
 */
public class BoundedExecutor {

    private final ExecutorService executor;
    private final Semaphore semaphore;

    public BoundedExecutor(ExecutorService executor, int bound) {
        this.executor = executor;
        /**
         * 计数信号量，用来控制同时访问某个特定资源的操作数量
         */
        this.semaphore = new Semaphore(bound);
    }

    public void submitTask(final Runnable command) {
        /**
         * 阻塞方法，直到资源池不为空
         */
        try {
            semaphore.acquire();
            try {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            command.run();
                        } finally {
                            semaphore.release();
                        }
                    }
                });
            } catch (RejectedExecutionException e) {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            semaphore.release();
        }
    }

    public void stop() {
        this.executor.shutdown();
    }

    public static void main(String[] args) {
        /**
         * ThreadPoolExecutor是ExecutorService的一个实现类
         */
        ExecutorService executorService = new ThreadPoolExecutor(6, 6, 0L,
                TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>(5));
        /**
         * 信号量设置为，最多同时有5个线程来处理任务，大于5时，acquire()会阻塞，不会进行后续execute操作
         */
        BoundedExecutor boundedExecutor = new BoundedExecutor(executorService, 5);
        for (int i = 0; i < 20; i++) {
            boundedExecutor.submitTask(new MyThread(String.valueOf(i)));
        }
        boundedExecutor.stop();
    }
}
