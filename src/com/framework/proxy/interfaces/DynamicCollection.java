package com.framework.proxy.interfaces;

import com.framework.events.ChangeListener;
import com.framework.proxy.DynamicObject;

public interface DynamicCollection extends DynamicObject {

	public void addChangeListener(ChangeListener l);

	public void removeChangeListener(ChangeListener l);

	public void fireChange();

}
