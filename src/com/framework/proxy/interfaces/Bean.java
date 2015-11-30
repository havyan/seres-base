/**
 * 
 */
package com.framework.proxy.interfaces;

import java.beans.PropertyChangeListener;

import com.framework.proxy.DynamicObject;

/**
 * @author HWYan
 * 
 */
public interface Bean extends DynamicObject {

	public void setProperty(String propertyName, Object value);

	public Object getProperty(String propertyName);

	public void addPropertyChangeListener(PropertyChangeListener l);

	public void removePropertyChangeListener(PropertyChangeListener l);
	
	public void removePropertyChangeListenerByFrom(Object from);
	
	public boolean hasPropertyChangeListenerFrom(Object from);

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue);

}
