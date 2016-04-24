/**
 * 
 */
package com.framework.proxy.interfaces;

import java.beans.PropertyChangeListener;
import java.util.Map;

import com.framework.events.PropertyChangeListenerProxy;
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

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

	public void removePropertyChangeListenerFrom(Object from);

	public PropertyChangeListener[] getPropertyChangeListeners();

	public PropertyChangeListener[] getPropertyChangeListeners(String propertyName);

	public PropertyChangeListenerProxy[] getPropertyChangeListenersFrom(Object from);

	public PropertyChangeListenerProxy[] getPropertyChangeListenersFrom(Object from, String propertyName);

	public Map<String, PropertyChangeListenerProxy[]> getPropertyChangeListenersMapFrom(Object from);

	public void removeAllPropertyChangeListenerFrom(Object from);

	public boolean hasPropertyChangeListenerFrom(Object from);

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	public void firePropertyChange(Object target, String propertyName, Object oldValue, Object newValue);
	
	public void fireChange();

}
