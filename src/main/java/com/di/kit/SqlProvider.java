package com.di.kit;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author d
 */
public class SqlProvider {
	static final HashMap<String, String> sqls = new HashMap<>();
	static final HashMap<String, List<Field>> modelFieldsMap = new HashMap<>();
	static final HashMap<Class<?>, Field> idFieldsMap = new HashMap<>();

	public String insert(Object bean) {
		return getCachedSql(bean, "insert", new Func<Object>() {
			@Override
			public String apply(Object t) {
				return getInsertSql(bean, false);
			}
		});
	}

	public String insertSelective(Object bean) {
		return getInsertSql(bean, true);
	}

	public String getInsertSql(Object bean, boolean selective) {
		String tableName = table(bean);
		StringBuilder sql = new StringBuilder();
		List<String> props = new ArrayList<>();
		List<String> columns = new ArrayList<>();
		sql.append("INSERT INTO ").append(tableName).append("(");
		try {
			for (Field field : getCachedModelFields(bean.getClass())) {
				if (selective) {
					Object value = field.get(bean);
					if (value == null) {
						continue;
					}
				}
				if (field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(IgnoreInsert.class)) {
					continue;
				}
				columns.add(StringUtil.snakeCase(field.getName()));
				props.add("#{" + field.getName() + "}");
			}
		} catch (Exception e) {
			new RuntimeException(sql.toString(), e);
		}
		for (int i = 0; i < columns.size(); i++) {
			sql.append("`").append(columns.get(i)).append("`");
			if (i != columns.size() - 1)
				sql.append(",");
		}
		sql.append(")").append(" VALUES(");
		for (int i = 0; i < props.size(); i++) {
			sql.append(props.get(i));
			if (i != props.size() - 1)
				sql.append(",");
		}
		sql.append(")");
		return sql.toString();
	}

	public String update(Object bean) {
		return getCachedSql(bean, "update", new Func<Object>() {
			@Override
			public String apply(Object t) {
				return getUpdateSql(bean, false);
			}
		});
	}

	public String updateSelective(Object bean) {
		return getUpdateSql(bean, true);
	}

	public String getUpdateSql(Object bean, boolean selective) {
		String tableName = table(bean);
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(tableName).append(" SET ");
		String id = "id";
		try {
			for (Field field : getCachedModelFields(bean.getClass())) {
				if (selective) {
					Object value = field.get(bean);
					if (value == null) {
						continue;
					}
				}
				if (field.isAnnotationPresent(Id.class)) {
					id = field.getName();
					continue;
				} else if (field.isAnnotationPresent(Transient.class)
						|| field.isAnnotationPresent(IgnoreUpdate.class)) {
					continue;
				}
				sql.append("`").append(StringUtil.snakeCase(field.getName())).append("`=#{").append(field.getName())
						.append("},");
			}
		} catch (Exception e) {
			new RuntimeException(sql.toString(), e);
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" where ").append(StringUtil.snakeCase(id)).append(" =#{").append(id).append("}");
		return sql.toString();
	}

	public String delete(Object bean) {
		return getCachedSql(bean, "delete", new Func<Object>() {
			@Override
			public String apply(Object t) {
				String tableName = table(bean);
				List<Field> fields = getCachedModelFields(bean.getClass());
				StringBuilder sql = new StringBuilder();
				sql.append(" DELETE FROM ").append(tableName).append(" WHERE ");
				try {
					for (int i = 0; i < fields.size(); i++) {
						Field field = fields.get(i);
						if (field.isAnnotationPresent(Id.class)) {
							sql.append(StringUtil.snakeCase(field.getName())).append("=#{").append(field.getName())
									.append("}");
							break;
						}
					}
				} catch (Exception e) {
					new RuntimeException(sql.toString(), e);
				}
				return sql.toString();
			}
		});
	}

	public String deleteMark(Object bean) {
		return getCachedSql(bean, "deleteMark", new Func<Object>() {
			@Override
			public String apply(Object t) {
				String tableName = table(bean);
				List<Field> fields = ClassUtil.getDeclaredFields(bean.getClass());
				StringBuilder sql = new StringBuilder();
				sql.append(" UPDATE ").append(tableName).append(" SET ");
				String delete = "", id = "";
				try {
					for (Field field : fields) {
						if (field.isAnnotationPresent(DeleteMark.class)) {
							delete = StringUtil.snakeCase(field.getName());
						} else if (field.isAnnotationPresent(Id.class)) {
							id = field.getName();
						}
					}
				} catch (Exception e) {
					new RuntimeException(sql.toString(), e);
				}
				sql.append(delete).append("=1 WHERE ").append(StringUtil.snakeCase(id)).append("=#{").append(id)
						.append("}");
				return sql.toString();
			}
		});
	}

	public String get(Object bean) {
		return getCachedSql(bean, "get", new Func<Object>() {
			@Override
			public String apply(Object t) {
				String tableName = table(bean);
				List<Field> fields = ClassUtil.getDeclaredFields(bean.getClass());
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT * FROM ").append(tableName).append(" WHERE ");
				try {
					for (int i = 0; i < fields.size(); i++) {
						Field field = fields.get(i);
						if (field.isAnnotationPresent(Id.class)) {
							sql.append(StringUtil.snakeCase(field.getName())).append("=#{").append(field.getName())
									.append("} and");
						}
					}
					sql.delete(sql.toString().length() - 3, sql.toString().length());
				} catch (Exception e) {
					new RuntimeException(sql.toString(), e);
				}
				return sql.toString();
			}
		});
	}

	public String getById(Class<?> bean, Serializable id) {
		return getCachedSql(bean, "get", new Func<Class<?>>() {
			@Override
			public String apply(Class<?> t) {
				String tableName = table(bean);
				List<Field> fields = ClassUtil.getDeclaredFields(bean);
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT * FROM ").append(tableName).append(" WHERE ");
				try {
					for (int i = 0; i < fields.size(); i++) {
						Field field = fields.get(i);
						if (field.isAnnotationPresent(Id.class)) {
							sql.append(StringUtil.snakeCase(field.getName())).append("=#{param1}");
							// sql.append(field.getName()).append("}and");
							break;
						}
					}
					// sql.delete(sql.toString().length() - 3, sql.toString().length());
				} catch (Exception e) {
					new RuntimeException(sql.toString(), e);
				}
				return sql.toString();
			}
		});
	}

	public String listAll(Class<?> bean) {
		return getCachedSql(bean, "listAll", new Func<Class<?>>() {
			@Override
			public String apply(Class<?> t) {
				String tableName = table(bean);
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT * FROM ").append(tableName);
				return sql.toString();
			}
		});
	}

	public String countAll(Class<?> bean) {
		return getCachedSql(bean, "countAll", new Func<Class<?>>() {
			@Override
			public String apply(Class<?> t) {
				String tableName = table(bean);
				StringBuilder getSql = new StringBuilder();
				getSql.append("SELECT count(0) FROM ").append(tableName);
				return getSql.toString();
			}
		});
	}

	public String listByIds(Class<?> entity, Iterable<Serializable> ids) {
		StringBuilder s = new StringBuilder();
		s.append("SELECT * FROM ").append(table(entity));
		s.append(" WHERE ").append(StringUtil.snakeCase(id(entity).getName())).append(" IN ( ");
		for (Serializable id : ids) {
			s.append("'").append(id).append("',");
		}
		s.deleteCharAt(s.length() - 1).append(" )");
		return s.toString();
	}

	public static Field id(Class<?> entity) {
		Field id = null;
		if (idFieldsMap.containsKey(entity)) {
			id = idFieldsMap.get(entity);
		} else {
			List<Field> fields = ClassUtil.getDeclaredFields(entity);
			for (Field f : fields) {
				if (f.isAnnotationPresent(Id.class)) {
					id = f;
					idFieldsMap.put(entity, f);
					break;
				}
			}
		}
		if (id == null)
			throw new RuntimeException(entity.getName() + "没有主键!");
		return id;
	}

	private static String getCachedSql(Object bean, String method, Func<Object> func) {
		String key = bean.getClass().getName() + "_" + method;
		if (sqls.containsKey(key)) {
			return sqls.get(key);
		} else {
			String apply = func.apply(bean);
			sqls.put(key, apply);
			return apply;
		}
	}

	private static String getCachedSql(Class<?> bean, String method, Func<Class<?>> func) {
		String key = bean.getName() + "_" + method;
		if (sqls.containsKey(key)) {
			return sqls.get(key);
		} else {
			String apply = func.apply(bean);
			sqls.put(key, apply);
			return apply;
		}
	}

	public static List<Field> getCachedModelFields(Class<?> beanClass) {
		if (modelFieldsMap.containsKey(beanClass.getName())) {
			return modelFieldsMap.get(beanClass.getName());
		} else {
			List<Field> fields = ClassUtil.getDeclaredFields(beanClass);
			fields.forEach(f->{f.setAccessible(true);});
			modelFieldsMap.put(beanClass.getName(), fields);
			return fields;
		}
	}

	private static String table(Object bean) {
		return table(bean.getClass());
	}

	private static String table(Class<?> bean) {
		if (bean.isAnnotationPresent(Table.class) && !bean.getAnnotation(Table.class).value().isEmpty()) {
			return bean.getAnnotation(Table.class).value();
		} else {
			return StringUtil.snakeCase(bean.getSimpleName());
		}
	}

	@FunctionalInterface
	public interface Func<T> {
		String apply(T t);
	}

	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Table {
		String value() default "";
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Id {
		boolean autoGenerated() default false;
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Transient {
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DeleteMark {
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IgnoreInsert {
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IgnoreUpdate {
	}
}
