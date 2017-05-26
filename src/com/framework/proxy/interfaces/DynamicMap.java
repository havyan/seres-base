package com.framework.proxy.interfaces;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;

public interface DynamicMap extends Bean {
	
	public Object invoke(Method method, Object[] args, MethodProxy methodProxy) throws Throwable;

}
