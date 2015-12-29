/**
 * 
 */
package com.framework.proxy.impl;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.framework.common.BaseUtils;
import com.framework.events.ChangeAdapter;
import com.framework.events.ChangeEvent;
import com.framework.events.ChangeListener;
import com.framework.events.PropertyChangeListenerProxy;
import com.framework.log.Logger;
import com.framework.proxy.DynamicObjectFactory2;
import com.framework.proxy.interfaces.AbstractBean;
import com.framework.proxy.interfaces.Bean;
import com.framework.proxy.interfaces.DynamicCollection;

/**
 * @author HWYan
 * 
 */
public class BeanImpl extends AbstractBean<Object> implements ChangeListener {

	private List<String> changes = new ArrayList<String>();

	private Map<String, Bean> complexes = new HashMap<String, Bean>();

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
				firePropertyChange(propertyName, current, value);
			}
		}
		Logger.debug("end set propety name @" + propertyName);
	}

	@Override
	public Object getProperty(String propertyName) {
		if (source != null) {
			Object value = BaseUtils.getProperty(source, propertyName);
			Bean complex = complexes.get(propertyName);
			if (complex != null && complex.getSource() == value) {
				return complex;
			} else {
				if (value != null && !value.getClass().isPrimitive() && !Modifier.isFinal(value.getClass().getModifiers())) {
					Bean bean = createBean(propertyName, value);
					complexes.put(propertyName, bean);
					return bean;
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
			bean = (Bean) DynamicObjectFactory2.createDynamicObject(value);
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
		if (!bean.hasPropertyChangeListenerFrom(this)) {
			bean.addPropertyChangeListener(new PropertyChangeListenerProxy(this) {
				public void propertyChange(PropertyChangeEvent e) {
					firePropertyChange(propertyName + "." + e.getPropertyName(), e.getOldValue(), e.getNewValue());
				}
			});
		}
		return bean;
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
			for (Map.Entry<String, Bean> entry : complexes.entrySet()) {
				changed = changed || entry.getValue().isChanged();
			}
			return changed;
		}
	}

}
