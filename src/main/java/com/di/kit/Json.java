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
	boolean snakeCase = false;
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

	public Json snakeCase(boolean snakeCase) {
		this.snakeCase = snakeCase;
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

	@SuppressWarnings("unchecked")
	public static Map<String, Object> fromJson(String json) {
		return getJson().toObject(json, Map.class);
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
		} else if (o.getClass() == byte.class || o.getClass() == short.class || o.getClass() == int.class || o.getClass() == long.class || o.getClass() == double.class
				|| o.getClass() == float.class || o.getClass() == java.lang.Byte.class || o.getClass() == java.lang.Short.class || o.getClass() == java.lang.Integer.class
				|| o.getClass() == java.lang.Long.class || o.getClass() == java.lang.Double.class || o.getClass() == java.lang.Float.class || o.getClass() == boolean.class
				|| o.getClass() == java.lang.Boolean.class || o.getClass() == java.math.BigDecimal.class || o.getClass() == java.math.BigInteger.class) {
			return String.valueOf(o);
		} else if (o.getClass() == java.lang.String.class || o.getClass() == java.lang.Character.class) {
			return  String.valueOf(o);
		} else if (o.getClass() == Date.class || o.getClass() == java.sql.Date.class || o.getClass() == java.sql.Time.class) {
			return getDateFormat().format(o);
		} else if (o.getClass().isArray()) {			
			return toArrayString(o);
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
		} else if (o.getClass() == java.util.Map.class || o.getClass() == java.util.LinkedHashMap.class) {
			Map<?, ?> m0 = (Map<?, ?>) o;
			Str str = new Str().add("{");
			for (Object key : m0.keySet()) {
				str.add("\"").add(key).add("\":").add(toJson(m0.get(key))).add(",");
			}
			return str.delLastChar().add("}").toString();
		} else if ((o instanceof Object) && o.getClass() != Object.class && o.getClass() != Class.class) {
			Str str = new Str().add("{");
			try {
				for (Field f : ClassUtil.getDeclaredFields(o.getClass())) {
					if(!f.isAccessible()) f.setAccessible(true);
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
		List<T> list = new ArrayList<>();
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
				return toArray(type, ss);
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
					} else if (v != null && f.getType().isArray()) {
						Class<?> t = f.getType().getComponentType();
						List<?> vs = (List<?>) v;
						f.set(o, toArray(t, vs));
					} else {
						f.set(o, toObjectVal(v, f.getType()));
					}
				}
				return o;
			}
		} catch (ClassCastException e1) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}
	
	private String toArrayString(Object o) {
		Str str = new Str().add("[");
		if(o.getClass().getComponentType()==byte.class) {
			for (Object o0 : (byte[])o) {			
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==short.class) {
			for (Object o0 : (short[])o) {
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==int.class) {
			for (Object o0 : (int[])o) {
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==long.class) {
			for (Object o0 : (long[])o) {
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==double.class) {
			for (Object o0 : (double[])o) {
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==float.class) {
			for (Object o0 : (float[])o) {
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==boolean.class) {
			for (Object o0 : (boolean[])o) {
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==Byte.class) {
			for (Object o0 : (Byte[])o) {			
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==Short.class) {
			for (Object o0 : (Short[])o) {
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==Integer.class) {
			for (Object o0 : (Integer[])o) {
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==Long.class) {
			for (Object o0 : (Long[])o) {
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==Double.class) {
			for (Object o0 : (Double[])o) {
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==Float.class) {
			for (Object o0 : (Float[])o) {
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==Boolean.class) {
			for (Object o0 : (Boolean[])o) {
				str.add(toJson(o0)).add(",");
			}
		} else if(o.getClass().getComponentType()==String.class) {
			for (Object o0 : (String[])o) {
				str.add("\"").add(toJson(o0)).add("\",");
			}
		}
		return str.delLastChar().add("]").toString();
	}
	
	private Object toArray(Class<?> t,List<?> vs) {
		if (t == String.class) {
			String[] os = new String[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (String) toObjectVal(vs.get(i), t);
			}
			return os;
		} else if(t==byte.class){
			byte[]os=new byte[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (byte) toObjectVal(vs.get(i), t);
			}
			return os;
		} else if(t==Byte.class){
			Byte[]os=new Byte[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (Byte) toObjectVal(vs.get(i), t);
			}
			return os;
		} else if(t==short.class){
			short[]os=new short[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (short) toObjectVal(vs.get(i), t);
			}
			return os;
		}  else if(t==Short.class){
			Short[]os=new Short[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (Short) toObjectVal(vs.get(i), t);
			}
			return os;
		} else if(t==int.class){
			int[]os=new int[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (int) toObjectVal(vs.get(i), t);
			}
			return os;
		} else if(t==Integer.class){
			Integer[]os=new Integer[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (Integer) toObjectVal(vs.get(i), t);
			}
			return os;
		} else if(t==double.class){
			double[]os=new double[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (double) toObjectVal(vs.get(i), t);
			}
			return os;
		} else if(t==Double.class){
			Double[]os=new Double[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (Double) toObjectVal(vs.get(i), t);
			}
			return os;
		} else if(t==float.class){
			float[]os=new float[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (float) toObjectVal(vs.get(i), t);
			}
			return os;
		} else if(t==Float.class){
			Float[]os=new Float[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (Float) toObjectVal(vs.get(i), t);
			}
			return os;
		} else if(t==boolean.class){
			boolean[]os=new boolean[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (boolean) toObjectVal(vs.get(i), t);
			}
			return os;
		} else if(t==Boolean.class){
			Boolean[]os=new Boolean[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = (Boolean) toObjectVal(vs.get(i), t);
			}
			return os;
		} else {
			Object[] os = new Object[vs.size()];
			for (int i = 0; i < vs.size(); i++) {
				os[i] = toObjectVal(vs.get(i), t);
			}
			return os;
		}		
	}
	
	public Object toObject(String json) {
		json = json.trim();
		if (json.startsWith("{")) {
			LinkedHashMap<String, Object> m = new LinkedHashMap<>();
			String s0 = json.substring(json.indexOf("{") + 1, json.lastIndexOf("}")).trim();
			for (String s : split(s0)) {
				String k = s.substring(0, s.indexOf(":") - 1).replaceAll("\"", "").trim();
				if (snakeCase) {
					k = StringUtil.snakeCase(k);
				}
				String v = s.substring(s.indexOf(":") + 1).trim();
				if (v.startsWith("\"") && v.endsWith("\"")) {
					m.put(k, v.substring(1, v.length() - 1));
				} else {
					m.put(k, toObject(v));
				}
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
		} else {
			if (json.contains(".")) {
				return Double.valueOf(json);
			} else if (json.equalsIgnoreCase("true") || json.equalsIgnoreCase("false")) {
				return Boolean.valueOf(json);
			} else if (json.length() < 12) {
				return Integer.valueOf(json);
			} else if (json.length() < 20) {
				return Long.valueOf(json);
			} else {
				return json.replaceAll("\"", "");
			}
		}
	}
}
