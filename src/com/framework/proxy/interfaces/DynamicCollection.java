package com.framework.proxy.interfaces;

import com.framework.events.ChangeListener;
import com.framework.proxy.DynamicInterface;

public interface DynamicCollection extends DynamicInterface {

	public void addChangeListener(ChangeListener l);

	public void removeChangeListener(ChangeListener l);

	public void fireChange();

}
