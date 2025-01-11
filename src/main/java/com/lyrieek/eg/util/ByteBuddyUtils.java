package com.lyrieek.eg.util;

import net.bytebuddy.implementation.MethodCall;

public class ByteBuddyUtils {

	public static MethodCall getNoopSuper() {
		try {
			return MethodCall.invoke(Object.class.getConstructor()).onSuper();
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}
