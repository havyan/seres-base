/**
 * 
 */
package com.framework.proxy;

import java.lang.reflect.Method;
import java.util.Collection;

import com.framework.proxy.interfaces.DynamicCollection;

import net.sf.cglib.proxy.MethodProxy;

/**
 * @author HWYan
 *
 */
public class ListMethodInterceptor extends DynamicMethodInterceptor {

	public ListMethodInterceptor(Collection<?> source, Class<? extends DynamicObject>[] interfaces) {
		super(source, interfaces);
	}

	protected Object invokeSourceMethod(Object dynamicObject, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (hasInterface(DynamicCollection.class)) {
			DynamicCollection dynamicCollection = getInterfaceFieldValue(dynamicObject, DynamicCollection.class);
			return dynamicCollection.invoke(method, args, proxy);
		}
		return proxy.invoke(source, args);
	}
}
