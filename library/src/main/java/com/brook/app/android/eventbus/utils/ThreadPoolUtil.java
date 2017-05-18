package com.brook.app.android.eventbus.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Brook
 * @time 2017/4/26 18:01
 */
public class ThreadPoolUtil {

    private static ExecutorService executorservice;

    // 默认的线程池大小
    private static int DEFAULT_SIZE = 5;

    /**
     * 创建一个默认的线程池
     *
     * @return
     */
    public static ExecutorService getDefault() {
        if (executorservice == null) {
            synchronized (ThreadPoolUtil.class) {
                if (executorservice == null) {
                    executorservice = Executors.newFixedThreadPool(DEFAULT_SIZE);
                }
            }
        }
        return executorservice;
    }
}
