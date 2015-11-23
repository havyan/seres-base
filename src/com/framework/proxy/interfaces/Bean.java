/**
 * 
 */
package com.framework.proxy.interfaces;

import java.beans.PropertyChangeListener;

import com.framework.proxy.DynamicInterface;

/**
 * @author HWYan
 * 
 */
public interface Bean extends DynamicInterface,PropertyChangeListener, PropertiesContainer{

	public int getStatus();

	public void setStatus(int status);

	public void setProperty(String propertyName, Object value);

	public Object getProperty(String propertyName);

	public static final int NEW = 0;
	
	public static final int UNCHANGED = 1;

	public static final int UPDATED = 2;

	public static final int DELETED = 3;

}
