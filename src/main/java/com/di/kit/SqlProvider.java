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
		sql.append("insert into ").append(tableName).append("(");
		try {
			for (Field field : getCachedModelFields(bean.getClass())) {
				if (selective) {
					Object value = field.get(bean);
					if (value == null) {
						continue;
					}
				}
				if (field.isAnnotationPresent(Transient.class)
						|| field.isAnnotationPresent(IgnoreInsert.class)) {
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
		sql.append("update ").append(tableName).append(" set ");
		List<String> ids=new ArrayList<>();
		String version=null ;
		try {
			for (Field field : getCachedModelFields(bean.getClass())) {
				if (selective) {
					Object value = field.get(bean);
					if (value == null) {
						continue;
					}
				}
				if (field.isAnnotationPresent(Id.class)) {
					ids.add(field.getName());
					continue;
				} else if(field.isAnnotationPresent(Version.class)) {
					version=field.getName();
					continue;
				} else if (field.isAnnotationPresent(Transient.class)
						|| field.isAnnotationPresent(IgnoreUpdate.class)) {
					continue;
				}
				sql.append("`").append(StringUtil.snakeCase(field.getName()))
						.append("`=#{").append(field.getName()).append("},");
			}
		} catch (Exception e) {
			new RuntimeException(sql.toString(), e);
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" where ");
		for(String id:ids) {
			sql.append(StringUtil.snakeCase(id)).append(" =#{")
				.append(id).append("} and ");
		}
		if(version!=null) {
			sql.append(" and ").append(StringUtil.snakeCase(version)).append("=#{").append(version).append("} and");
		}
		return sql.delete(sql.length()-4, sql.length()).toString();
	}

	public String delete(Object bean) {
		return getCachedSql(bean, "delete", new Func<Object>() {
			@Override
			public String apply(Object t) {
				String tableName = table(bean);
				List<Field> fields = getCachedModelFields(bean.getClass());
				StringBuilder sql = new StringBuilder();
				sql.append("delete from ").append(tableName).append(" where ");
				List<String> ids=new ArrayList<>();
				try {
					for (int i = 0; i < fields.size(); i++) {
						Field field = fields.get(i);
						if (field.isAnnotationPresent(Id.class)) {
							ids.add(field.getName());
						} else {
							continue;
						}
					}
				} catch (Exception e) {
					new RuntimeException(sql.toString(), e);
				}
				if(ids.isEmpty()) {
					throw new RuntimeException();
				} else {
					for(String id:ids) {
						sql.append(StringUtil.snakeCase(id))
						.append("=#{").append(id)
						.append("} and ");
					}
				}
				return sql.delete(sql.length()-5, sql.length()).toString();
			}
		});
	}

	public String deleteMark(Object bean) {
		return getCachedSql(bean, "deleteMark", new Func<Object>() {
			@Override
			public String apply(Object t) {
				String tableName = table(bean);
				List<Field> fields = ClassUtil
						.getDeclaredFields(bean.getClass());
				StringBuilder sql = new StringBuilder();
				sql.append("update ").append(tableName).append(" set ");
				String delete = null,version=null;
				List<String> ids=new ArrayList<>();
				try {
					for (Field field : fields) {
						if (field.isAnnotationPresent(DeleteMark.class)) {
							delete = StringUtil.snakeCase(field.getName());
						} else if (field.isAnnotationPresent(Id.class)) {
							ids.add(field.getName());
						} else if (field.isAnnotationPresent(Version.class)) {
							version=field.getName();
						}
					}
				} catch (Exception e) {
					new RuntimeException(sql.toString(), e);
				}
				sql.append(delete).append("=1 where ");
				if(ids.isEmpty()) {
					throw new RuntimeException("主键必须声明");
				} else {
					for(String id:ids) {
						sql.append(StringUtil.snakeCase(id)).append("=#{")
						.append(id).append("} and ");
					}
					if(version!=null) {
						sql.append(StringUtil.snakeCase(version)).append("=#{")
						.append(version).append("} and ");
					}
				}
				return sql.delete(sql.length()-5, sql.length()).toString();
			}
		});
	}

	public String get(Object bean) {
		return getCachedSql(bean, "get", new Func<Object>() {
			@Override
			public String apply(Object t) {
				String tableName = table(bean);
				List<Field> fields = ClassUtil
						.getDeclaredFields(bean.getClass());
				StringBuilder sql = new StringBuilder();
				sql.append("select * from ").append(tableName)
						.append(" where ");
				try {
					for (int i = 0; i < fields.size(); i++) {
						Field field = fields.get(i);
						if (field.isAnnotationPresent(Id.class)) {
							sql.append(StringUtil.snakeCase(field.getName()))
									.append("=#{").append(field.getName())
									.append("} and");
						}
					}
					sql.delete(sql.toString().length() - 3,
							sql.toString().length());
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
				sql.append("select * from ").append(tableName)
						.append(" where ");
				try {
					for (int i = 0; i < fields.size(); i++) {
						Field field = fields.get(i);
						if (field.isAnnotationPresent(Id.class)) {
							sql.append(StringUtil.snakeCase(field.getName()))
									.append("=#{param1}");
							// sql.append(field.getName()).append("}and");
							break;
						}
					}
					// sql.delete(sql.toString().length() - 3,
					// sql.toString().length());
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
				sql.append("select * from ").append(tableName);
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
				getSql.append("select count(0) from ").append(tableName);
				return getSql.toString();
			}
		});
	}

	public String listByIds(Class<?> entity, Iterable<Serializable> ids) {
		StringBuilder s = new StringBuilder();
		s.append("select * from ").append(table(entity));
		s.append(" where ").append(StringUtil.snakeCase(id(entity).getName()))
				.append(" in ( ");
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
					if (!f.isAccessible())
						f.setAccessible(true);
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

	private static String getCachedSql(Object bean, String method,
			Func<Object> func) {
		String key = bean.getClass().getName() + "_" + method;
		if (sqls.containsKey(key)) {
			return sqls.get(key);
		} else {
			String apply = func.apply(bean);
			sqls.put(key, apply);
			return apply;
		}
	}

	private static String getCachedSql(Class<?> bean, String method,
			Func<Class<?>> func) {
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
			fields.forEach(f -> {
				f.setAccessible(true);
			});
			modelFieldsMap.put(beanClass.getName(), fields);
			return fields;
		}
	}

	private static String table(Object bean) {
		return table(bean.getClass());
	}

	private static String table(Class<?> bean) {
		if (bean.isAnnotationPresent(Table.class)
				&& !bean.getAnnotation(Table.class).value().isEmpty()) {
			return bean.getAnnotation(Table.class).value();
		} else {
			return StringUtil.snakeCase(bean.getSimpleName());
		}
	}

	@FunctionalInterface
	public interface Func<T> {
		String apply(T t);
	}

	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Table {
		String value() default "";
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Id {
		boolean autoGenerated() default false;
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Transient {
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DeleteMark {
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IgnoreInsert {
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IgnoreUpdate {
	}
	
	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Version {
	}
}
