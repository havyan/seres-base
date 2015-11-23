/**
 * 
 */
package com.framework.proxy;

import java.beans.Introspector;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;

import com.framework.proxy.interfaces.Bean;

/**
 * @author HWYan
 * 
 */
public class BeanMethodInterceptor extends DynamicMethodInterceptor {

	public BeanMethodInterceptor(Object source, Class<? extends DynamicInterface>[] interfaces) {
		super(source, interfaces);
	}

	protected Object invokeSourceMethod(Object dynamicObject, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (containsInterface(Bean.class)) {
			if (method.getName().startsWith("set") || method.getName().startsWith("get")) {
				Bean bean = getInterfaceFieldValue(dynamicObject, Bean.class);
				String propertyName = Introspector.decapitalize(method.getName().substring(3));
				if (method.getName().startsWith("set") && args.length == 1) {
					bean.setProperty(propertyName, args[0]);
					return null;
				} else {
					return bean.getProperty(propertyName);
				}
			}
		}
		return proxy.invoke(source, args);
	}

}
