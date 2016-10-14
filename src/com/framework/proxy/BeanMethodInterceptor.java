/**
 * 
 */
package com.framework.proxy;

import java.beans.Introspector;
import java.lang.reflect.Method;

import com.framework.proxy.interfaces.Bean;

import net.sf.cglib.proxy.MethodProxy;

/**
 * @author HWYan
 * 
 */
public class BeanMethodInterceptor extends DynamicMethodInterceptor {

	public BeanMethodInterceptor(Object source, Class<? extends DynamicObject>[] interfaces) {
		super(source, interfaces);
	}

	protected Object invokeSourceMethod(Object dynamicObject, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (hasInterface(Bean.class)) {
			Bean bean = getInterfaceFieldValue(dynamicObject, Bean.class);
			String methodName = method.getName();
			if (methodName.startsWith("get")) {
				String propertyName = Introspector.decapitalize(methodName.substring(3));
				if (args == null || args.length == 0) {
					return bean.getProperty(propertyName);
				}
			} else if (methodName.startsWith("is")) {
				String propertyName = Introspector.decapitalize(methodName.substring(2));
				if (args == null || args.length == 0) {
					return bean.getProperty(propertyName);
				}
			} else if (methodName.startsWith("set")) {
				String propertyName = Introspector.decapitalize(methodName.substring(3));
				if (args.length == 1) {
					bean.setProperty(propertyName, args[0]);
					return null;
				}
			}
		}
		return proxy.invoke(source, args);
	}

}
