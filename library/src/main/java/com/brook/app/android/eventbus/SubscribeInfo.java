package com.brook.app.android.eventbus;

import java.util.List;

/**
 * @author Brook
 * @time 2017/4/26 15:42
 */
public class SubscribeInfo {

    // 接收者的一个方法名
    private String methodName;
    // 接收者的参数列表参数方法名
    private List<String> methodParameter;
    // 接收者需要工作的线程模式
    private int threadMode;
    // 接收者关心的发布者的信息
    private List<String> publisherName;

    public List<String> getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(List<String> publisherName) {
        this.publisherName = publisherName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getMethodParameter() {
        return methodParameter;
    }

    public void setMethodParameter(List<String> methodParameter) {
        this.methodParameter = methodParameter;
    }

    public int getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(int threadMode) {
        this.threadMode = threadMode;
    }

    public int getParameterLength() {
        if (getMethodParameter() == null) {
            return 0;
        }
        return getMethodParameter().size();
    }
}
