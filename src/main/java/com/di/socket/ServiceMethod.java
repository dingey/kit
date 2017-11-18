package com.di.socket;

import java.io.Serializable;

/**
 * @author d
 */
public class ServiceMethod implements Serializable {
	private static final long serialVersionUID = 633150853860193282L;
	private String className;
	private String methodName;
	private String[] paramTypes;
	private Object[] paramValues;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String[] getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(String[] paramTypes) {
		this.paramTypes = paramTypes;
	}

	public Object[] getParamValues() {
		return paramValues;
	}

	public void setParamValues(Object[] paramValues) {
		this.paramValues = paramValues;
	}

}
