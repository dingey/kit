package com.di.kit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author di
 */
public class ClassUtil {
	public static boolean isUserClass(Class<?> c) {
		return !c.isPrimitive() && c != Byte.class && c != Short.class && c != Integer.class && c != Long.class
				&& c != Double.class && c != Float.class && c != Character.class && c != Boolean.class
				&& c != Date.class && c != java.sql.Date.class && !c.isInterface() && !c.isEnum()
				&& (c instanceof Object) && c != Object.class && c != Class.class;
	}

	public static boolean isJdkClass(Class<?> clz) {
		return clz != null && clz.getClassLoader() == null;
	}

	public static <T> Object invokeMethod(Method m, T o, Object... args) {
		try {
			return m.invoke(o, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String, Object> getBeanFieldsMap(Object o) {
		Map<String, Object> m = new HashMap<>();
		for (Field f : o.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			try {
				m.put(f.getName(), f.get(o));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return m;
	}

	public static <T> Object getFieldValue(Field f, T t) {
		try {
			f.setAccessible(true);
			return f.get(t);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> Object getFieldValue(String fieldName, T t) {
		try {
			Field f = getDeclaredField(t.getClass(), fieldName);
			f.setAccessible(true);
			return f.get(t);
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> Object getFieldValueByGetMethod(Field f, T t) {
		try {
			Method m = null;
			if (f.getType() == boolean.class || f.getType() == Boolean.class) {
				m = getDeclaredMethod(t.getClass(), "is" + StringUtil.firstCharUpper(f.getName()));
			} else {
				m = getDeclaredMethod(t.getClass(), "get" + StringUtil.firstCharUpper(f.getName()));
			}
			return m.invoke(t);
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> Object getFieldValueByGetMethod(String fieldName, T t) {
		try {
			return getFieldValueByGetMethod(getDeclaredField(t.getClass(), fieldName), t);
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> void setObjectFieldsValue(Map<String, Object> m, T o) {
		try {
			for (Field f : o.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				if (m.get(f.getName()) == null) {
					continue;
				}
				if (f.getType() == boolean.class || f.getType() == java.lang.Boolean.class) {
					f.set(o, Boolean.valueOf(m.get(f.getName()).toString()));
				} else if (f.getType() == byte.class || f.getType() == java.lang.Byte.class) {
					f.set(o, Byte.valueOf(m.get(f.getName()).toString()));
				} else if (f.getType() == short.class || f.getType() == java.lang.Short.class) {
					f.set(o, Short.valueOf(m.get(f.getName()).toString()));
				} else if (f.getType() == int.class || f.getType() == java.lang.Integer.class) {
					f.set(o, Integer.valueOf(m.get(f.getName()).toString()));
				} else if (f.getType() == long.class || f.getType() == java.lang.Long.class) {
					f.set(o, Long.valueOf(m.get(f.getName()).toString()));
				} else if (f.getType() == double.class || f.getType() == java.lang.Double.class) {
					f.set(o, Double.valueOf(m.get(f.getName()).toString()));
				} else if (f.getType() == float.class || f.getType() == java.lang.Float.class) {
					f.set(o, Float.valueOf(m.get(f.getName()).toString()));
				} else if (f.getType() == char.class || f.getType() == java.lang.Character.class) {
					f.set(o, m.get(f.getName()));
				} else if (f.getType() == java.lang.String.class) {
					f.set(o, m.get(f.getName()).toString());
				} else if (f.getType() == java.util.Date.class) {
					if (m.get(f.getName()).getClass() == java.lang.String.class) {
						try {
							f.set(o, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(m.get(f.getName()).toString()));
						} catch (ParseException e) {
							try {
								f.set(o, new SimpleDateFormat("yyyy-MM-dd").parse(m.get(f.getName()).toString()));
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
							e.printStackTrace();
						}
					} else {
						f.set(o, m.get(f.getName()));
					}
				} else if (f.getType() == java.util.List.class || f.getType() == java.util.ArrayList.class) {
					Type type = f.getGenericType();
					ParameterizedType pt = (ParameterizedType) type;
					Type type2 = pt.getActualTypeArguments()[0];
					String typeName = type2.getTypeName();
					List<Object> os = (List<Object>) m.get(f.getName());
					List<Object> os_ = new ArrayList<>();
					for (Object oo : os) {
						Map<String, Object> m0 = (Map<String, Object>) oo;
						Object o0 = Class.forName(typeName).newInstance();
						setObjectFieldsValue(m0, o0);
						os_.add(o0);
					}
					f.set(o, os_);
				} else if (f.getType() instanceof Object) {
					Object fo;
					fo = f.getType().newInstance();
					Map<String, Object> m0 = (Map<String, Object>) m.get(f.getName());
					setObjectFieldsValue(m0, fo);
					f.set(o, fo);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static <T> Field getDeclaredField(Class<T> t, String fieldName) {
		Class<?> clazz = t;
		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				return clazz.getDeclaredField(fieldName);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static List<Field> getDeclaredFields(Class<?> t) {
		Class<?> clazz = t;
		List<Field> fields = new ArrayList<>();
		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				for (Field f : clazz.getDeclaredFields()) {
					int modifiers = f.getModifiers();
					if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isNative(modifiers)
							&& !Modifier.isTransient(modifiers)) {
						fields.add(f);
					}
				}
			} catch (Exception e) {
			}
		}
		return fields;
	}

	public static <T> Method getDeclaredMethod(Class<T> t, String methodName, Class<?>... parameterTypes) {
		for (Class<?> clazz = t; clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				return clazz.getDeclaredMethod(methodName, parameterTypes);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static <T> List<Method> getDeclaredMethods(Class<T> t) {
		List<Method> methods = new ArrayList<>();
		for (Class<?> clazz = t; clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				for (Method m : clazz.getDeclaredMethods()) {
					if (ModifierUtil.isCommon(m.getModifiers())) {
						methods.add(m);
					}
				}
				// methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
			} catch (Exception e) {
			}
		}
		return methods;
	}

	public static Class<?> getByClassName(String className) {
		if (className.equals("byte.class")) {
			return byte.class;
		} else if (className.equals("short.class")) {
			return short.class;
		} else if (className.equals("int.class")) {
			return int.class;
		} else if (className.equals("long.class")) {
			return long.class;
		} else if (className.equals("double.class")) {
			return double.class;
		} else if (className.equals("float.class")) {
			return float.class;
		} else if (className.equals("boolean.class")) {
			return boolean.class;
		} else if (className.equals("char.class")) {
			return char.class;
		} else if (className.equals("String.class")) {
			return String.class;
		} else {
			try {
				return Class.forName(className);
			} catch (ClassNotFoundException e) {
			}
		}
		return null;
	}
}
