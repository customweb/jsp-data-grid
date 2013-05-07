package org.jdgrid.util;

public class Property {
	
	public static Class<?> getPropertyDataType(Class<?> clazz, String propertyName) throws SecurityException, NoSuchFieldException {
		if (propertyName.contains(".")) {
			int first = propertyName.indexOf(".");
			String fieldNameRest = propertyName.substring(first+1);
			String effectiveFieldName = propertyName.substring(0, first);
			return  getPropertyDataType(clazz.getDeclaredField(effectiveFieldName).getType(), fieldNameRest);
		}
		else {
			return clazz.getDeclaredField(propertyName).getType();
		}
	}

}
