/**
 * 
 */
package com.framework.common;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;

import com.framework.log.Logger;

/**
 * @author HWYan
 * 
 */
public class BaseUtils {

	private static String RE_LIST_PROPERTY = "((^|\\.)(\\d))(\\.|$)";

	private static final Map<String, Class<?>> primitiveWrapperMap = new HashMap<String, Class<?>>();

	private static final Class<?>[] primitiveTypes = new Class<?>[] { Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Double.TYPE, Float.TYPE,
			Void.TYPE };

	static {
		for (Class<?> type : primitiveTypes) {
			primitiveWrapperMap.put(type.toString(), type);
		}
	}

	public static Class<?> getPrimitiveClass(String name) {
		return primitiveWrapperMap.get(name);
	}

	public static boolean isClass(String name) {
		try {
			Class<?> cls = ClassUtils.getClass(name);
			return cls != null;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static Class<?> getClass(String name) {
		try {
			return ClassUtils.getClass(name);
		} catch (ClassNotFoundException e) {
			Logger.error(e);
		}
		return null;
	}

	public static Object deepClone(Object obj) {
		Object cloneObject = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.close();

			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);
			cloneObject = ois.readObject();
			ois.close();

		} catch (IOException e) {
			Logger.error(e);
		} catch (ClassNotFoundException e) {
			Logger.error(e);
		}
		return cloneObject;
	}

	public static void setField(Object target, String fieldName, Object value) {
		Field field = null;
		try {
			field = target.getClass().getDeclaredField(fieldName);
		} catch (Exception e) {
			Logger.error(e);
		}
		if (field != null) {
			field.setAccessible(true);
			try {
				field.set(target, value);
			} catch (Exception e) {
				Logger.error(e);
			}
		}
	}

	public static boolean hasField(Object target, String fieldName) {
		try {
			return target.getClass().getDeclaredField(fieldName) != null;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean hasMethod(Object target, String name, Class<?>... parameterTypes) {
		try {
			return target.getClass().getDeclaredMethod(name, parameterTypes) != null;
		} catch (Exception e) {
			Logger.info(e);
			return false;
		}
	}

	public static Object newInstance(String className) {
		Object obj = null;
		try {
			obj = newInstance(Class.forName(className.trim()));
		} catch (Exception e) {
			Logger.error(e);
			return null;
		}

		return obj;
	}

	public static Object getProperty(Object bean, String propertyName) {
		try {
			return PropertyUtils.getProperty(bean, convertPropertyName(propertyName));
		} catch (Exception e) {
			Logger.error(e);
			return null;
		}
	}

	public static void setProperty(Object bean, String propertyName, Object value) {
		try {
			PropertyUtils.setProperty(bean, convertPropertyName(propertyName), value);
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	public static String convertPropertyName(String propertyName) {
		propertyName = propertyName.trim();
		String result = propertyName;
		Pattern pattern = Pattern.compile(RE_LIST_PROPERTY);
		Matcher matcher = pattern.matcher(propertyName);
		while (matcher.find()) {
			String find = matcher.group();
			String indexString = "[" + matcher.group(3) + "]";
			if (find.startsWith(".") && find.endsWith(".")) {
				String replace = find.replace(matcher.group(1), indexString);
				result = result.replace(find, replace);
			} else if (find.endsWith(".")) {
				result = indexString + "." + result.substring(find.length());
			} else if (find.startsWith(".")) {
				result = result.substring(0, result.length() - find.length()) + indexString;
			} else {
				result = indexString;
			}
		}
		return result;
	}

	public static Object newInstance(Class<?> cl) {
		cl = getWraapedClass(cl);
		Object obj = null;
		try {
			obj = cl.newInstance();
		} catch (Exception e) {
			Logger.error(e);
			return null;
		}

		return obj;
	}

	public static Object newInstance(String className, Class<?>[] ccls, Object[] values) {
		Object obj = null;
		try {
			obj = newInstance(getWraapedClass(className), ccls, values);
		} catch (Exception e) {
			Logger.error(e);
			return null;
		}

		return obj;
	}

	public static Object newInstance(Class<?> cl, Class<?>[] ccls, Object[] values) {
		cl = getWraapedClass(cl);
		Object obj = null;
		try {
			Constructor<?> constructor = null;
			int paramsCount = values != null ? values.length : 0;
			if (Arrays.asList(ccls).stream().anyMatch((cls) -> cls == null)) {
				for (Constructor<?> c : cl.getConstructors()) {
					if (c.getParameterCount() == paramsCount) {
						constructor = c;
						break;
					}
				}
			} else {
				constructor = cl.getConstructor(ccls);
			}
			if (constructor != null) {
				obj = constructor.newInstance(values);
			}
		} catch (Exception e) {
			Logger.error(e);
			return null;
		}

		return obj;
	}

	public static Class<?> getWraapedClass(String className) {
		try {
			return getWraapedClass(Class.forName(className));
		} catch (ClassNotFoundException e) {
			Logger.error(e);
			return null;
		}
	}

	public static Class<?> getWraapedClass(Class<?> cls) {
		if (cls.isPrimitive()) {
			return ClassUtils.primitiveToWrapper(cls);
		} else {
			return cls;
		}
	}

	public static Method getReadMethod(Object source, String prop) {
		return getReadMethod(source.getClass(), prop);
	}

	public static Method getReadMethod(Class<?> cls, String prop) {
		try {
			PropertyDescriptor pd = new PropertyDescriptor(prop, cls);
			return pd.getReadMethod();
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
	}

	public static Method getWriteMethod(Object source, String prop) {
		return getWriteMethod(source.getClass(), prop);
	}

	public static Method getWriteMethod(Class<?> cls, String prop) {
		try {
			PropertyDescriptor pd = new PropertyDescriptor(prop, cls);
			return pd.getWriteMethod();
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
	}

	public static void setProperties(Object bean, Map<String, Object> properties) {
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			try {
				PropertyUtils.setProperty(bean, entry.getKey(), entry.getValue());
			} catch (Exception e) {
				Logger.error(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T createObject(Class<T> cls, String text) {
		if (cls == String.class) {
			return (T) text;
		}
		cls = (Class<T>) getWraapedClass(cls);
		try {
			Constructor<T> constructor = cls.getConstructor(String.class);
			if (constructor != null) {
				return constructor.newInstance(text);
			} else {
				Method method = cls.getDeclaredMethod("create", String.class);
				if (Modifier.isStatic(method.getModifiers())) {
					return (T) method.invoke(null, text);
				}
			}
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
	}

	public static Object getStaticValue(Class<?> cl, String field) {
		try {
			Field f = cl.getField(field);
			f.setAccessible(true);
			return f.get(cl);
		} catch (Exception e) {
			Logger.error(e);
			return null;
		}
	}

	/**
	 * 
	 * @param pack
	 * @return
	 */
	public static Set<Class<?>> getClasses(String pack) {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		boolean recursive = true;
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {
					JarFile jar;
					try {
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							if (name.charAt(0) == '/') {
								name = name.substring(1);
							}
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								if (idx != -1) {
									packageName = name.substring(0, idx).replace('/', '.');
								}
								if ((idx != -1) || recursive) {
									if (name.endsWith(".class") && !entry.isDirectory()) {
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										try {
											classes.add(Class.forName(packageName + '.' + className));
										} catch (ClassNotFoundException e) {
											Logger.error(e);
										}
									}
								}
							}
						}
					} catch (IOException e) {
						Logger.error(e);
					}
				}
			}
		} catch (IOException e) {
			Logger.error(e);
		}

		return classes;
	}

	/**
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
			} else {
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					Logger.error(e);
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Class<T> getClassGenricType(final Class clazz) {
		return getClassGenricType(clazz, 0);
	}

	@SuppressWarnings("rawtypes")
	public static Class getClassGenricType(final Class clazz, final int index) {
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}
		return (Class) params[index];
	}
}
