package com.di.kit;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author di
 */
@SuppressWarnings("unused")
public class ArraysUtil {
	public static <T> List<List<T>> splitList(List<T> list, int limit) {
		int i = 0;
		List<List<T>> temps = new ArrayList<>();
		while (i * limit < list.size()) {
			if ((i + 1) * limit > list.size()) {
				temps.add(list.subList(i * limit, list.size()));
			} else {
				temps.add(list.subList(i * limit, (i + 1) * limit));
			}
			i++;
		}
		return temps;
	}

	public static <T> List<List<T>> split(List<T> list, int num) {
		List<List<T>> temps = new ArrayList<>();
		int length = list.size() / num + (list.size() % num == 0 ? 0 : 1);
		if (list.size() > 0) {
			for (int i = 0; i < num; i++) {
				if ((i + 1) * length < list.size()) {
					List<T> ts = list.subList(i * length, (i + 1) * length);
					if (!ts.isEmpty())
						temps.add(ts);
				} else {
					List<T> ts = list.subList(i * length, list.size());
					if (!ts.isEmpty())
						temps.add(ts);
				}
			}
		}
		return temps;
	}

	public static <T> T[] merge(T[] t1, T[] t2) {
		T[] temp = Arrays.copyOf(t1, t1.length + t2.length);
		System.arraycopy(t2, 0, temp, t1.length, t2.length);
		return temp;
	}

	public static <T> List<T> merge(List<T> list, T[] args) {
		List<T> tmp = new ArrayList<>();
		tmp.addAll(list);
		tmp.addAll(Arrays.asList(args));
		return tmp;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] merge(T[] args, List<T> list) {
		T[] tmp = (T[]) Array.newInstance(args.getClass().getComponentType(), list.size());
		return merge(args, list.toArray(tmp));
	}
}
