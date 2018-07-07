package com.di.kit;

import java.util.Stack;

public class Conversion {
	private static char[] array = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
	private static String numStr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	// 10进制转为其他进制，除留取余，逆序排列
	public static String fromDecimal(long number, int n) {
		if (n > array.length)
			n = array.length;
		Long rest = number;
		Stack<Character> stack = new Stack<Character>();
		StringBuilder result = new StringBuilder(0);
		while (rest != 0) {
			stack.add(array[new Long((rest % n)).intValue()]);
			rest = rest / n;
		}
		for (; !stack.isEmpty();) {
			result.append(stack.pop());
		}
		return result.length() == 0 ? "0" : result.toString();
	}

	// 其他进制转为10进制，按权展开
	public static long toDecimal(String number, int n) {
		char ch[] = number.toCharArray();
		int len = ch.length;
		long result = 0;
		if (n == 10) {
			return Long.parseLong(number);
		}
		long base = 1;
		for (int i = len - 1; i >= 0; i--) {
			int index = numStr.indexOf(ch[i]);
			result += index * base;
			base *= n;
		}
		return result;
	}

	public static void main(String[] args) {
		String d = fromDecimal(2018053023041900000L, 62);
		System.out.println(d);
		System.out.println(toDecimal(d, 62));
	}
}
