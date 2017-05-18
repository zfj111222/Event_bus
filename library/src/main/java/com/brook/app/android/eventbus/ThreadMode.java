package com.brook.app.android.eventbus;

/**
 * @author Brook
 * @time 2017/4/29 15:43
 */
public enum ThreadMode {
    // 接收者代码在Android的主线程执行
    MAIN,
    // 接收者代码在子线程中执行，由默认线程池托管
    ASYNC,
    // 在同步代码中执行，谁调用，就在谁的线程执行
    SYNC
}