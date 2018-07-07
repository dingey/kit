package com.di.kit;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author di
 */
@SuppressWarnings("all")
public class ClassUtil {
    private static HashMap<Class, Constructor<?>> constructorMap = new HashMap<>();

    public static boolean isUserClass(Class<?> c) {
        return !c.isPrimitive() && c != Byte.class && c != Short.class && c != Integer.class && c != Long.class && c != Double.class && c != Float.class && c != Character.class && c != String.class && c != Boolean.class && c != Date.class && c != java.sql.Date.class && !c.isInterface() && !c.isEnum() && c != Object.class && c != Class.class;
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

    public static <T> void setFieldValueBySetMethod(String fieldName, T t, Object val) {
        try {
            Method m = getDeclaredMethod(t.getClass(), "set" + StringUtil.firstUpper(fieldName));
            if (m != null)
                m.invoke(t, val);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static <T> void setFieldValueBySetMethod(Field f, T t, Object val) {
        setFieldValueBySetMethod(f.getName(), t, val);
    }

    public static <T> Object getFieldValueByGetMethod(String fieldName, T t) {
        try {
            Method m = getDeclaredMethod(t.getClass(), "get" + StringUtil.firstUpper(fieldName));
            if (m == null)
                m = getDeclaredMethod(t.getClass(), "is" + StringUtil.firstUpper(fieldName));
            if (m != null)
                return m.invoke(t);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> Object getFieldValueByGetMethod(Field f, T t) {
        return getFieldValueByGetMethod(f.getName(), t);
    }

    public static <T> T getObjectInstance(Map<String, Object> value, Class<T> o) {
        Object instance = instance(o);
        if (instance != null) {
            setObjectFieldsValue(value, instance);
            return (T) instance;
        }
        return null;
    }

    public static <T> void setObjectFieldsValue(Map<String, Object> m, T o) {
        if (m == null)
            return;
        if (o == null)
            throw new RuntimeException("The second parameter cannot be null.");
        try {
            for (Field f : getDeclaredFields(o.getClass())) {
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
                } else if (f.getType() == BigDecimal.class) {
                    f.set(o, new BigDecimal(m.get(f.getName()).toString()));
                } else if (f.getType() == java.lang.String.class) {
                    f.set(o, m.get(f.getName()).toString());
                } else if (f.getType() == java.sql.Date.class || f.getType() == java.sql.Time.class || f.getType() == java.sql.Timestamp.class) {
                    f.set(o, m.get(f.getName()));
                } else if (f.getType() == java.util.Date.class) {
                    if (m.get(f.getName()).getClass() == java.lang.String.class) {
                        try {
                            f.set(o, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(m.get(f.getName()).toString()));
                        } catch (ParseException e) {
                            f.set(o, new SimpleDateFormat("yyyy-MM-dd").parse(m.get(f.getName()).toString()));
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
                } else if (f.getType().isArray()) {
                    Class<?> type = getFieldArrayType(f);
                    Object[] os = (Object[]) m.get(f.getName());
                    Object[] tos = new Object[os.length];
                    for (int i = 0; i < os.length; i++) {
                        Map<String, Object> m0 = (Map<String, Object>) os[i];
                        Object o0 = Class.forName(type.getName()).newInstance();
                        setObjectFieldsValue(m0, o0);
                        tos[i] = o0;
                    }
                    f.set(o, tos);
                } else if (f.getType() != null) {
                    Object fo = f.getType().newInstance();
                    Map m0 = (Map) m.get(f.getName());
                    setObjectFieldsValue(m0, fo);
                    f.set(o, fo);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Field getDeclaredField(Class<T> t, String fieldName) {
        for (Class<?> clazz = t; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static List<Field> getDeclaredFields(Class<?> t) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> clazz = t; clazz != Object.class && clazz != Class.class && clazz != Field.class; clazz = clazz.getSuperclass()) {
            try {
                for (Field f : clazz.getDeclaredFields()) {
                    int modifiers = f.getModifiers();
                    if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isNative(modifiers) && !Modifier.isTransient(modifiers)) {
                        fields.add(f);
                    }
                }
            } catch (Exception e) {
            }
        }
        return fields;
    }

    public static <T> Method getDeclaredMethod(Class<T> t, String methodName, Class<?>... parameterTypes) {
        for (Class<?> clazz = t; clazz != Object.class && clazz != Class.class; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static <T> List<Method> getDeclaredMethods(Class<T> t) {
        List<Method> methods = new ArrayList<>();
        for (Class<?> clazz = t; clazz != Object.class && clazz != Class.class; clazz = clazz.getSuperclass()) {
            try {
                for (Method m : clazz.getDeclaredMethods()) {
                    if (ModifierUtil.isCommon(m.getModifiers())) {
                        if (!m.isAccessible())
                            m.setAccessible(true);
                        methods.add(m);
                    }
                }
            } catch (Exception e) {
            }
        }
        return methods;
    }

    public static Class<?> getByClassName(String className) {
        switch (className) {
            case "byte.class":
                return byte.class;
            case "short.class":
                return short.class;
            case "int.class":
                return int.class;
            case "long.class":
                return long.class;
            case "double.class":
                return double.class;
            case "float.class":
                return float.class;
            case "boolean.class":
                return boolean.class;
            case "char.class":
                return char.class;
            case "String.class":
                return String.class;
            default:
                try {
                    return Class.forName(className);
                } catch (ClassNotFoundException ignored) {
                }
                break;
        }
        return null;
    }

    public static Class<?> getFieldListType(Field f) {
        ParameterizedType pt = (ParameterizedType) f.getGenericType();
        Type t = pt.getActualTypeArguments()[0];
        return (Class<?>) t;
    }

    public static boolean isListField(Field f) {
        return f.getType() == List.class || f.getType() == Collection.class || f.getType() == ArrayList.class;
    }

    public static Object instance(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == byte.class) {
                return (byte) 0;
            } else if (clazz == short.class) {
                return (short) 0;
            } else if (clazz == int.class) {
                return 0;
            } else if (clazz == long.class) {
                return (long) 0;
            } else if (clazz == double.class) {
                return 0d;
            } else if (clazz == float.class) {
                return 0f;
            } else if (clazz == boolean.class) {
                return false;
            } else if (clazz == char.class) {
                return '\0';
            }
        } else if (clazz.isArray()) {
            return null;
        }
        Constructor constructor = null;
        if (constructorMap.containsKey(clazz)) {
            constructor = constructorMap.get(clazz);
            if (constructor == null)
                return null;
        } else {
            Constructor<?>[] constructors = clazz.getConstructors();
            if (constructors == null || constructors.length == 0)
                return null;
            constructor = constructors[0];
            if (constructors.length > 1) {
                for (int i = 1; i < constructors.length; i++) {
                    if (constructor.getParameterCount() > constructors[i].getParameterCount())
                        constructor = constructors[i];
                }
            }
            constructorMap.put(clazz, constructor);
        }
        int count = constructor.getParameterCount();
        try {
            if (count == 0)
                return constructor.newInstance(null);
            if (count > 0) {
                Class[] types = constructor.getParameterTypes();
                Object[] params = new Object[count];
                for (int i = 0; i < count; i++) {
                    params[i] = instance(types[i]);
                }
                return constructor.newInstance(params);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Class<?> getFieldArrayType(Field f) {
        return f.getType().getComponentType();
    }

    public static Class<?>[] getFieldGenericType(Field f) {
        ParameterizedType pt = (ParameterizedType) f.getGenericType();
        return (Class<?>[]) pt.getActualTypeArguments();
    }

    public static Class<?>[] getMethodReturnGenericType(Method method) {
        Type type = method.getGenericReturnType();
        Class<?> returnType = method.getReturnType();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            Class<?>[] cs = new Class[types.length];
            for (int i = 0; i < types.length; i++) {
                cs[i] = (Class<?>) types[i];
            }
            return cs;
        } else if (returnType.isArray()) {
            Class<?> type2 = returnType.getComponentType();
            return new Class<?>[]{type2};
        }
        return null;
    }

    public static Class<?>[] getClassGenericType(Class<?> entity) {
        ParameterizedType pt = (ParameterizedType) entity.getGenericSuperclass();
        return (Class<?>[]) pt.getActualTypeArguments();
    }
}
