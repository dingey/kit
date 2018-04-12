package com.di.kit;

import java.sql.JDBCType;

public enum JDBCTypeEnum {
	STRING(String.class, JDBCType.CHAR, JDBCType.VARCHAR, JDBCType.LONGVARCHAR, JDBCType.NUMERIC),
	BIGDECIMAL(java.math.BigDecimal.class,JDBCType.NUMERIC,JDBCType.DECIMAL),
	BOOLEAN(new Class[] {boolean.class,Boolean.class},JDBCType.BIT,JDBCType.BOOLEAN),
	BYTE(new Class[] {byte.class,Byte.class},JDBCType.TINYINT), 
	SHORT(new Class[] {short.class,Short.class}, JDBCType.SMALLINT),
	INT(new Class[] {int.class,Integer.class},JDBCType.INTEGER),
	LONG(new Class[] {long.class,Long.class},JDBCType.BIGINT),
	FLOAT(new Class[] {float.class,Float.class},JDBCType.REAL),
	DOUBLE(new Class[] {double.class,Double.class},JDBCType.FLOAT,JDBCType.DOUBLE),
	DATE(java.sql.Date.class,JDBCType.DATE),
	TIME(java.sql.Time.class,JDBCType.TIME),
	TIMESTAMP(java.sql.Timestamp.class,JDBCType.TIMESTAMP),
	CLOB(java.sql.Clob.class,JDBCType.CLOB),
	BLOB(java.sql.Blob.class,JDBCType.DATALINK),
	ARRAY(java.sql.Array.class,JDBCType.ARRAY),
	REF(java.sql.Ref.class,JDBCType.REF),
	URL(java.net.URL.class,JDBCType.DATALINK),
	BYTE_ARRAY(byte[].class,JDBCType.BINARY,JDBCType.VARBINARY,JDBCType.LONGVARBINARY);

	private JDBCType[] jdbcTypes;
	private Class<?>[] javaTypes;

	private JDBCTypeEnum(Class<?>[] javaTypes, JDBCType... jdbcTypes) {
		this.jdbcTypes = jdbcTypes;
		this.javaTypes = javaTypes;
	}
	
	private JDBCTypeEnum(Class<?> javaType, JDBCType... jdbcTypes) {
		this.jdbcTypes = jdbcTypes;
		this.javaTypes = new Class[] {javaType};
	}

	public JDBCType[] getJdbcTypes() {
		return jdbcTypes;
	}

	public Class<?>[] getJavaTypes() {
		return javaTypes;
	}
	
	public Class<?> getJavaType() {
		return getJavaType(null);
	}
	
	public Class<?> getJavaType(Boolean isPrimitive) {
		if(isPrimitive!=null && isPrimitive) {
			for(Class<?> c:javaTypes) {
				if(c.isPrimitive())
					return c;
			}
		} else if(isPrimitive!=null && !isPrimitive) {
			for(Class<?> c:javaTypes) {
				if(!c.isPrimitive())
					return c;
			}
		}
		return javaTypes[0];
	}
}
