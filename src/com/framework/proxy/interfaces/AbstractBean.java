package com.framework.proxy.interfaces;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.framework.common.BaseUtils;
import com.framework.events.PropertyChangeAdapter;
import com.framework.log.Logger;

public abstract class AbstractBean<T> implements Bean {

	protected T source;

	protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(l);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener((e) -> {
			if (e.getPropertyName().equals(propertyName) || propertyName.startsWith(e.getPropertyName() + ".")) {
				listener.propertyChange(e);
			}
		});
	}
	
	public boolean hasPropertyChangeListenerFrom(Object from) {
		for (PropertyChangeListener l : changeSupport.getPropertyChangeListeners()) {
			if (l instanceof PropertyChangeAdapter && ((PropertyChangeAdapter) l).getFrom() == from) {
				return true;
			}
		}
		return false;
	}

	public void removePropertyChangeListenerByFrom(Object from) {
		for (PropertyChangeListener l : changeSupport.getPropertyChangeListeners()) {
			if (l instanceof PropertyChangeAdapter && ((PropertyChangeAdapter) l).getFrom() == from) {
				removePropertyChangeListener(l);
			}
		}
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		Logger.debug("Property [" + propertyName + "] Changed to " + newValue);
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
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
