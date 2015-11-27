/**
 * 
 */
package com.framework.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.framework.log.Logger;

/**
 * @author HWYan
 * 
 */
public class DynamicMethodInterceptor implements MethodInterceptor {

	protected Object source; // TODO may be no need

	protected Class<? extends DynamicObject>[] interfaces;

	public DynamicMethodInterceptor(Object source, Class<? extends DynamicObject>[] interfaces) {
		super();
		this.source = source;
		this.interfaces = interfaces;
	}

	@Override
	public Object intercept(Object dynamicObject, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		try {
			if (source.getClass().getMethod(method.getName(), method.getParameterTypes()) != null) {
				return invokeSourceMethod(dynamicObject, method, args, proxy);
			}
		} catch (SecurityException e) {
			Logger.error(e);
		} catch (NoSuchMethodException e) {
			Logger.info(e);
		}
		Object interfaceFieldValue = getInterfaceFieldValue(dynamicObject, method);
		if (interfaceFieldValue != null) {
			return proxy.invoke(interfaceFieldValue, args);
		}
		return null;
	}

	protected Object getInterfaceFieldValue(Object dynamicObject, Method method) {
		Class<? extends DynamicObject> interfaceClass = null;
		for (Class<? extends DynamicObject> cls : interfaces) {
			try {
				if (cls.getMethod(method.getName(), method.getParameterTypes()) != null) {
					interfaceClass = cls;
					break;
				}
			} catch (SecurityException e) {
				Logger.error(e);
				return null;
			} catch (NoSuchMethodException e) {
				Logger.info(e);
			}
		}

		return getInterfaceFieldValue(dynamicObject, interfaceClass);

	}

	@SuppressWarnings("unchecked")
	protected <T> T getInterfaceFieldValue(Object dynamicObject, Class<T> interfaceClass) {
		if (interfaceClass != null) {
			Field[] fields = dynamicObject.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (!Modifier.isStatic(field.getModifiers())) {
					Object value = null;
					field.setAccessible(true);
					try {
						value = field.get(dynamicObject);
					} catch (IllegalArgumentException e) {
						Logger.error(e);
					} catch (IllegalAccessException e) {
						Logger.error(e);
					}
					if (value != null && interfaceClass.isInstance(value)) {
						return (T) value;
					}
				}
			}
		}

		return null;
	}

	protected Object invokeSourceMethod(Object dynamicObject, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		return proxy.invoke(source, args);
	}

	protected boolean hasInterface(Class<?> cls) {
		for (Class<? extends DynamicObject> i : interfaces) {
			if (i == cls) {
				return true;
			}
		}

		return false;
	}

}
