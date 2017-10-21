package com.di.kit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.di.kit.data.annotation.Alias;

/**
 * @author di
 */
public class XmlUtil {
	private static String start = "<![CDATA[";
	private static String end = "]]>";

	public static <T> String toXml(T o) {
		String n = StringUtil.firstCharLower(o.getClass().getSimpleName());
		if (o.getClass().isAnnotationPresent(Alias.class)) {
			if (!o.getClass().getAnnotation(Alias.class).xml().isEmpty()) {
				n = o.getClass().getAnnotation(Alias.class).xml();
			} else if (!o.getClass().getAnnotation(Alias.class).value().isEmpty()) {
				n = o.getClass().getAnnotation(Alias.class).value();
			}
		}
		Str str = new Str();
		str.add("<").add(n).add(">");
		if (o.getClass() == byte.class || o.getClass() == short.class || o.getClass() == int.class
				|| o.getClass() == long.class || o.getClass() == double.class || o.getClass() == float.class
				|| o.getClass() == java.lang.Byte.class || o.getClass() == java.lang.Short.class
				|| o.getClass() == java.lang.Integer.class || o.getClass() == java.lang.Long.class
				|| o.getClass() == java.lang.Double.class || o.getClass() == java.lang.Float.class
				|| o.getClass() == boolean.class || o.getClass() == java.lang.Boolean.class
				|| o.getClass() == java.lang.String.class || o.getClass() == java.lang.Character.class) {
			str.add(o).add("</").add(n).add(">");
			return str.toString();
		} else if (o.getClass().isArray()) {
			str = new Str().add("<").add(n).add(">");
			List<?> os = (List<?>) o;
			for (Object o0 : os) {
				str.add(toXml(o0));
			}
			str.add("</").add(n).add(">");
			return str.toString();
		} else if (o.getClass() == java.util.List.class || o.getClass() == java.util.ArrayList.class) {
			str = new Str().add("<").add(n).add(">");
			List<?> os = (List<?>) o;
			for (Object o0 : os) {
				str.add(toXml(o0));
			}
			str.add("</").add(n).add(">");
			return str.toString();
		} else if (o.getClass() == java.util.Map.class || o.getClass() == java.util.LinkedHashMap.class) {
			Map<?, ?> m0 = (Map<?, ?>) o;
			for (Object key : m0.keySet()) {
				str.add("<").add(key).add(">").add(toXml(m0.get(key))).add("</").add(key).add(">");
			}
		} else if (o instanceof Object) {
			try {
				for (Field f : ClassUtil.getDeclaredFields(o.getClass())) {
					f.setAccessible(true);
					if (f.get(o) == null) {
						continue;
					}
					String n0 = f.getName();
					if (n0.equals("attributes")) {
						Object attr = f.get(o);
						Str str1 = new Str();
						for (Field ff : attr.getClass().getDeclaredFields()) {
							ff.setAccessible(true);
							String nn = ff.getName();
							if (ff.isAnnotationPresent(Alias.class)) {
								if (!ff.getAnnotation(Alias.class).xml().isEmpty()) {
									nn = ff.getAnnotation(Alias.class).xml();
								} else if (!ff.getAnnotation(Alias.class).value().isEmpty()) {
									nn = ff.getAnnotation(Alias.class).value();
								}
							}
							str1.add(nn).add("=\"").add(ff.get(attr)).add("\" ");
						}
						str.replaceFirst("<" + n + ">", "<" + n + " " + str1.deleteLastChar().toString() + ">");
						continue;
					}
					if (f.isAnnotationPresent(Alias.class)) {
						if (!f.getAnnotation(Alias.class).xml().isEmpty()) {
							n0 = f.getAnnotation(Alias.class).xml();
						} else if (!f.getAnnotation(Alias.class).value().isEmpty()) {
							n0 = f.getAnnotation(Alias.class).value();
						}
					}
					if (Modifier.isFinal(f.getModifiers())) {
						continue;
					} else if (f.getType() == byte.class || f.getType() == short.class || f.getType() == int.class
							|| f.getType() == long.class || f.getType() == double.class || f.getType() == float.class
							|| f.getType() == java.lang.Byte.class || f.getType() == java.lang.Short.class
							|| f.getType() == java.lang.Integer.class || f.getType() == java.lang.Long.class
							|| f.getType() == java.lang.Double.class || f.getType() == java.lang.Float.class
							|| f.getType() == boolean.class || f.getType() == java.lang.Boolean.class
							|| f.getType() == java.lang.String.class || f.getType() == java.lang.Character.class) {
						str.add("<").add(n0).add(">").add(f.get(o)).add("</").add(n0).add(">");
					} else if (f.getType() == java.util.List.class || f.getType() == java.util.ArrayList.class) {
						str.add("<").add(n0).add(">");
						List<?> os = (List<?>) f.get(o);
						for (Object o0 : os) {
							str.add(toXml(o0));
						}
						str.add("</").add(n0).add(">");
					} else if (f.getType() == java.util.Map.class || f.getType() == java.util.LinkedHashMap.class) {
						Map<?, ?> m0 = (Map<?, ?>) f.get(o);
						str.add("<").add(n0).add(">");
						for (Object key : m0.keySet()) {
							str.add("<").add(key).add(">").add(m0.get(key)).add("</").add(key).add(">");
						}
						str.add("</").add(n0).add(">");
					} else if (f.getDeclaringClass() == Object.class) {
						continue;
					} else if (f.getType() instanceof Object) {
						str.add(toXml(f.get(o)));
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		str.add("</").add(n).add(">");
		return str.toString();
	}

	@SuppressWarnings("unchecked")
	public static <T> T toObject(String xml, Class<T> cl) {
		T o = null;
		if (cl == java.util.Collection.class || cl == java.util.List.class || cl == java.util.ArrayList.class) {
			List<Object> l = toList(xml);
			return (T) l;
		} else if (cl == java.util.Map.class || cl == java.util.LinkedHashMap.class) {
			Map<String, Object> m = toMap(xml);
			o = (T) m;
		} else {
			try {
				Map<String, Object> m = toMap(xml);
				o = cl.newInstance();
				set(m, o);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return o;
	}

	private static <T> void set(Map<String, Object> m, T o) {
		ClassUtil.setObjectFieldsValue(m, o);
	}

	public static Map<String, Object> toMap(String xml) {
		if(xml.startsWith("<?xml")){
			xml=xml.substring(xml.indexOf("?>")+2).trim();
		}
		if(xml.startsWith("<!DOCTYPE")){
			xml=xml.substring(xml.indexOf(">")+1).trim();
		}
		Map<String, Object> m = new LinkedHashMap<>();
		//m.put("attributes", getAttributes(xml));
		//m.put("element name", getWrapperName(xml));
		put(m,"element attributes",getAttributes(xml));
		put(m,"element name",getWrapperName(xml));
		if (getWrapperValue(xml).indexOf("<") == -1) {
			m.put(getWrapperName(xml), getWrapperValue(xml));
			return m;
		}
		xml = getWrapperValue(xml);
		List<String> split = split(xml);
		for (String s : split) {
			String name = getWrapperName(s);
			if (getAttributes(s) != null) {
				// m.put(name, toMap(s));
				put(m, name, toMap(s));
				continue;
			}
			String value = getWrapperValue(s);
			if (!value.startsWith("<") && !value.startsWith(start)) {
				//m.put(name, value);
				put(m, name, value);
			} else if (value.startsWith(start)) {
				//m.put(name, replaceEscape(value));
				put(m, name, replaceEscape(value));
			} else if (value.startsWith("<") && !isList(value)) {
				//m.put(name, toMap(s));
				put(m, name,toMap(s));
			} else if (isList(value)) {
				//m.put(name, toList(value));
				put(m, name, toList(value));
			}
		}
		return m;
	}

	@SuppressWarnings("unchecked")
	private static void put(Map<String, Object> m, String k, Object v) {
		if (m.containsKey(k)) {
			Object val = m.get(k);
			if (val.getClass() == ArrayList.class || val.getClass() == List.class) {
				List<Object> os = (List<Object>) val;
				os.add(v);
				m.put(k, os);
			} else {
				List<Object> os = new ArrayList<>();
				os.add(val);
				os.add(v);
				m.put(k, os);
			}			
		} else {
			m.put(k, v);
		}
	}

	private static boolean isList(String xml) {
		String n1 = getWrapperName(xml);
		if (xml.indexOf("</" + n1 + ">") == -1) {
			return false;
		} else if (xml.substring(xml.indexOf("</" + n1 + ">")).indexOf("<" + n1 + ">") > -1) {
			return true;
		}
		return false;
	}

	private static List<Object> toList(String xml) {
		List<Object> ls = new ArrayList<>();
		for (String s : split(xml)) {
			if (isList(s)) {
				ls.add(toList(s));
			} else {
				ls.add(toMap(s));
			}
		}
		return ls;
	}

	private static Map<String, String> getAttributes(String xml) {
		String s = sub(xml,"<",">");
		String[] ss = s.split(" ");
		if (ss.length == 1) {
			return null;
		}
		Map<String, String> m = new LinkedHashMap<>();
		for (int i = 1; i < ss.length; i++) {
			String s0 = ss[i].trim();
			if (!s0.isEmpty()&&s0.split("=").length>1) {
				m.put(s0.split("=")[0], delQuot(s0.split("=")[1]));
			}
		}
		return m;
	}
	
	private static String sub(String s,String start,String end){
		int s1=s.indexOf(start)+start.length(),s2=s.indexOf(end);
		if(0<s1&&s1<s2){
			return s.substring(s1, s2);
		}else{
			return s.replaceFirst("<", "").replaceFirst(">", "");
		}
	}

	private static String delQuot(String s) {
		int start=0,end=s.length();
		if(s.indexOf('"')>0){
			start=s.indexOf('"');
		}
		if(s.lastIndexOf('"')>start){
			end=s.lastIndexOf('"');
		}
		return s.substring(start+1,end);
	}

	private static String getWrapperName(String xml) {
		return xml.substring(xml.indexOf("<") > -1 ? (xml.indexOf("<") + 1) : 0,
				xml.indexOf(">") > -1 ? xml.indexOf(">") : xml.length()).split(" ")[0];
	}

	private static String getWrapperValue(String xml) {
		return xml.substring(xml.indexOf(">") > -1 ? (xml.indexOf(">") + 1) : 0,
				xml.lastIndexOf("<") > -1 ? xml.lastIndexOf("<") : xml.length());
	}

	private static String replaceEscape(String xml) {
		return xml.substring(xml.indexOf(start) + 9, xml.lastIndexOf(end));
	}

	private static List<String> split(String xml) {
		int i = 0;
		int left = 0;// <
		int right = 0;// >
		int close = 0;// </ left+close=right;
		char[] cs = xml.toCharArray();
		List<Integer> ls = new ArrayList<Integer>();
		while (i < cs.length) {
			switch (cs[i]) {
			case '<':
				if (i == 0 || xml.indexOf(start, i - 1) != i) {
					if (cs[i + 1] != '/') {
						left++;
					} else {
						close++;
					}
				} else if (xml.indexOf(start, i - 1) == i) {
					i = xml.indexOf(end, i) + 3;
					continue;
				}
				break;
			case '>':
				if (cs[i - 1] != ']' && cs[i - 2] != ']') {
					right++;
				}
				if ((left + close) == right && left == close) {
					ls.add(i);
				}
				break;
			default:
				break;
			}
			i++;
		}
		List<String> ss = new ArrayList<String>();
		if (ls.size() == 0) {
			ss.add(xml);
		} else if (ls.size() == 1) {
			ss.add(xml.substring(0, ls.get(0)));
			ss.add(xml.substring(ls.get(0) + 1));
		} else if (ls.size() > 1) {
			ss.add(xml.substring(0, ls.get(0) + 1));
			for (int j = 0; j < ls.size() - 1; j++) {
				ss.add(xml.substring(ls.get(j) + 1, ls.get(j + 1) + 1));
			}
		}
		return ss;
	}

	public static enum XmlEscape {
		AND('&', "a"), LESS('<', "&lt;"), GREAT('>', "&gt;"), QUOT('"', "&quot;"), APOS('\'', "&apos;");
		private char ch;
		private String value;

		private XmlEscape(char ch, String value) {
			this.value = value;
			this.ch = ch;
		}

		public static String escape(char c) {
			for (XmlEscape xe : XmlEscape.values()) {
				if (xe.ch == c) {
					return xe.value;
				}
			}
			return null;
		}

		public static char unEscape(String s) {
			for (XmlEscape xe : XmlEscape.values()) {
				if (xe.value.equals(s)) {
					return xe.ch;
				}
			}
			return ' ';
		}
	}

	public static void createFromXml(String xml, String packag) {
		ClassCreate.createFromXml(xml, packag, "");
	}
}
