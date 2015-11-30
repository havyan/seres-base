package com.framework.proxy;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.framework.events.ChangeAdapter;
import com.framework.events.ChangeEvent;
import com.framework.events.PropertyChangeAdapter;
import com.framework.proxy.interfaces.Bean;
import com.framework.proxy.interfaces.DynamicCollection;
import com.framework.proxy.interfaces.DynamicMap;

import net.sf.cglib.proxy.MethodProxy;

public class MapMethodInterceptor extends DynamicMethodInterceptor {

	private DynamicMap dynamicMap;

	public MapMethodInterceptor(Object source, Class<? extends DynamicObject>[] interfaces) {
		super(source, interfaces);
		convertMap();
	}

	protected Object invokeSourceMethod(Object dynamicObject, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (dynamicMap == null && dynamicObject != null) {
			this.dynamicMap = (DynamicMap) dynamicObject;
		}
		Map<Object, Object> origin = getOrigin();
		Object result = proxy.invoke(source, args);
		check();
		if (hasInterface(DynamicMap.class)) {
			for (Map.Entry<Object, Object> entry : origin.entrySet()) {
				if (entry.getKey() instanceof String) {
					Object oldValue = entry.getValue();
					Object newValue = ((Map<?, ?>) source).get(entry.getKey());
					if (newValue != oldValue) {
						firePropertyChange((String) entry.getKey(), oldValue, newValue);
					}
				}
			}
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) source).entrySet()) {
				if (entry.getKey() instanceof String) {
					if (!origin.containsKey(entry.getKey())) {
						firePropertyChange((String) entry.getKey(), null, entry.getValue());
					}
				}
			}
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void convertMap() {
		Map map = (Map) source;
		Set<Map.Entry> entrySet = map.entrySet();
		for (Map.Entry entry : entrySet) {
			if (entry.getKey() instanceof String && entry.getValue() != null) {
				Object result = convert2DynamicObject((String) entry.getKey(), entry.getValue());
				if (result instanceof Bean) {
					bindBean((String) entry.getKey(), (Bean) result);
				}
				map.put(entry.getKey(), result);
			} else {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		map.clear();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void check() throws Exception {
		Map map = (Map) source;
		Set<Map.Entry> entrySet = map.entrySet();
		for (Map.Entry entry : entrySet) {
			if (entry.getKey() instanceof String && entry.getValue() != null) {
				Bean bean = null;
				if (entry.getValue() instanceof Bean) {
					bean = (Bean) entry.getValue();
				} else {
					Object result = convert2DynamicObject((String) entry.getKey(), entry.getValue());
					if (result instanceof Bean) {
						bean = (Bean) result;
					}
					map.put(entry.getKey(), result);
				}
				bindBean((String) entry.getKey(), bean);
			}
		}
	}

	protected void bindBean(String propertyName, Bean bean) {
		if (bean != null) {
			if (!bean.hasPropertyChangeListenerFrom(this)) {
				bean.addPropertyChangeListener(new PropertyChangeAdapter(this) {
					public void propertyChange(PropertyChangeEvent e) {
						firePropertyChange(propertyName + "." + e.getPropertyName(), e.getOldValue(), e.getNewValue());
					}
				});
			}
			if (bean instanceof DynamicCollection) {
				DynamicCollection dynamicCollection = (DynamicCollection) bean;
				if (!dynamicCollection.hasChangeListenerFrom(this)) {
					dynamicCollection.addChangeListener(new ChangeAdapter(this) {
						public void change(ChangeEvent e) {
							firePropertyChange(propertyName, null, e.getSource());
						}
					});
				}
			}
		}
	}

	protected Object convert2DynamicObject(String propertyName, Object target) {
		Bean bean = null;
		if (target instanceof Bean) {
			bean = (Bean) target;
		} else {
			Object result = DynamicObjectFactory2.createDynamicObject(target);
			if (result instanceof Bean) {
				bean = (Bean) result;
			}
		}
		return bean;
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (dynamicMap != null) {
			if (hasInterface(DynamicMap.class)) {
				DynamicMap dynamicMapCallback = getInterfaceFieldValue(dynamicMap, DynamicMap.class);
				dynamicMapCallback.firePropertyChange(propertyName, oldValue, newValue);
			}
		}
	}

	private Map<Object, Object> getOrigin() {
		Map<Object, Object> origin = new HashMap<Object, Object>();
		Map<?, ?> source = (Map<?, ?>) this.source;
		for (Map.Entry<?, ?> entry : source.entrySet()) {
			origin.put(entry.getKey(), entry.getValue());
		}
		return origin;
	}

}
