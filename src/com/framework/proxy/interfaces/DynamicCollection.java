package com.framework.proxy.interfaces;

import com.framework.events.ChangeListener;

public interface DynamicCollection extends Bean {

	public void addChangeListener(ChangeListener l);

	public void removeChangeListener(ChangeListener l);
	
	public void removeChangeListenerByFrom(Object from);
	
	public boolean hasChangeListenerFrom(Object from);

	public void fireChange();

}
