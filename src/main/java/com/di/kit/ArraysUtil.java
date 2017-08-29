package com.di.kit;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author di
 */
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
