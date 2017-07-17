package com.di.kit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author d
 */
public class FilterUtil {
	/**
	 * 获取数组1和数组2对象的交集部分
	 * @param list1 数组1
	 * @param list2 数组2
	 * @param key 判断对象一致的key
	 * @return
	 */
	public static <T> List<T> andSetTogether(List<T> list1, List<T> list2, String key) {
		List<T> tmps = new ArrayList<>();
		for (T t1 : list1) {
			for (T t2 : list2) {
				Object v1 = ClassUtil.getFieldValueByGetMethod(key, t1);
				Object v2 = ClassUtil.getFieldValueByGetMethod(key, t2);
				if (v1.equals(v2)) {
					tmps.add(t1);
					continue;
				}
			}
		}
		return tmps;
	}
	/**
	 * 获取数组1减去与数组2交集部分
	 * @param list1 数组1
	 * @param list2 数组2
	 * @param key 判断对象一致的key
	 * @return
	 */
	public static <T> List<T> miniusSet(List<T> list1, List<T> list2, String key) {
		List<T> tmps = new ArrayList<>();
		for (T t1 : list1) {
			boolean b=false;
			for (T t2 : list2) {
				Object v1 = ClassUtil.getFieldValueByGetMethod(key, t1);
				Object v2 = ClassUtil.getFieldValueByGetMethod(key, t2);
				b=v1.equals(v2);
				if (b) {
					continue;
				}
			}
			if(b){
				continue;
			}else{
				tmps.add(t1);
			}
		}
		return tmps;
	}
	
}
