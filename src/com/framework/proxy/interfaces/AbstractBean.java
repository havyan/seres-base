package com.framework.proxy.interfaces;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.framework.common.BaseUtils;
import com.framework.events.PropertyChangeListenerProxy;
import com.framework.events.AdvancedPropertyChangeSupport;
import com.framework.log.Logger;

public abstract class AbstractBean<T> implements Bean {

	protected T source;

	protected transient AdvancedPropertyChangeSupport changeSupport = new AdvancedPropertyChangeSupport(this);

	protected List<PropertyChangeListenerProxy> propertyChangeListenerProxies;

	public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(l);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (propertyChangeListenerProxies == null) {
			propertyChangeListenerProxies = new ArrayList<PropertyChangeListenerProxy>();
		}
		Object from = this;
		if (listener instanceof PropertyChangeListenerProxy) {
			from = ((PropertyChangeListenerProxy) listener).getFrom();
		}
		PropertyChangeListenerProxy listenerProxy = new PropertyChangeListenerProxy(from, listener) {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals(propertyName) || propertyName.startsWith(e.getPropertyName() + ".") || e.getPropertyName().equals("*")) {
					listener.propertyChange(e);
				}
			}
		};
		changeSupport.addPropertyChangeListener(listenerProxy);
		propertyChangeListenerProxies.add(listenerProxy);
	}

	public boolean hasPropertyChangeListenerFrom(Object from) {
		return changeSupport.hasPropertyChangeListenerFrom(from);
	}

	public void removeAllPropertyChangeListenerFrom(Object from) {
		changeSupport.removeAllPropertyChangeListenerFrom(from);
	}

	public void removePropertyChangeListener(String propertyName, final PropertyChangeListener l) {
		Stream<PropertyChangeListenerProxy> stream = propertyChangeListenerProxies.stream().filter(e -> e.getListener() == l);
		if (stream.count() > 0) {
			changeSupport.removePropertyChangeListener(propertyName, (PropertyChangeListener) stream.toArray()[0]);
		} else {
			changeSupport.removePropertyChangeListener(propertyName, l);
		}
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}

	public void removePropertyChangeListenerFrom(Object from) {
		changeSupport.removePropertyChangeListenerFrom(from);
	}

	public PropertyChangeListenerProxy[] getPropertyChangeListenersFrom(Object from) {
		return changeSupport.getPropertyChangeListenersFrom(from);
	}

	public PropertyChangeListenerProxy[] getPropertyChangeListenersFrom(Object from, String propertyName) {
		return changeSupport.getPropertyChangeListenersFrom(from, propertyName);
	}

	public Map<String, PropertyChangeListenerProxy[]> getPropertyChangeListenersMapFrom(Object from) {
		return changeSupport.getPropertyChangeListenersMapFrom(from);
	}

	public PropertyChangeListener[] getPropertyChangeListeners() {
		return changeSupport.getPropertyChangeListeners();
	}

	public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
		return changeSupport.getPropertyChangeListeners(propertyName);
	}

	public void firePropertyChange(Object target, String propertyName, Object oldValue, Object newValue) {
		Logger.debug("Property [" + propertyName + "] Changed to " + newValue);
		changeSupport.firePropertyChange(target, propertyName, oldValue, newValue);
	}

	public void fireChange() {
		changeSupport.firePropertyChange("*", 0, 1);
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		BaseUtils.setProperty(source, propertyName, value);
	}

	@Override
	public Object getProperty(String propertyName) {
		return BaseUtils.getProperty(source, propertyName);
	}

	public T getSource() {
		return source;
	}

	@SuppressWarnings("unchecked")
	public void setSource(Object source) {
		this.source = (T) source;
	}

}
