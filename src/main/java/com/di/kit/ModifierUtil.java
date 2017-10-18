package com.di.kit;

import java.lang.reflect.Modifier;

/**
 * @author di
 */
public class ModifierUtil {
	public static boolean isCommon(int modifiers) {
		return !Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isNative(modifiers)
				&& !Modifier.isTransient(modifiers);
	}
}
