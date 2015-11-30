/**
 * 
 */
package com.framework.proxy.impl;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.framework.common.BaseUtils;
import com.framework.events.ChangeEvent;
import com.framework.events.ChangeListener;
import com.framework.log.Logger;
import com.framework.proxy.DynamicObjectFactory2;
import com.framework.proxy.interfaces.AbstractBean;
import com.framework.proxy.interfaces.Bean;
import com.framework.proxy.interfaces.DynamicCollection;

/**
 * @author HWYan
 * 
 */
public class BeanImpl extends AbstractBean implements ChangeListener {

	private Object source;

	private List<String> changes = new ArrayList<String>();

	private Map<String, Bean> complexes = new HashMap<String, Bean>();

	private Map<String, DynamicCollection> lists = new HashMap<String, DynamicCollection>();

	public BeanImpl() {

	}

	public BeanImpl(Object source) {
		this.source = source;
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		Logger.debug("begin set propety name @" + propertyName);
		if (source != null) {
			Object current = BaseUtils.getProperty(source, propertyName);
			if ((current != null && value != null && !current.equals(value)) || current != value) {
				BaseUtils.setProperty(source, propertyName, value);
				changes.add(propertyName);
				complexes.remove(propertyName);
				lists.remove(propertyName);
				firePropertyChange(propertyName, current, value);
			}
		}
		Logger.debug("end set propety name @" + propertyName);
	}

	@Override
	public Object getProperty(String propertyName) {
		if (source != null) {
			if (complexes.containsKey(propertyName)) {
				return complexes.get(propertyName);
			} else if (lists.containsKey(propertyName)) {
				return lists.get(propertyName);
			} else {
				Object value = BaseUtils.getProperty(source, propertyName);
				if (value != null && !value.getClass().isPrimitive() && !Modifier.isFinal(value.getClass().getModifiers())) {
					if (List.class.isInstance(value)) {
						lists.put(propertyName, createDynamicList(propertyName, (List<?>) value));
					} else {
						complexes.put(propertyName, createBean(propertyName, value));
					}
				}
				return value;
			}
		}
		return null;
	}

	protected Bean createBean(String propertyName, Object value) {
		Bean bean = null;
		if (value instanceof Bean) {
			bean = (Bean) value;
		} else {
			bean = (Bean) DynamicObjectFactory2.createDynamicBeanObject(value);
		}
		bean.addPropertyChangeListener((e) -> {
			firePropertyChange(propertyName + "." + e.getPropertyName(), e.getOldValue(), e.getNewValue());
		});
		return bean;
	}

	protected DynamicCollection createDynamicList(String propertyName, List<?> list) {
		DynamicCollection dynamicCollection = null;
		if (list instanceof DynamicCollection) {
			dynamicCollection = (DynamicCollection) list;
		} else {
			dynamicCollection = (DynamicCollection) DynamicObjectFactory2.createDynamicListObject(list);
		}
		dynamicCollection.addChangeListener((e) -> {
			firePropertyChange(propertyName, null, e.getSource());
		});
		dynamicCollection.addPropertyChangeListener((e) -> {
			firePropertyChange(propertyName + "." + e.getPropertyName(), e.getOldValue(), e.getNewValue());
		});
		return dynamicCollection;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	@Override
	public void change(ChangeEvent e) {

	}

	@Override
	public boolean isChanged() {
		if (changes.size() > 0) {
			return true;
		} else {
			boolean changed = false;
			for (Map.Entry<String, DynamicCollection> entry : lists.entrySet()) {
				changed = changed || entry.getValue().isChanged();
			}

			for (Map.Entry<String, Bean> entry : complexes.entrySet()) {
				changed = changed || entry.getValue().isChanged();
			}
			return changed;
		}
	}

}
