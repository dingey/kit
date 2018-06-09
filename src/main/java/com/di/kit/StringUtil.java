package com.di.kit;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author di
 */
public class StringUtil {
	public static final String lineSeparator = System.getProperty("line.separator", "\n");

	public static String firstLower(String s) {
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	public static String firstUpper(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static String camelCase(String s) {
		if (s.indexOf("_") != -1) {
			String[] ss = s.split("_");
			StringBuilder sb = new StringBuilder(ss[0]);
			if (ss.length > 1) {
				for (int i = 1; i < ss.length; i++) {
					sb.append(firstUpper(ss[i]));
				}
			}
			return sb.toString();
		} else {
			return s;
		}
	}

	public static String upperCamelCase(String s) {
		return firstUpper(camelCase(s));
	}

	public static String snakeCase(String camelCase) {
		if (camelCase != null && !camelCase.trim().isEmpty()) {
			char[] cs = camelCase.toCharArray();
			StringBuilder sb = new StringBuilder();
			sb.append(Character.toLowerCase(cs[0]));
			for (int i = 1; i < cs.length; i++) {
				if (Character.isUpperCase(cs[i])) {
					sb.append("_").append(Character.toLowerCase(cs[i]));
				} else {
					sb.append(camelCase.toCharArray()[i]);
				}
			}
			return sb.toString();
		} else {
			return camelCase;
		}
	}

	public static boolean isBlank(Object... objects) {
		Boolean result = false;
		for (Object object : objects) {
			if (object == null || "".equals(object.toString().trim()) || "null".equals(object.toString().trim())
					|| "[null]".equals(object.toString().trim()) || "[]".equals(object.toString().trim())) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static String mapToGet(Map<? extends Object, ? extends Object> map) {
		String result = "";
		if (map == null || map.size() == 0) {
			return result;
		}
		Set<? extends Object> keys = map.keySet();
		for (Object key : keys) {
			result += ((String) key + "=" + (String) map.get(key) + "&");
		}

		return isBlank(result) ? result : result.substring(0, result.length() - 1);
	}

	public static Map<String, ? extends Object> getToMap(String args) {
		if (isBlank(args)) {
			return null;
		}
		args = args.trim();
		// 如果是?开头,把?去掉
		if (args.startsWith("?")) {
			args = args.substring(1, args.length());
		}
		String[] argsArray = args.split("&");

		Map<String, Object> result = new HashMap<String, Object>();
		for (String ag : argsArray) {
			if (!isBlank(ag) && ag.indexOf("=") > 0) {

				String[] keyValue = ag.split("=");
				// 如果value或者key值里包含 "="号,以第一个"="号为主 ,如 name=0=3
				// 转换后,{"name":"0=3"}, 如果不满足需求,请勿修改,自行解决.
				String key = keyValue[0];
				String value = "";
				for (int i = 1; i < keyValue.length; i++) {
					value += keyValue[i] + "=";
				}
				value = value.length() > 0 ? value.substring(0, value.length() - 1) : value;
				result.put(key, value);
			}
		}
		return result;
	}

	public static String toUnicode(String str) {
		String as[] = new String[str.length()];
		String s1 = "";
		for (int i = 0; i < str.length(); i++) {
			as[i] = Integer.toHexString(str.charAt(i) & 0xffff);
			s1 = s1 + "\\u" + as[i];
		}
		return s1;
	}

	public static String doubleString(Double str) {
		String money = str.toString();
		try {
			Double.parseDouble(money);
		} catch (Exception e) {
			BigDecimal bDecimal = new BigDecimal(str);
			money = bDecimal.toPlainString();
		}
		return money;
	}

	public static String urlEncode(String value, String encode) {
		try {
			value = java.net.URLEncoder.encode(value, encode == null ? "utf-8" : encode);
			return value;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String urlDecoder(String value, String encode) {
		try {
			value = java.net.URLDecoder.decode(value, encode == null ? "utf-8" : encode);
			return value;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String escape(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		} else {
			return s.trim().replace("\\", "\\\\").replace("\r", "\\r").replace("\n", "\\n");
		}
	}
}
