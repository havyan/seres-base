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
import com.rits.cloning.Cloner;

/**
 * @author HWYan
 * 
 */
public class BeanImpl extends AbstractBean<Object> implements ChangeListener {

	private List<String> changes = new ArrayList<String>();

	private Map<String, Bean> complexes = new HashMap<String, Bean>();

	public BeanImpl() {
		this(null);
	}

	public BeanImpl(Object source) {
		super(source);
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		Logger.debug("begin set propety name @" + propertyName);
		if (source != null) {
			Object current = BaseUtils.getProperty(source, propertyName);
			if ((current != null && value != null && !current.equals(value)) || current != value) {
				Bean bean = null;
				if (value instanceof Bean) {
					bean = (Bean) value;
					value = bean.source();
				}
				BaseUtils.setProperty(source, propertyName, value);
				changes.add(propertyName);
				removeBean(propertyName);
				if (bean != null) {
					addBean(propertyName, bean);
				}
				value = BaseUtils.getProperty(source, propertyName);
				firePropertyChange(null, propertyName, current, value);
			}
		}
		Logger.debug("end set propety name @" + propertyName);
	}

	@Override
	public Object getProperty(String propertyName) {
		if (source != null) {
			Object value = BaseUtils.getProperty(source, propertyName);
			Bean complex = complexes.get(propertyName);
			if (complex != null && complex.source() == value) {
				return complex;
			} else {
				if (value != null && !value.getClass().isPrimitive() && !Modifier.isFinal(value.getClass().getModifiers())) {
					return addBean(propertyName, value);
				}
				return value;
			}
		}
		return null;
	}

	protected void removeBean(String propertyName) {
		Bean bean = complexes.remove(propertyName);
		if (bean != null) {
			if (bean instanceof DynamicCollection) {
				DynamicCollection dynamicCollection = (DynamicCollection) bean;
				dynamicCollection.removeChangeListenerByFrom(this);
			}
			bean.removeAllPropertyChangeListenerFrom(this);
		}
	}

	protected Bean addBean(String propertyName, Object value) {
		Bean bean = null;
		if (value instanceof Bean) {
			bean = (Bean) value;
		} else {
			bean = (Bean) DynamicObjectFactory2.createDynamicObject(value);
		}
		this.bindBean(propertyName, bean);
		complexes.put(propertyName, bean);
		return bean;
	}

	@Override
	public void change(ChangeEvent e) {

	}

	@Override
	public boolean changed() {
		if (changes.size() > 0) {
			return true;
		} else {
			boolean changed = false;
			for (Map.Entry<String, Bean> entry : complexes.entrySet()) {
				changed = changed || entry.getValue().changed();
			}
			return changed;
		}
	}

	@Override
	public Object cloneSource() {
		Cloner cloner = new Cloner();
		Object target = cloner.shallowClone(this.source);
		for (Map.Entry<String, Bean> entry : this.complexes.entrySet()) {
			BaseUtils.setProperty(target, entry.getKey(), null);
		}
		target = cloner.deepClone(target);
		for (Map.Entry<String, Bean> entry : this.complexes.entrySet()) {
			BaseUtils.setProperty(target, entry.getKey(), entry.getValue().cloneSource());
		}
		return target;
	}

}
