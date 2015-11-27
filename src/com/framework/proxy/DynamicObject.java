package com.framework.proxy;

import net.sf.cglib.proxy.NoOp;

public interface DynamicObject extends NoOp {

	public Object getSource();

	public void setSource(Object source);
	
	public boolean isChanged();

}
