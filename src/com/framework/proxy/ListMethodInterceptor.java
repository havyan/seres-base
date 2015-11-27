/**
 * 
 */
package com.framework.proxy;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import com.framework.log.Logger;
import com.framework.proxy.interfaces.Bean;
import com.framework.proxy.interfaces.DynamicCollection;

import net.sf.cglib.proxy.MethodProxy;

/**
 * @author HWYan
 *
 */
public class ListMethodInterceptor extends DynamicMethodInterceptor {

	public ListMethodInterceptor(Collection<?> source, Class<? extends DynamicInterface>[] interfaces) {
		super(source, interfaces);
		convert();
	}

	protected Object invokeSourceMethod(Object dynamicObject, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		List<?> list = (List<?>) source;
		Object[] origin = list.toArray();
		Object result = proxy.invoke(source, args);
		if (isDifferent(origin, list)) {
			Logger.debug("List changed");
			check();
			if (hasInterface(DynamicCollection.class)) {
				DynamicCollection dynamicCollection = getInterfaceFieldValue(dynamicObject, DynamicCollection.class);
				Logger.debug("Fire list changed");
				dynamicCollection.fireChange();
			}
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void convert() {
		List list = (List) source;
		Object[] array = list.toArray();
		list.clear();
		for (Object e : array) {
			try {
				list.add(convert2Bean(e));
			} catch (Exception ex) {
				Logger.error(ex);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void check() throws Exception {
		List list = (List) source;
		for (int i = 0; i < list.size(); i++) {
			Object e = list.get(i);
			if (!(e instanceof Bean)) {
				list.set(i, convert2Bean(e));
			}
		}
	}

	protected Object convert2Bean(Object target) throws Exception {
		if (target instanceof Bean) {
			return target;
		} else {
			return DynamicObjectFactory2.createDynamicBeanObject(target);
		}
	}

	protected boolean isDifferent(Object[] array, List<?> list) {
		if (array.length != list.size()) {
			return true;
		} else {
			for (int i = 0; i < array.length; i++) {
				if (array[i] != list.get(i)) {
					return true;
				}
			}
		}
		return false;
	}
}
