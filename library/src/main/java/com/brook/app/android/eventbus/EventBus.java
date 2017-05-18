package com.brook.app.android.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import com.brook.app.android.superlibrary.eventbus.utils.ClassUtil;
import com.brook.app.android.superlibrary.eventbus.utils.MirrorsReference;
import com.brook.app.android.superlibrary.eventbus.utils.ThreadPoolUtil;
import com.brook.app.android.superlibrary.utils.LogUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author Brook
 * @time 2017/4/24 21:55
 */
public final class EventBus {

    private static EventBus eventBus;

    // 默认的线程池，用于执行接收者方法
    private ExecutorService sExecutorService = ThreadPoolUtil.getDefault();

    private Handler mHandler;

    public void setExecutorService(ExecutorService executorService) {
        if (executorService == null) {
            return;
        }
        sExecutorService = executorService;
    }

    // 提供构造方法，便于自己实现特殊的单例，推荐使用默认的
    public EventBus() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    // 默认推荐的构造方法
    public static EventBus getDefault() {
        if (eventBus == null) {
            synchronized (EventBus.class) {
                if (eventBus == null) {
                    eventBus = new EventBus();
                }
            }
        }
        return eventBus;
    }

    // 存储所有的接收者信息
    private Map<MirrorsReference<Object>, List<SubscribeInfo>> map = new HashMap<>();

    /**
     * 存储所有的粘滞事件
     * Pair.first的值为发送粘滞事件的类的名字
     * Pair.second 的值为发送的粘滞事件的消息体，即事件内容
     */
    public List<Pair<String, Object[]>> sticky = new ArrayList<>();

    /**
     * 注册一个事件订阅者
     *
     * @param object 需要订阅事件的类
     * @return 返回一个EventBus实例
     */
    public EventBus register(final Object object) {
        MirrorsReference<Object> reference = new MirrorsReference<>(object);
        if (!map.containsKey(reference)) {
            Class<?> aClass = object.getClass();
            Method[] methods = aClass.getDeclaredMethods();
            List<SubscribeInfo> methodList = new ArrayList<>();
            for (final Method method : methods) {
                EventSubscribe subscribe = method.getAnnotation(EventSubscribe.class);
                if (subscribe != null) {
                    ThreadMode threadMode = subscribe.THREAD_MODE();
                    Class[] actions = subscribe.actions();

                    SubscribeInfo subscribeInfo = new SubscribeInfo();
                    subscribeInfo.setMethodName(method.getName());
                    List<String> names = new ArrayList<>();
                    for (Class action : actions) {
                        names.add(action.getName());
                    }
                    subscribeInfo.setPublisherName(names);

                    switch (threadMode) {
                        case SYNC:
                            subscribeInfo.setThreadMode(2);
                        case ASYNC:
                            subscribeInfo.setThreadMode(1);
                            break;
                        default:
                            subscribeInfo.setThreadMode(0);
                            break;
                    }

                    Class<?>[] parameterTypes = method.getParameterTypes();

                    List<String> parameters = new ArrayList<>();
                    for (Class name : parameterTypes) {
                        parameters.add(name.getName());
                    }
                    subscribeInfo.setMethodParameter(parameters);

                    for (int i = 0; i < sticky.size(); i++) {
                        final Pair<String, Object[]> pair = sticky.get(i);

                        if (ClassHelper.compile(method, pair.second)
                                && (subscribeInfo.getPublisherName().size() <= 0
                                || subscribeInfo.getPublisherName().contains(pair.first))) {

                            if (subscribeInfo.getThreadMode() == 0) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            method.invoke(object, pair.second);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else if (subscribeInfo.getThreadMode() == 1) {
                                sExecutorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            method.invoke(object, pair.second);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                try {
                                    method.invoke(object, pair.second);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                    methodList.add(subscribeInfo);
                }
            }
            map.put(reference, methodList);
        }
        return this;
    }

    /**
     * 移除一个事件订阅者
     *
     * @param object 需要移除的订阅者
     */
    public void unregister(Object object) {
        map.remove(new MirrorsReference<Object>(object));
    }

    /**
     * 发布一个事件
     *
     * @param event 事件主体
     * @return 当前对象
     */
    public EventBus post(final Object... event) {
        if (event != null) {
            Iterator<MirrorsReference<Object>> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                MirrorsReference<Object> next = iterator.next();
                final Object object = next.get();
                if (object == null) {
                    iterator.remove();
                    map.remove(next);
                    continue;
                }

                List<SubscribeInfo> methodList = map.get(next);
                for (SubscribeInfo subscribe : methodList) {
                    Method[] methodByName = getMethodByName(object, subscribe.getMethodName());
                    for (final Method method : methodByName) {
                        if (ClassHelper.compile(subscribe.getMethodParameter(), method.getParameterTypes())) {
                            method.setAccessible(true);
                            if (subscribe.getThreadMode() == 0) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            method.invoke(object, event);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else if (subscribe.getThreadMode() == 1) {
                                sExecutorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            method.invoke(object, event);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                try {
                                    method.invoke(object, event);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                }
            }
        }
        return this;
    }

    /**
     * 通过方法名获取类的全部方法
     *
     * @param object 需要解析的类
     * @param name   方法名
     * @return 匹配的方法数组，保证不为NULL
     */
    public Method[] getMethodByName(Object object, String name) {
        Method[] declaredMethods = object.getClass().getDeclaredMethods();
        List<Method> methodList = new ArrayList<>();
        for (Method method : declaredMethods) {
            if (method.getName().equals(name)) {
                methodList.add(method);
            }
        }
        return methodList.toArray(new Method[]{});
    }

    /**
     * 发布一个粘滞事件
     *
     * @param event 事件主体
     * @return 当前对象
     */
    public EventBus postSticky(Object... event) {
        sticky.add(new Pair<String, Object[]>(ClassUtil.getWhoCallMe(), event));
        post(event);
        return this;
    }

    /**
     * 移除一个粘滞事件
     *
     * @param event 事件主体
     * @return 当前对象
     */
    public EventBus removeStickyEvent(Object... event) {
        Iterator<Pair<String, Object[]>> iterator = sticky.iterator();
        while (iterator.hasNext()) {
            Pair<String, Object[]> next = iterator.next();
            if (ClassHelper.compile(ClassHelper.array(next.second), ClassHelper.array(event))) {
                iterator.remove();
            }
        }
        return this;
    }

    /**
     * 移除所有粘滞事件
     */
    public void removeAllStickyEvents() {
        sticky.clear();
    }
}
