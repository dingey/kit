package com.di.kit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author d
 */
public class Xml {
	private static String start = "<![CDATA[";
	private static String end = "]]>";

	@SuppressWarnings({ "unchecked" })
	public <T> T toObject(String xml, Class<T> t) {
		if (t.isArray()) {

		} else if (t == List.class || t == ArrayList.class) {

		} else if (t != Object.class && t != Class.class && (t instanceof Object)) {
			try {
				T o = t.newInstance();
				Map<String, Object> m = (Map<String, Object>) toObject(xml);
				String simpleName = o.getClass().getSimpleName();
				Map<String, Object> v = (Map<String, Object>)m.get(StringUtil.firstCharLower(simpleName));
				ClassUtil.setObjectFieldsValue(v, o);
				return o;
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Object toObject(String xml) {
		if (!xml.startsWith("<")) {
			return xml;
		} else {
			xml = xml.trim();
			if (xml.startsWith(start)) {
				return xml.substring(xml.indexOf(start) + start.length(), xml.indexOf(end));
			}
		}
		String e = xml.substring(xml.indexOf("<"), xml.indexOf(">") + 1).replaceAll(" +", " ").replaceAll(" +=", "=")
				.replaceAll("= +", "=").replaceAll("< +", "<").replaceAll(" +>", ">");
		String n = e.substring(1, e.indexOf(" ") > 0 ? e.indexOf(" ") : (e.length() - 1));
		LinkedHashMap<String, String> attrs = null;
		if ((n.length() + 2) < e.length()) {
			attrs = new LinkedHashMap<>();
			String att = e.substring(n.length() + 2, e.length() - 1);
			List<Integer> is = new ArrayList<>();
			int quot = 0;
			char[] cs = att.toCharArray();
			for (int i = 0; i < cs.length; i++) {
				if (cs[i] == '"' && i > 0 && cs[i - 1] != '\\') {
					quot++;
				}
				if (quot != 0 && quot % 2 == 0 && cs[i] == '"') {
					is.add(i);
				}
			}
			if (is.size() > 0) {
				attrs.put(att.substring(0, att.indexOf("=")),
						att.substring(att.indexOf("=") + 1, is.get(0)).replaceFirst("\"", ""));
			}
			if (is.size() > 1) {
				for (int i = 1; i < is.size(); i++) {
					String at = att.substring(is.get(i - 1) + 1, is.get(i));
					attrs.put(at.substring(0, at.indexOf("=")).trim(),
							at.substring(at.indexOf("=") + 1, at.length()).replaceFirst("\"", "").trim());
				}
			}
		}
		List<Integer> vs = new ArrayList<>();
		int dep = 0;
		String nStart = "<" + n;
		String nEnd = "</" + n + ">";
		for (int i = 0; i < (xml.toCharArray().length - nEnd.length() + 1); i++) {
			int quot = 0;
			int escape = 0;
			if (i != 0 && xml.charAt(i) == '"') {
				quot++;
			}
			boolean eStart = true;
			for (int j = 0; j < start.length(); j++) {
				if (start.charAt(j) != xml.charAt(i + j)) {
					eStart = false;
					break;
				}
			}
			if (eStart) {
				escape++;
			}
			boolean eEnd = true;
			for (int j = 0; j < end.length(); j++) {
				if (end.charAt(j) != xml.charAt(i + j)) {
					eEnd = false;
					break;
				}
			}
			if (eEnd) {
				escape--;
			}
			boolean bStart = true;
			for (int j = 0; j < nStart.length(); j++) {
				if (nStart.charAt(j) != xml.charAt(i + j)) {
					bStart = false;
					break;
				}
			}
			if (bStart) {
				dep++;
			}
			boolean bEnd = true;
			for (int j = 0; j < nEnd.length(); j++) {
				if (nEnd.charAt(j) != xml.charAt(i + j)) {
					bEnd = false;
					break;
				}
			}
			if (bEnd) {
				dep--;
			}
			if (dep == 0 && bEnd && escape == 0 && quot % 2 == 0) {
				vs.add(i);
			}
		}
		if (vs.size() < 2) {
			LinkedHashMap<String, Object> m = new LinkedHashMap<>();
			m.put("element name", n);
			m.put("element attributes", attrs);
			String x = xml.substring(xml.indexOf(">") + 1, xml.lastIndexOf("</"));
			m.put(n, toObject(x));
			return m;
		} else {
			List<Object> l = new ArrayList<>();
			for (int i = 0; i < vs.size(); i++) {
				String x = xml.substring(i == 0 ? 0 : (vs.get(i - 1) + 3 + n.length()), vs.get(i) + n.length() + 3);
				if (x.startsWith("<" + n)) {
					x = x.substring(x.indexOf(">") + 1, x.lastIndexOf("</" + n));
				}
				l.add(toObject(x));
			}
			return l;
		}
	}

	LinkedHashMap<String, String> attrs(String xnode) {
		String e = xnode.substring(xnode.indexOf("<"), xnode.indexOf(">") + 1).replaceAll(" +", " ")
				.replaceAll(" +=", "=").replaceAll("= +", "=").replaceAll("< +", "<").replaceAll(" +>", ">");
		String n = e.substring(1, e.indexOf(" ") > 0 ? e.indexOf(" ") : (e.length() - 1));
		LinkedHashMap<String, String> attrs = null;
		if (e.split(" ").length > 0) {
			attrs = new LinkedHashMap<>();
			String att = e.substring(n.length() + 2, e.length() - 1);
			List<Integer> is = new ArrayList<>();
			int quot = 0;
			char[] cs = att.toCharArray();
			for (int i = 0; i < cs.length; i++) {
				if (cs[i] == '"' && i > 0 && cs[i - 1] != '\\') {
					quot++;
				}
				if (quot != 0 && quot % 2 == 0 && cs[i] == '"') {
					is.add(i);
				}
			}
			if (is.size() > 0) {
				attrs.put(att.substring(0, att.indexOf("=")),
						att.substring(att.indexOf("=") + 1, is.get(0)).replaceFirst("\"", ""));
			}
			if (is.size() > 1) {
				for (int i = 1; i < is.size(); i++) {
					String at = att.substring(is.get(i - 1) + 1, is.get(i));
					attrs.put(at.substring(0, at.indexOf("=")).trim(),
							at.substring(at.indexOf("=") + 1, at.length()).replaceFirst("\"", "").trim());
				}
			}
		}
		return attrs;
	}
}
