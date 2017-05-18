package com.brook.app.android.eventbus;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Brook
 * @time 2017/4/26 12:04
 */
public class ClassHelper {

    /**
     * 比较类名
     *
     * @param method
     * @param method1
     * @return
     */
    public static boolean compile(Method method, Method method1) {
        return compile(method.getParameterTypes(), method1.getParameterTypes());
    }

    /**
     * 类名
     *
     * @param parameterTypes
     * @param parameterTypes1
     * @return
     */
    public static boolean compile(Class[] parameterTypes, Class[] parameterTypes1) {
        if (parameterTypes == null || parameterTypes1 == null || parameterTypes.length != parameterTypes1.length) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!equalsName(parameterTypes[i], parameterTypes1[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较类名
     *
     * @param method
     * @param method1
     * @return
     */
    public static boolean compile(Method method, Object[] method1) {
        return compile(method.getParameterTypes(), array(method1));
    }

    /**
     * 比较类名
     *
     * @param clazz
     * @param clazz1
     * @return
     */
    public static boolean compile(List<String> clazz, Object... clazz1) {
        if (clazz.size() != clazz1.length) {
            return false;
        }
        for (int i = 0; i < clazz.size(); i++) {
            String name = getName(clazz1[i].getClass());
            if (!name.equals(clazz.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较两个参数的类名字是否一样
     *
     * @param clazz  包含名字的List
     * @param clazz1 Class数组
     * @return true表示相同，false表示不同
     */
    public static boolean compile(List<String> clazz, Class... clazz1) {
        if (clazz.size() != clazz1.length) {
            return false;
        }
        for (int i = 0; i < clazz.size(); i++) {
            String name = getName(clazz1[i]);
            if (!name.equals(clazz.get(i))) {
                return false;
            }
        }
        return true;
    }

    // 通过Object数组得到Class数组
    public static Class[] array(Object[] objects) {
        Class[] clazz = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            clazz[i] = objects[i].getClass();
        }
        return clazz;
    }


    // 比较两个Class对象的全类名
    public static boolean equalsName(Class clazz, Class clazz1) {
        String name = getName(clazz);
        String name1 = getName(clazz1);
        if (name.equals(name1)) {
            return true;
        }
        return false;
    }


    // 通过Class对象，得到全类名，会自动把基本类型向包装器类型转换
    public static String getName(Class clazz) {
        String name = clazz.getName();
        if (int.class.getName().equals(name)) {
            return Integer.class.getName();
        }
        if (long.class.getName().equals(name)) {
            return Long.class.getName();
        }
        if (char.class.getName().equals(name)) {
            return Character.class.getName();
        }
        if (boolean.class.getName().equals(name)) {
            return Boolean.class.getName();
        }
        if (float.class.getName().equals(name)) {
            return Float.class.getName();
        }
        if (double.class.getName().equals(name)) {
            return Double.class.getName();
        }
        if (short.class.getName().equals(name)) {
            return Short.class.getName();
        }
        if (byte.class.getName().equals(name)) {
            return Byte.class.getName();
        }
        return name;
    }
}
