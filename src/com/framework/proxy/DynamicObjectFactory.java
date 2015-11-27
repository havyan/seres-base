/**
 * 
 */
package com.framework.proxy;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.framework.common.BaseUtils;
import com.framework.log.Logger;
import com.framework.proxy.impl.DefaultBean;
import com.framework.proxy.interfaces.Bean;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * @author HWYan
 * 
 */
public class DynamicObjectFactory {

	private static final String CALLBACK_PREFIX = "CGLIB$CALLBACK_";

	@SuppressWarnings("unchecked")
	public static <T> T createDynamicObject(T target, MethodInterceptor methodInterceptor, Class<? extends DynamicObject>[] interfaces, DynamicObject[] impls) {
		if (target != null && !target.getClass().isPrimitive() && !Modifier.isFinal(target.getClass().getModifiers())) {
			T dynamicObject = null;
			if (interfaces.length == impls.length) {
				String classKey = createClassKey(target, interfaces);
				Class<?> dynamicClass = DynamicClassCache.classCache.get(classKey);
				if (dynamicClass == null) {
					Enhancer enhancer = new Enhancer();
					enhancer.setSuperclass(target.getClass());
					List<Class<? extends Callback>> callbacks = new ArrayList<Class<? extends Callback>>();
					callbacks.add(MethodInterceptor.class);
					callbacks.addAll(Arrays.asList(interfaces));
					enhancer.setCallbackTypes(callbacks.toArray(new Class[0]));
					enhancer.setInterfaces(interfaces);
					enhancer.setCallbackFilter(new DynamicCallbackFilter());
					dynamicClass = enhancer.createClass();
					DynamicClassCache.classCache.put(classKey, dynamicClass);
					for (int i = 0; i < callbacks.size(); i++) {
						Class<? extends Callback> callback = callbacks.get(i);
						DynamicClassCache.cacheFieldName(dynamicClass.getName(), callback, CALLBACK_PREFIX + i);
					}
				}

				if (dynamicClass != null) {
					dynamicObject = (T) BaseUtils.newInstance(dynamicClass);
					if (dynamicObject != null) {
						if (methodInterceptor == null) {
							methodInterceptor = new DynamicMethodInterceptor(target, interfaces);
						}
						BaseUtils.setField(dynamicObject, DynamicClassCache.getFieldName(dynamicClass.getName(), MethodInterceptor.class), methodInterceptor);
						for (int i = 0; i < interfaces.length; i++) {
							BaseUtils.setField(dynamicObject, DynamicClassCache.getFieldName(dynamicClass.getName(), interfaces[i]), impls[i]);
						}
					}
				}
			} else {
				Logger.warn("The length of interfaces doesn't match the length of implements");
			}
			return dynamicObject;
		} else {
			return target;
		}
	}

	public static <T> T createDynamicObject(T target, MethodInterceptor methodInterceptor, Class<? extends DynamicObject>[] interfaces,
			Class<? extends DynamicObject>[] implClasses) {
		DynamicObject[] impls = new DynamicObject[implClasses.length];
		for (int i = 0; i < impls.length; i++) {
			impls[i] = (DynamicObject) BaseUtils.newInstance(implClasses[i]);
			impls[i].setSource(target);
		}

		return createDynamicObject(target, methodInterceptor, interfaces, impls);
	}

	@SuppressWarnings("unchecked")
	public static <T> T createDynamicBeanObject(T target) {
		Class<? extends DynamicObject>[] interfaces = new Class[] { Bean.class };
		return createDynamicObject(target, new BeanMethodInterceptor(target, interfaces), interfaces, new DynamicObject[] { new DefaultBean(target) });
	}

	private static String createClassKey(Object target, Class<? extends Callback>[] interfaces) {
		StringBuilder sb = new StringBuilder();
		sb.append(target.getClass().getName());
		String[] classNames = new String[interfaces.length];
		for (int i = 0; i < classNames.length; i++) {
			classNames[i] = interfaces[i].getName();
		}
		Arrays.sort(classNames);
		for (String className : classNames) {
			sb.append(":").append(className);
		}

		return sb.toString();
	}

}
