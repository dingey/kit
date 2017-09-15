package com.di.kit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author d
 */
public class FilterUtil {
	/**
	 * 获取数组1和数组2对象的交集部分
	 */
	public static <T> List<T> andSetTogether(List<T> list1, List<T> list2, String... keys) {
		List<T> tmps = new ArrayList<>();
		for (T t1 : list1) {
			for (T t2 : list2) {
				int i = 0;
				for (String key : keys) {
					Object v1 = ClassUtil.getFieldValueByGetMethod(key, t1);
					Object v2 = ClassUtil.getFieldValueByGetMethod(key, t2);
					if (v1.equals(v2)) {
						i++;
					} else {
						break;
					}
				}
				if (i == keys.length) {
					tmps.add(t1);
					break;
				}
			}
		}
		return tmps;
	}

	/**
	 * 获取数组1减去与数组2交集部分
	 */
	public static <T> List<T> miniusSet(List<T> list1, List<T> list2, String... keys) {
		List<T> tmps = new ArrayList<>();
		for (T t1 : list1) {
			boolean b = false;
			for (T t2 : list2) {
				int i = 0;
				for (String key : keys) {
					Object v1 = ClassUtil.getFieldValueByGetMethod(key, t1);
					Object v2 = ClassUtil.getFieldValueByGetMethod(key, t2);
					if (!v1.equals(v2)) {
						break;
					} else {
						i++;
					}
				}
				if (i == keys.length) {
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
	 * 获取数组1与数组2交集和差集部分
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T>[] minusAndTogether(List<T> list1, List<T> list2, String... keys) {
		List<T> minus = new ArrayList<>();
		List<T> ands = new ArrayList<>();
		for (T t1 : list1) {
			boolean b = false;
			for (T t2 : list2) {
				int i = 0;
				for (String key : keys) {
					Object v1 = ClassUtil.getFieldValueByGetMethod(key, t1);
					Object v2 = ClassUtil.getFieldValueByGetMethod(key, t2);
					if (!v1.equals(v2)) {
						break;
					} else {
						i++;
					}
				}
				if (i == keys.length) {
					b = true;
				}
			}
			if (!b) {
				minus.add(t1);
			} else {
				ands.add(t1);
			}
		}
		return new List[] { minus, ands };
	}
}
