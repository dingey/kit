package com.kit.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.di.kit.ArraysUtil;

/**
 * @author di
 */
public class A {
    @SuppressWarnings("unused")
    @Test
    public void test() {
	ArrayList<String> list = new ArrayList<>();
	list.add("a");
	String[] ss = { "b", "c" };
	String s1[] = { "1", "2" };
	String s2[] = { "3", "4" };
	String[] merge = ArraysUtil.merge(s1, s2);
	List<String> merge2 = ArraysUtil.merge(list, ss);
	String[] merge3 = ArraysUtil.merge(ss, list);
    }
}
