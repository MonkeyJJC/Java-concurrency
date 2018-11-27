package com.monkey.chapter8.timingthreadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * @description: 扩展ThreadPoolExecutor，实现给线程增加统计信息，如平均任务耗时等
 * 改写beforeExecute,afterExecute,terminated
 * 任务从run中正常返回或抛出异常而返回，afterExecute都会执行（如任务在完成后带一个error，不调用afterExecute）
 * beforeExecute若抛出RuntimeException，任务不执行且afterExecute不执行
 * @author: JJC
 * @createTime: 2018/11/27
 */
public class TimingThreadPool extends ThreadPoolExecutor {

    private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
    private final Logger log = Logger.getLogger("TimingThreadPool");
    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default thread factory and rejected execution handler.
     * It may be more convenient to use one of the {@link Executors} factory
     * methods instead of this general purpose constructor.
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *        pool
     * @param keepAliveTime when the number of threads is greater than
     *        the core, this is the maximum time that excess idle threads
     *        will wait for new tasks before terminating.
     *        线程池维护线程所允许的空闲时间
     * @param unit the time unit for the {@code keepAliveTime} argument 线程池维护所允许的空闲时间的单位
     * @param workQueue the queue to use for holding tasks before they are
     *        executed.  This queue will hold only the {@code Runnable}
     *        tasks submitted by the {@code execute} method.
     *        线程池所使用的缓存队列
     * @throws IllegalArgumentException if one of the following holds:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException if {@code workQueue} is null
     */
    public TimingThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                            BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        log.info(String.format("Thread %s : start %s", t, r));
        /**
         *nanoTime()返回系统计时器的当前值，以毫微秒为单位
         */
        startTime.set(System.nanoTime());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        try {
            long endTime = System.nanoTime();
            /**
             * ThreadLocal中存储各自线程任务的开始时间
             */
            long taskTime = endTime - startTime.get();
            numTasks.incrementAndGet();
            totalTime.addAndGet(taskTime);
            log.info(String.format("Thread %s : end %s, time = %dns", t, r, taskTime));
        } finally {
            super.afterExecute(r, t);
        }
    }

    /**
     * 线程池完成关闭操作时调用terminated，可以用来释放各种资源及发送通知，统计信息等
     */
    @Override
    protected void terminated() {
        try {
            log.info(String.format("Terminated : avgTime = %dns", totalTime.get()/numTasks.get()));
        } finally {
            super.terminated();
        }
    }
}
