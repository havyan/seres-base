/**
 * 
 */
package com.framework.proxy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HWYan
 * 
 */
class DynamicClassCache {

	static Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();

	static Map<String, Map<String, String>> fieldNameCache = new HashMap<String, Map<String, String>>();

	static void cacheFieldName(String classKey, Class<?> filedClass, String fieldName) {
		Map<String, String> fieldMap = fieldNameCache.get(classKey);
		if (fieldMap == null) {
			fieldMap = new HashMap<String, String>();
			fieldNameCache.put(classKey, fieldMap);
		}
		fieldMap.put(filedClass.getName(), fieldName);
	}

	static String getFieldName(String cls, Class<?> filedClass) {
		Map<String, String> fieldMap = fieldNameCache.get(cls);
		if (fieldMap != null) {
			return fieldMap.get(filedClass.getName());
		}

		return null;
	}
	
	static String getFieldName(Class<?> cls, Class<?> filedClass) {
		return getFieldName(cls.getName(), filedClass);
	}

}
