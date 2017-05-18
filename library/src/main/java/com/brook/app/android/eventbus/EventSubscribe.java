package com.brook.app.android.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Brook
 * @time 2017/4/24 21:52
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventSubscribe {

    // 设置当前订阅者只关心谁发送的消息
    Class[] actions() default {};

    // 设置订阅者的线程模式
    ThreadMode THREAD_MODE() default ThreadMode.SYNC;
}
