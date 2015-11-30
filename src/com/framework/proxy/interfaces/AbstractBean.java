package com.framework.proxy.interfaces;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.framework.log.Logger;

public abstract class AbstractBean implements Bean {

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

	public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		Logger.debug("Property Changed: " + propertyName);
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

}
