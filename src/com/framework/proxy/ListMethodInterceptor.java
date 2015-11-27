/**
 * 
 */
package com.framework.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

import com.framework.common.BaseUtils;
import com.framework.log.Logger;
import com.framework.proxy.interfaces.Bean;
import com.framework.proxy.interfaces.DynamicCollection;

import net.sf.cglib.proxy.MethodProxy;

/**
 * @author HWYan
 *
 */
public class ListMethodInterceptor extends DynamicMethodInterceptor {

	public ListMethodInterceptor(Collection<?> source, Class<? extends DynamicObject>[] interfaces) {
		super(source, interfaces);
		convertList();
	}

	protected Object invokeSourceMethod(Object dynamicObject, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		List<?> list = (List<?>) source;
		Object[] origin = list.toArray();
		Object result = proxy.invoke(source, convertArgs(args));
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
	protected void convertList() {
		List list = (List) source;
		Object[] array = list.toArray();
		list.clear();
		for (Object e : array) {
			list.add(convert2DynamicObject(e));
		}
	}

	protected Object[] convertArgs(Object[] args) {
		Object[] newArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			newArgs[i] = convertArg(args[i]);
		}
		return newArgs;
	}

	protected Object convertArg(Object arg) {
		if (Collection.class.isInstance(arg)) {
			return convertCollectionArg((Collection<?>) arg);
		} else {
			return convertSimpleArg(arg);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object convertCollectionArg(Collection<?> arg) {
		Collection newArg = (Collection) BaseUtils.newInstance(arg.getClass());
		for (Object e : arg) {
			newArg.add(convertSimpleArg(e));
		}
		return newArg;
	}

	protected Object convertSimpleArg(Object arg) {
		if (arg instanceof DynamicObject) {
			return arg;
		} else {
			List<?> list = (List<?>) source;
			for (Object e : list) {
				DynamicObject dynamicObject = (DynamicObject) e;
				if (dynamicObject.getSource() == arg) {
					return dynamicObject;
				}
			}
		}
		return arg;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void check() throws Exception {
		List list = (List) source;
		for (int i = 0; i < list.size(); i++) {
			Object e = list.get(i);
			if (!(e instanceof Bean)) {
				list.set(i, convert2DynamicObject(e));
			}
		}
	}

	protected Object convert2DynamicObject(Object target) {
		if (target instanceof Bean || target instanceof DynamicCollection || (target.getClass().isPrimitive() && Modifier.isFinal(target.getClass().getModifiers()))) {
			return target;
		} else {
			if (List.class.isInstance(target)) {
				return DynamicObjectFactory2.createDynamicListObject((List<?>) target);
			} else {
				return DynamicObjectFactory2.createDynamicBeanObject(target);
			}
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
