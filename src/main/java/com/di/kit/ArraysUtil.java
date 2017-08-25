package com.di.kit;

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
	System.arraycopy(t2, 0, temp, t1.length, t1.length);
	return temp;
    }
}
