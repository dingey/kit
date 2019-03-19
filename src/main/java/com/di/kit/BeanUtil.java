package com.di.kit;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class BeanUtil {
    private BeanUtil() {
    }

    public static <T> void setProperty(T t, String propertyName, Object value) {
        PropertyDescriptor propertyDescriptor;
        try {
            propertyDescriptor = new PropertyDescriptor(propertyName, t.getClass());
            Method method = propertyDescriptor.getWriteMethod();
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            method.invoke(t, value);
        } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException("设置属性值失败" + e.getMessage(), e);
        }
    }

    public static <T> Object getProperty(T t, String propertyName) {
        try {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, t.getClass());
            Method method = propertyDescriptor.getReadMethod();
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method.invoke(t);
        } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException("获取属性值失败" + e.getMessage(), e);
        }
    }

    public static <T> Map<String, Object> getProperties(T t) {
        Map<String, Object> m = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(t.getClass());
            PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
            if (proDescrtptors != null && proDescrtptors.length > 0) {
                for (PropertyDescriptor propDesc : proDescrtptors) {
                    if (propDesc.getName().equals("class"))
                        continue;
                    Method method = propDesc.getReadMethod();
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    try {
                        m.put(propDesc.getName(), method.invoke(t));
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IntrospectionException e) {
            System.err.println("获取实例属性失败" + e.getMessage());
        }
        return m;
    }

    public static <T> void setValues(Class<T> t, Map<String, Object> values) {
        try {
            setValues(t.newInstance(), values);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(t.getName() + "实例化失败" + e.getMessage(), e);
        }
    }

    public static <T> void setValues(T t, Map<String, Object> values) {
        Objects.requireNonNull(t, "示例不能为空");
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(t.getClass());
            PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
            if (proDescrtptors != null && proDescrtptors.length > 0) {
                for (PropertyDescriptor propDesc : proDescrtptors) {
                    Method method = propDesc.getWriteMethod();
                    if (method == null)
                        continue;
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    Object v = values.get(propDesc.getName());
                    try {
                        method.invoke(t, v);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        System.err.println(propDesc.getName() + "设置实例属性值失败" + e.getMessage());
                    }
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
}
