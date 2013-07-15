package org.jdgrid.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class Property {

	private Property() {
	}

	public static Class<?> getPropertyDataType(Class<?> clazz, String propertyName) {
		if (propertyName.contains(".")) {
			int first = propertyName.indexOf(".");
			String fieldNameRest = propertyName.substring(first + 1);
			String effectiveFieldName = propertyName.substring(0, first);

			return getPropertyDataType(getReturnTypeByPropertyName(clazz, effectiveFieldName), fieldNameRest);
		} else {
			return getReturnTypeByPropertyName(clazz, propertyName);
		}
	}

	private static Class<?> getReturnTypeByPropertyName(Class<?> clazz, String propertyName) {

		Field field = getFieldByFieldName(clazz, propertyName);
		if (field != null) {
			return field.getType();
		}
		char first = Character.toUpperCase(propertyName.charAt(0));
		String methodName = "get" + first + propertyName.substring(1);
		Method method = getMethodByMethodName(clazz, methodName);
		if (method != null) {
			return method.getReturnType();
		}
		throw new RuntimeException(String.format("No field and no getter method found for property '%1s' on class '%2s.", propertyName,
				clazz.getCanonicalName()));
	}

	public static Field getFieldByFieldName(Class<?> clazz, String propertyName) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals(propertyName)) {
				return field;
			}
		}
		if (clazz.getSuperclass() != null) {
			return getFieldByFieldName(clazz.getSuperclass(), propertyName);
		}

		return null;
	}

	public static void makeAccessible(Field field) {
		if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

	public static Method getMethodByMethodName(Class<?> clazz, String methodName) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		if (clazz.getSuperclass() != null) {
			return getMethodByMethodName(clazz.getSuperclass(), methodName);
		}
		return null;
	}

}
