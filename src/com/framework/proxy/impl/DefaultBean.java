/**
 * 
 */
package com.framework.proxy.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.framework.common.BaseUtils;
import com.framework.log.Logger;
import com.framework.proxy.DynamicObjectFactory;
import com.framework.proxy.interfaces.Bean;

/**
 * @author HWYan
 * 
 */
public class DefaultBean implements Bean {

	private Object source;

	private int status = NEW;

	private Map<String, Object> changedProperties = new HashMap<String, Object>();

	private Map<String, Object> complexProperties = new HashMap<String, Object>();

	protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public DefaultBean() {

	}

	public DefaultBean(Object source) {
		this.source = source;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		Logger.info("begin set propety name &" + propertyName);
		if (source != null) {
			if (status != DELETED) {
				if (status == NEW) {
					BaseUtils.setProperty(source, propertyName, value);
				} else if (status == UNCHANGED) {
					changedProperties.put(propertyName, value);
					status = UPDATED;
				} else if (status == UPDATED) {
					changedProperties.put(propertyName, value);
				}
			}
		}
		Logger.info("end set propety name &" + propertyName);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getProperty(String propertyName) {
		if (source != null) {
			if (changedProperties.containsKey(propertyName)) {
				return changedProperties.get(propertyName);
			} else {
				if (complexProperties.containsKey(propertyName)) {
					return complexProperties.get(propertyName);
				} else {
					Method getter = BaseUtils.getReadMethod(source, propertyName);
					if (!getter.getReturnType().isPrimitive() && !Modifier.isFinal(getter.getReturnType().getModifiers())) {
						Object value = BaseUtils.getProperty(source, propertyName);
						if (value != null) {
							if (List.class.isInstance(value)) {
								List dynamicList = (List<?>) BaseUtils.newInstance(value.getClass());
								for (Object obj : (List<?>) value) {
									if (!(obj instanceof Bean)) {
										obj = DynamicObjectFactory.createDynamicBeanObject((Bean) obj);
										((Bean) obj).addPropertyChangeListener(this);
									}
									((Bean) obj).setStatus(Bean.UNCHANGED);
									dynamicList.add(obj);
								}
								value = dynamicList;
							} else {
								value = DynamicObjectFactory.createDynamicBeanObject(value);
								((Bean) value).addPropertyChangeListener(this);
							}
							complexProperties.put(propertyName, value);
						}
						return value;
					}
				}
			}
			return BaseUtils.getProperty(source, propertyName);
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

	public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean flushPropertyChange() {
		boolean changed = changedProperties.size() > 0;
		for (Map.Entry<String, Object> entry : changedProperties.entrySet()) {
			BaseUtils.setProperty(source, entry.getKey(), entry.getValue());
		}

		for (Map.Entry<String, Object> entry : complexProperties.entrySet()) {
			if (entry.getValue() instanceof Bean) {
				changed = changed || ((Bean) entry.getValue()).flushPropertyChange();
			} else if (entry.getValue() instanceof List) {
				List currentList = (List) entry.getValue();
				List srcList = (List) getProperty(entry.getKey());
				if (!changed) {
					boolean listNotChanged = currentList.size() == srcList.size();
					if (listNotChanged) {
						for (int i = 0; i < srcList.size(); i++) {
							listNotChanged = listNotChanged && srcList.get(i) == currentList.get(i);
						}
					}
					changed = changed || !listNotChanged;
				}
				srcList.clear();
				for (Object obj : currentList) {
					Object srcObject = obj;
					if (obj instanceof Bean) {
						changed = changed || ((Bean) obj).flushPropertyChange();
						srcObject = ((Bean) obj).getSource();
					}
					srcList.add(srcObject);
				}
			}
		}

		return changed;
	}

	@Override
	public void cancelPropertyChange() {
		changedProperties.clear();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}

}
