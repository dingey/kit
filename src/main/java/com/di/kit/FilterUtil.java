package com.di.kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author d
 */
public class FilterUtil {
    /**
     * 获取数组1和数组2对象的交集部分
     *
     * @param list1  数组1
     * @param list2  数组2
     * @param props1 list1包装的用于比较的对象属性名集合
     * @param props2 list2包装的用于比较的对象属性名集合
     * @param <T>    类型1
     * @param <E>    类型2
     * @return 数组1值与数组2相等的部分
     */
    public static <T, E> List<T> intersection(List<T> list1, List<E> list2, List<String> props1, List<String> props2) {
        List<T> tmps = new ArrayList<>();
        if (props1 == null || props2 == null || props1.size() != props2.size()) {
            return tmps;
        }
        for (T t1 : list1) {
            for (E t2 : list2) {
                int i = 0;
                for (int j = 0; j < props1.size(); j++) {
                    Object v1 = ClassUtil.getFieldValueByGetMethod(props1.get(j), t1);
                    Object v2 = ClassUtil.getFieldValueByGetMethod(props2.get(j), t2);
                    if (v1 != null && v1.equals(v2) || (v1 == null && v2 == null)) {
                        i = i + 1;
                    } else {
                        break;
                    }
                }
                if (i == props1.size()) {
                    tmps.add(t1);
                    break;
                }
            }
        }
        return tmps;
    }

    public static <T, E> List<T> intersection(List<T> list1, List<E> list2, String[] props1, String[] props2) {
        return intersection(list1, list2, Arrays.asList(props1), Arrays.asList(props2));
    }

    public static <T> List<T> intersection(List<T> list1, List<T> list2, List<String> props) {
        return intersection(list1, list2, props, props);
    }

    /**
     * 相同对象数组的交集
     *
     * @param list1  数组1
     * @param list2  数组2
     * @param props 属性
     * @param <T>   类型
     * @return 数组
     */
    public static <T> List<T> intersection(List<T> list1, List<T> list2, String... props) {
        return intersection(list1, list2, props, props);
    }

    /**
     * 获取差集,数组1减去与数组2交集部分
     *
     * @param list1  数组1
     * @param list2  数组2
     * @param props1 list1包装的用于比较的对象属性名集合
     * @param props2 list2包装的用于比较的对象属性名集合
     * @param <T>    类型1
     * @param <E>    类型2
     * @return 数组
     */
    public static <T, E> List<T> differenceSet(List<T> list1, List<E> list2, List<String> props1, List<String> props2) {
        List<T> tmps = new ArrayList<>();
        if (props1 == null || props2 == null || props1.size() != props2.size() || list1 == null) {
            return tmps;
        }
        if (list2 == null) {
            return list1;
        }
        for (T t1 : list1) {
            boolean b = false;
            for (E t2 : list2) {
                int i = 0;
                for (int j = 0; j < props1.size(); j++) {
                    Object v1 = ClassUtil.getFieldValueByGetMethod(props1.get(j), t1);
                    Object v2 = ClassUtil.getFieldValueByGetMethod(props2.get(j), t2);
                    if (v1 != null && !v1.equals(v2) || (v1 == null && v2 != null)) {
                        break;
                    } else {
                        i = i + 1;
                    }
                }
                if (i == props1.size()) {
                    b = true;
                }
            }
            if (!b) {
                tmps.add(t1);
            }
        }
        return tmps;
    }

    /**
     * 不同对象数组的差集
     *
     * @param list1  数组1
     * @param list2  数组2
     * @param props1 list1包装的用于比较的对象属性名集合
     * @param props2 list2包装的用于比较的对象属性名集合
     * @param <T>    类型1
     * @param <E>    类型2
     * @return 数组
     */
    public static <T, E> List<T> differenceSet(List<T> list1, List<E> list2, String[] props1, String[] props2) {
        return differenceSet(list1, list2, Arrays.asList(props1), Arrays.asList(props2));
    }

    /**
     * 相同对象数组的差集
     *
     *
     * @param list1  数组1
     * @param list2  数组2
     * @param props 属性
     * @param <T>   类型
     * @return 数组
     */
    public static <T> List<T> differenceSet(List<T> list1, List<T> list2, List<String> props) {
        return differenceSet(list1, list2, props, props);
    }

    /**
     * 相同对象数组的差集
     *
     * @param list1  数组1
     * @param list2  数组2
     * @param props 属性
     * @param <T>   类型
     * @return 数组
     */
    public static <T> List<T> differenceSet(List<T> list1, List<T> list2, String... props) {
        return differenceSet(list1, list2, props, props);
    }
}
