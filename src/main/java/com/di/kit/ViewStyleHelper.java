package com.di.kit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.di.kit.JdbcMeta.Table;
import com.di.kit.MvcGenerater.ViewStyle;

/**
 * @author di
 */
public abstract class ViewStyleHelper implements ViewStyle {
	String lowCamel(String underline) {
		return StringUtil.snakeCase(underline);
	}

	LinkedHashMap<String, String> numEnum(String remark) {
		LinkedHashMap<String, String> m = new LinkedHashMap<>();
		List<Integer> sub = new ArrayList<>();
		for (int i = 0; i < remark.length(); i++) {
			char c = remark.toCharArray()[i];
			if (Character.isDigit(c)) {
				sub.add(i);
			}
		}
		if (sub.get(sub.size() - 1) < (remark.length() - 1) && sub.size() > 1) {
			for (int i = 0; i < sub.size() - 1; i++) {
				String s = remark.substring(sub.get(i), sub.get(i + 1));
				String k = remark.charAt(sub.get(i)) + "";
				s = s.replaceAll(k, "").replaceAll(":", "").replaceAll(",", "").replaceAll(";", "");
				m.put(k, s);
			}
			String k = remark.charAt(sub.get(sub.size() - 1)) + "";
			String s = remark.substring(sub.get(sub.size() - 1), remark.length()).replaceAll(k, "");
			m.put(k, s.replaceAll(k, s));
		}
		return m;
	}

	public static void main(String[] args) {
		String s = "订单类型,0:线下展会订单,1:当面付订单,2:线上展会订单3专题订单";
		ViewStyleHelper v = new ViewStyleHelper() {

			@Override
			public String list(Table t) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String edit(Table t) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		LinkedHashMap<String, String> numEnum = v.numEnum(s);
		for (String i : numEnum.keySet()) {
			System.out.println(i + ":" + numEnum.get(i));
		}
	}
}
