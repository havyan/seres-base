package com.framework.proxy.interfaces;

import java.beans.PropertyChangeListener;

public interface PropertiesContainer {
	
	public void addPropertyChangeListener(PropertyChangeListener l);

	public void removePropertyChangeListener(PropertyChangeListener l);

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue);

	public boolean flushPropertyChange();
	
	public void cancelPropertyChange();

}
