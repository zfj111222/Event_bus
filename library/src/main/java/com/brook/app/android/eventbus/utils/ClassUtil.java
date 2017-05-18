package com.brook.app.android.eventbus.utils;

/**
 * @author Brook
 * @time 2017/4/26 16:47
 */
public class ClassUtil {
    public static String getWhoCallMe() {
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        if (stack.length > 2) {
            StackTraceElement stackTraceElement = stack[2];
            return stackTraceElement.getClassName();
        }
        return null;
    }
}
