/**
 * 
 */
package com.framework.common;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;

import com.framework.events.AdvancedPropertyChangeEvent;
import com.framework.events.PropertyChangeListenerProxy;
import com.framework.log.Logger;
import com.framework.proxy.interfaces.Bean;
import com.rits.cloning.Cloner;

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
			Class<?> cls = Class.forName(name);
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

	public static void bind(Object target, PropertyChangeListener l) {
		if (target != null && target instanceof Bean) {
			((Bean) target).addPropertyChangeListener(l);
		}
	}

	public static void bind(Object target, String property, PropertyChangeListener l) {
		if (target != null && target instanceof Bean) {
			((Bean) target).addPropertyChangeListener(property, l);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T deepClone(T obj) {
		T target = null;
		if (obj instanceof Bean) {
			target = (T) ((Bean) obj).cloneSource();
		} else {
			Cloner cloner = new Cloner();
			target = cloner.deepClone(obj);
		}
		return target;
	}

	public static void setFieldValue(Object target, String fieldName, Object value) {
		Class<?> cls = target.getClass();
		Field field = null;
		while (cls != Object.class && field == null) {
			try {
				field = cls.getDeclaredField(fieldName);
			} catch (Exception e) {
				Logger.debug(fieldName + " is not found in " + cls.getName());
			}
			cls = cls.getSuperclass();
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

	@SuppressWarnings("unchecked")
	public static <T> T getSource(T target) {
		if (target != null && target instanceof Bean) {
			return (T) ((Bean) target).getSource();
		}
		return target;
	}

	public static boolean hasField(Object target, String fieldName) {
		Class<?> cls = target.getClass();
		Field field = null;
		while (cls != Object.class && field == null) {
			try {
				field = cls.getDeclaredField(fieldName);
			} catch (Exception e) {
				Logger.debug(fieldName + " is not found in " + cls.getName());
			}
			cls = cls.getSuperclass();
		}
		return field != null;
	}

	public static Object getFieldValue(Object target, String fieldName) {
		Class<?> cls = target.getClass();
		Field field = null;
		while (cls != Object.class && field == null) {
			try {
				field = cls.getDeclaredField(fieldName);
			} catch (Exception e) {
				Logger.debug(fieldName + " is not found in " + cls.getName());
			}
			cls = cls.getSuperclass();
		}
		if (field != null) {
			field.setAccessible(true);
			try {
				return field.get(target);
			} catch (Exception e) {
				Logger.error(e);
			}
		}
		return null;
	}

	public static boolean hasMethod(Object target, String name, Class<?>... parameterTypes) {
		Class<?> cls = target.getClass();
		Method method = null;
		while (cls != Object.class && method == null) {
			try {
				method = cls.getDeclaredMethod(name, parameterTypes);
			} catch (Exception e) {
				Logger.debug(name + " is not found in " + cls.getName());
			}
			cls = cls.getSuperclass();
		}
		return method != null;
	}

	public static void takeBinds(Object source, Object dest, Object from) {
		if (source != null && source instanceof Bean) {
			Bean sourceBean = (Bean) source;
			if (dest != null && dest instanceof Bean) {
				Bean destBean = (Bean) dest;
				PropertyChangeEvent event = new PropertyChangeEvent(dest, "*", 0, 1);
				PropertyChangeListenerProxy[] listeners = sourceBean.getPropertyChangeListenersFrom(from);
				for (PropertyChangeListenerProxy listener : listeners) {
					destBean.addPropertyChangeListener(listener);
					listener.propertyChange(event);
				}
				Map<String, PropertyChangeListenerProxy[]> map = sourceBean.getPropertyChangeListenersMapFrom(from);
				for (Map.Entry<String, PropertyChangeListenerProxy[]> entry : map.entrySet()) {
					String propertyName = entry.getKey();
					listeners = entry.getValue();
					for (PropertyChangeListenerProxy listener : listeners) {
						destBean.addPropertyChangeListener(propertyName, listener);
						listener.propertyChange(event);
					}
				}
			}
			sourceBean.removeAllPropertyChangeListenerFrom(from);
		}
	}

	public static List<Object> getChain(PropertyChangeEvent e) {
		List<Object> chain = new ArrayList<Object>();
		if (e instanceof AdvancedPropertyChangeEvent) {
			chain = ((AdvancedPropertyChangeEvent) e).getChain();
		}
		return chain;
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
			Field f = cl.getDeclaredField(field);
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
