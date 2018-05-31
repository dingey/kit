package com.di.kit;

public class Assert {
	public void assertNull(Object data) {
		if (data == null)
			throw new IllegalArgumentException();
	}
}
