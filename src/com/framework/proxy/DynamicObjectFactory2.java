package com.framework.proxy;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.framework.common.BaseUtils;
import com.framework.log.Logger;
import com.framework.proxy.impl.BeanImpl;
import com.framework.proxy.impl.DynamicCollectionImpl;
import com.framework.proxy.impl.DynamicMapImpl;
import com.framework.proxy.interfaces.Bean;
import com.framework.proxy.interfaces.DynamicCollection;
import com.framework.proxy.interfaces.DynamicMap;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;

public class DynamicObjectFactory2 {

	private static Map<ProxyInfo, Class<?>> classCache = new HashMap<ProxyInfo, Class<?>>();

	@SuppressWarnings("unchecked")
	public static <T> T createDynamicObject(T target, MethodInterceptor methodInterceptor, Class<? extends DynamicObject>[] interfaces, DynamicObject[] impls) {
		if (target != null && !target.getClass().isPrimitive() && !Modifier.isFinal(target.getClass().getModifiers())) {
			T dynamicObject = null;
			if (interfaces == null) {
				interfaces = (Class<? extends DynamicObject>[]) new Class<?>[0];
			}
			if (impls == null) {
				impls = new DynamicObject[0];
			}
			if ((interfaces.length == impls.length)) {
				ProxyInfo proxyInfo = new ProxyInfo(target.getClass(), interfaces);
				Class<?> dynamicClass = classCache.get(proxyInfo);
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
					classCache.put(proxyInfo, dynamicClass);
				}

				if (dynamicClass != null) {
					dynamicObject = (T) BaseUtils.newInstance(dynamicClass);
					List<Object> sortedImpls = new ArrayList<Object>();
					sortedImpls.add(methodInterceptor);
					for (Class<?> i : interfaces) {
						for (DynamicObject di : impls) {
							if (i.isInstance(di)) {
								sortedImpls.add(di);
								break;
							}
						}
					}

					((Factory) dynamicObject).setCallbacks(sortedImpls.toArray(new Callback[0]));
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
	public static <T> T createDynamicObject(T target) {
		if (target instanceof Bean || (target.getClass().isPrimitive() && Modifier.isFinal(target.getClass().getModifiers()))) {
			return target;
		} else {
			if (target instanceof List) {
				return (T) createDynamicListObject((List<?>) target);
			} else if(target instanceof Map){
				return (T) createDynamicMapObject((Map<?, ?>) target);
			}else{
				return createDynamicBeanObject(target);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T createDynamicBeanObject(T target) {
		Class<? extends DynamicObject>[] interfaces = new Class[] { Bean.class };
		return createDynamicObject(target, new BeanMethodInterceptor(target, interfaces), interfaces, new DynamicObject[] { new BeanImpl(target) });
	}

	@SuppressWarnings("unchecked")
	public static <T extends List<?>> T createDynamicListObject(T target) {
		Class<? extends DynamicObject>[] interfaces = new Class[] { DynamicCollection.class };
		return createDynamicObject(target, new ListMethodInterceptor(target, interfaces), interfaces, new DynamicObject[] { new DynamicCollectionImpl(target) });
	}

	@SuppressWarnings("unchecked")
	public static <T extends Map<?, ?>> T createDynamicMapObject(T target) {
		Class<? extends DynamicObject>[] interfaces = new Class[] { DynamicMap.class };
		return createDynamicObject(target, new MapMethodInterceptor(target, interfaces), interfaces, new DynamicObject[] { new DynamicMapImpl(target) });
	}

	static class ProxyInfo {

		private Class<?> superClass;

		private Class<?>[] interfaces;

		public ProxyInfo(Class<?> superClass, Class<?>[] interfaces) {
			super();
			this.superClass = superClass;
			setInterfaces(interfaces);
		}

		public Class<?> getSuperClass() {
			return superClass;
		}

		public void setSuperClass(Class<?> superClass) {
			this.superClass = superClass;
		}

		public Class<?>[] getInterfaces() {
			return interfaces;
		}

		public void setInterfaces(Class<?>[] interfaces) {
			Map<String, Class<?>> sortedInterfaces = new TreeMap<String, Class<?>>();
			for (Class<?> i : interfaces) {
				sortedInterfaces.put(i.getName(), i);
			}
			this.interfaces = sortedInterfaces.values().toArray(new Class<?>[] {});
		}

		public boolean equals(Object obj) {
			ProxyInfo info = (ProxyInfo) obj;
			boolean isEqual = superClass == info.superClass;
			if (isEqual) {
				isEqual = isEqual && interfaces.length == info.interfaces.length;
				if (isEqual) {
					for (int i = 0; i < interfaces.length; i++) {
						isEqual = isEqual && interfaces[i] == info.interfaces[i];
						if (!isEqual) {
							break;
						}
					}
				}
			}
			return isEqual;
		}

		public int hashCode() {
			int hashCode = superClass.hashCode();
			for (Class<?> i : interfaces) {
				hashCode += i.hashCode();
			}
			return hashCode;
		}

	}

}
