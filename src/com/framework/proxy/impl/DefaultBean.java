/**
 * 
 */
package com.framework.proxy.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
import com.framework.proxy.interfaces.Bean;
import com.framework.proxy.interfaces.DynamicCollection;

/**
 * @author HWYan
 * 
 */
public class DefaultBean implements Bean, ChangeListener {

	private Object source;

	private List<String> changes = new ArrayList<String>();

	private Map<String, Bean> complexes = new HashMap<String, Bean>();

	private Map<String, DynamicCollection> lists = new HashMap<String, DynamicCollection>();

	protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public DefaultBean() {

	}

	public DefaultBean(Object source) {
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
				firePropertyChange(propertyName, value, current);
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
						if (!(value instanceof DynamicCollection)) {
							value = DynamicObjectFactory2.createDynamicListObject((List<?>) value);
						}
						lists.put(propertyName, (DynamicCollection) value);
					} else {
						if (!(value instanceof Bean)) {
							value = DynamicObjectFactory2.createDynamicBeanObject(value);
						}
						complexes.put(propertyName, (Bean) value);
					}
				}
				return value;
			}
		}
		return null;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(l);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
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
