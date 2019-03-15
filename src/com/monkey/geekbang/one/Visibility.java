package com.monkey.geekbang.one;

/**
 * @description: 可见性问题
 * @author: JJC
 * @createTime: 2019/2/28
 */
public class Visibility {
    /**
     * 此处count不是static导致可见性问题
     */
    private long count = 0;

    private void add10K() {
        int idx = 0;
        while (idx++ < 10000) {
            /**
             * 完成count += 1操作实际至少三条CPU指令：
             * 1.count变量从内存加载到CPU的寄存器
             * 2.寄存器中执行+1操作
             * 3.结果写入内存（缓存机制导致可能写入的是CPU缓存而不是内存）
             */
            count += 1;
        }
    }

    public static void main(String[] args) throws InterruptedException{
        // 可见性问题
        final Visibility visibility = new Visibility();
        // java8 函数式接口的方式创建线程， Runnable实际是一个FunctionInterface
        Thread thread1 = new Thread(() -> {
            visibility.add10K();
        });
        Thread thread2 = new Thread(() -> {
            visibility.add10K();
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(visibility.count);
    }
}
