package com.framework.proxy;

import java.lang.reflect.Method;

import com.framework.proxy.interfaces.DynamicMap;

import net.sf.cglib.proxy.MethodProxy;

public class MapMethodInterceptor extends DynamicMethodInterceptor {

	public MapMethodInterceptor(Object source, Class<? extends DynamicObject>[] interfaces) {
		super(source, interfaces);
	}

	protected Object invokeSourceMethod(Object dynamicObject, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (hasInterface(DynamicMap.class)) {
			DynamicMap dynamicMap = getInterfaceFieldValue(dynamicObject, DynamicMap.class);
			return dynamicMap.invoke(method, args, proxy);
		}
		return proxy.invoke(source, args);
	}

}
