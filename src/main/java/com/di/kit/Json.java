package com.di.kit;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author di
 */
public class Json {
	SimpleDateFormat sdf;
	boolean camelCaseToUnderscores = false;
	static Json json;

	public Json setDateFormat(String pattern) {
		this.sdf = new SimpleDateFormat(pattern);
		return this;
	}

	private SimpleDateFormat getDateFormat() {
		if (sdf == null) {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		return sdf;
	}

	public Json setCamelCaseToUnderscores(boolean camelCaseToUnderscores) {
		this.camelCaseToUnderscores = camelCaseToUnderscores;
		return this;
	}

	public static Json getJson() {
		if (json == null) {
			json = new Json();
		}
		return json;
	}

	private List<String> split(String json) {
		int off = 0;
		int ang1 = 0;// {
		int ang2 = 0;// }
		int squa1 = 0;// [
		int squa2 = 0;// ]
		int quot = 0;// "
		List<Integer> is = new ArrayList<>();
		List<String> ss = new ArrayList<>();
		while (off < json.length()) {
			char c = json.charAt(off);
			switch (c) {
			case '"':
				if (off == 0 || json.charAt(off - 1) != '\\') {
					quot++;
				}
				break;
			case '{':
				if (quot % 2 == 0) {
					ang1++;
				}
				break;
			case '}':
				if (quot % 2 == 0) {
					ang2++;
				}
				break;
			case '[':
				if (quot % 2 == 0) {
					squa1++;
				}
				break;
			case ']':
				if (quot % 2 == 0) {
					squa2++;
				}
				break;
			case ',':
				if (quot % 2 == 0 && ang1 == ang2 && squa1 == squa2) {
					is.add(off);
				}
				break;
			default:
				break;
			}
			off++;
		}
		if (is.size() > 1) {
			ss.add(json.substring(0, is.get(0)));
			for (int i = 0; i < is.size() - 1; i++) {
				ss.add(json.substring(is.get(i) + 1, is.get(i + 1)));
			}
			ss.add(json.substring(is.get(is.size() - 1) + 1));
		} else if (is.size() == 1) {
			ss.add(json.substring(0, is.get(0)));
			ss.add(json.substring(is.get(0) + 1));
		} else {
			ss.add(json);
		}
		return ss;
	}

	public static <T> T fromJson(String json, Class<T> c) {
		return getJson().toObject(json, c);
	}

	public static String toJsonString(Object o) {
		return getJson().toJson(o);
	}

	public <T> String toJson(T o) {
		if (o == null) {
			return null;
		} else if (o.getClass() == byte.class || o.getClass() == short.class || o.getClass() == int.class
				|| o.getClass() == long.class || o.getClass() == double.class || o.getClass() == float.class
				|| o.getClass() == java.lang.Byte.class || o.getClass() == java.lang.Short.class
				|| o.getClass() == java.lang.Integer.class || o.getClass() == java.lang.Long.class
				|| o.getClass() == java.lang.Double.class || o.getClass() == java.lang.Float.class
				|| o.getClass() == boolean.class || o.getClass() == java.lang.Boolean.class
				|| o.getClass() == java.lang.String.class || o.getClass() == java.lang.Character.class) {
			return String.valueOf(o);
		} else if (o.getClass() == Date.class || o.getClass() == java.sql.Date.class
				|| o.getClass() == java.sql.Time.class) {
			return getDateFormat().format(o);
		} else if (o.getClass().isArray()) {
			Str str = new Str().add("[");
			Object[] os = (Object[]) o;
			for (Object o0 : os) {
				if (o0.getClass() == String.class || o0.getClass() == Date.class) {
					str.add("\"").add(toJson(o0)).add("\",");
				} else {
					str.add(toJson(o0)).add(",");
				}
			}
			return str.delLastChar().add("]").toString();
		} else if (o.getClass() == java.util.List.class || o.getClass() == java.util.ArrayList.class) {
			Str str = new Str().add("[");
			List<?> os = (List<?>) o;
			for (Object o0 : os) {
				if (o0.getClass() == String.class || o0.getClass() == Date.class) {
					str.add("\"").add(toJson(o0)).add("\",");
				} else {
					str.add(toJson(o0)).add(",");
				}
			}
			return str.delLastChar().add("]").toString();
		} else if ((o instanceof Object) && o.getClass() != Object.class && o.getClass() != Class.class) {
			Str str = new Str().add("{");
			try {
				for (Field f : ClassUtil.getDeclaredFields(o.getClass())) {
					f.setAccessible(true);
					if (f.get(o) == null) {
						continue;
					}
					String n0 = f.getName();
					Object v = f.get(o);
					if (f.getType() == String.class || f.getType() == Date.class) {
						str.add("\"").add(n0).add("\":\"").add(toJson(v)).add("\",");
					} else {
						str.add("\"").add(n0).add("\":").add(toJson(v)).add(",");
					}
				}
				str.deleteLastChar();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return str.add("}").toString();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T toObject(String json, Class<T> c) {
		if (json == null && !c.isPrimitive()) {
			return null;
		} else if ((json == null || json.isEmpty()) && c.isPrimitive()) {
			throw new IllegalArgumentException("json is null or empty that can't be transform to primitive Class.");
		}
		T o = null;
		json = json.trim();
		o = (T) toObjectVal(toObject(json), c);
		return o;
	}
	
	public <T> List<T> toObjects(String json, Class<T> c) {
		if (json == null && !c.isPrimitive()) {
			return null;
		} else if ((json == null || json.isEmpty()) && c.isPrimitive()) {
			throw new IllegalArgumentException("json is null or empty that can't be transform to primitive Class.");
		}
		List<T> list=new ArrayList<>();
		json = json.trim();
		toObjectVal(toObject(json), List.class);
		return list;
	}

	@SuppressWarnings("unchecked")
	private Object toObjectVal(Object val, Class<?> c) {
		try {
			if (val == null) {
				return null;
			} else if (c == boolean.class || c == Boolean.class) {
				return Boolean.valueOf(String.valueOf(val));
			} else if (c == char.class || c == Character.class) {
				return new Character(String.valueOf(val).charAt(0));
			} else if (c == byte.class || c == Byte.class) {
				return Byte.valueOf(String.valueOf(val));
			} else if (c == short.class || c == Short.class) {
				return Short.valueOf(String.valueOf(val));
			} else if (c == int.class || c == Integer.class) {
				return Integer.valueOf(String.valueOf(val));
			} else if (c == long.class || c == Long.class) {
				return Long.valueOf(String.valueOf(val));
			} else if (c == float.class || c == Float.class) {
				return Float.valueOf(String.valueOf(val));
			} else if (c == double.class || c == Double.class) {
				return Double.valueOf(String.valueOf(val));
			} else if (c == Date.class) {
				return getDateFormat().parse(String.valueOf(val));
			} else if (c == java.sql.Date.class) {
				return new java.sql.Date(getDateFormat().parse(String.valueOf(val)).getTime());
			} else if (c == Time.class) {
				return new Time(getDateFormat().parse(String.valueOf(val)).getTime());
			} else if (c == String.class) {
				return String.valueOf(val);
			} else if (c.isArray() && val != null) {
				List<Object> ss = (List<Object>) val;
				Class<?> type = c.getComponentType();
				if (type == String.class) {
					String[] os = new String[ss.size()];
					for (int i = 0; i < os.length; i++) {
						os[i] = toObject(String.valueOf(ss.get(i)), String.class);
					}
					return os;
				} else {
					Object[] os = new Object[ss.size()];
					for (int i = 0; i < os.length; i++) {
						os[i] = toObject(String.valueOf(ss.get(i)), type);
					}
					return os;
				}
			} else if (c == List.class || c == ArrayList.class) {
				return val;
			} else if (c == Map.class || c == HashMap.class || c == LinkedHashMap.class) {
				return val;
			} else if (c != Object.class && c != Class.class && (c instanceof Object) && val != null) {
				Map<String, Object> map = (Map<String, Object>) val;
				Object o = c.newInstance();
				for (Field f : ClassUtil.getDeclaredFields(c)) {
					if (!f.isAccessible()) {
						f.setAccessible(true);
					}
					Object v = map.get(f.getName());
					if (v != null && (f.getType() == List.class || f.getType() == ArrayList.class)) {
						ParameterizedType pt = (ParameterizedType) f.getGenericType();
						Type t = pt.getActualTypeArguments()[0];
						List<?> vs = (List<?>) v;
						ArrayList<Object> ts = new ArrayList<>();
						for (Object ov : vs) {
							ts.add(toObjectVal(ov, (Class<?>) t));
						}
						f.set(o, ts);
					} else {
						f.set(o, toObjectVal(v, f.getType()));
					}
				}
				return o;
			}
		} catch(ClassCastException e1){return null;}catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

	public Object toObject(String json) {
		json = json.trim();
		if (json.startsWith("{")) {
			LinkedHashMap<String, Object> m = new LinkedHashMap<>();
			String s0 = json.substring(json.indexOf("{") + 1, json.lastIndexOf("}")).trim();
			for (String s : split(s0)) {
				String k = s.substring(0, s.indexOf(":") - 1).replaceAll("\"", "").trim();
				if (camelCaseToUnderscores) {
					k = StringUtil.camelCaseToUnderline(k);
				}
				String v = s.substring(s.indexOf(":") + 1).trim();
				m.put(k, toObject(v));
			}
			return m;
		} else if (json.startsWith("[")) {
			String s0 = json.substring(json.indexOf("[") + 1, json.lastIndexOf("]")).trim();
			List<Object> ls = new ArrayList<>();
			if (s0.startsWith("{") || s0.startsWith("[")) {
				for (String s1 : split(s0)) {
					ls.add(toObject(s1));
				}
			} else {
				for (String s : s0.split(",")) {
					ls.add(s.replaceAll("\"", ""));
				}
			}
			return ls;
		}
		return json.replaceAll("\"", "");
	}
}
