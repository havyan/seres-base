package com.framework.proxy.impl;

import java.util.Map;

import com.framework.proxy.interfaces.AbstractBean;
import com.framework.proxy.interfaces.DynamicMap;

public class DynamicMapImpl extends AbstractBean<Map<?, ?>> implements DynamicMap {

	public DynamicMapImpl(Map<?, ?> source) {
		super(source);
	}

	@Override
	public boolean isChanged() {
		return false;
	}

}
