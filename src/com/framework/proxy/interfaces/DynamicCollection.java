package com.framework.proxy.interfaces;

import com.framework.events.ChangeListener;

public interface DynamicCollection extends Bean {

	public void addChangeListener(ChangeListener l);

	public void removeChangeListener(ChangeListener l);

	public void fireChange();

}
