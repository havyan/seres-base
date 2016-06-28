package com.framework.proxy.impl;

import java.util.Map;
import java.util.Map.Entry;

import com.framework.common.BaseUtils;
import com.framework.proxy.interfaces.AbstractBean;
import com.framework.proxy.interfaces.Bean;
import com.framework.proxy.interfaces.DynamicMap;
import com.rits.cloning.Cloner;

public class DynamicMapImpl extends AbstractBean<Map<?, ?>> implements DynamicMap {

	public DynamicMapImpl(Map<?, ?> source) {
		super(source);
	}

	@Override
	public boolean isChanged() {
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object cloneSource() {
		Cloner cloner = new Cloner();
		Map target = (Map) BaseUtils.newInstance(this.getSource().getClass());
		for (Entry<?, ?> entry: this.getSource().entrySet()) {
			if (entry.getValue() instanceof Bean) {
				target.put(entry.getKey(), ((Bean)entry.getValue() ).cloneSource());
			} else {
				target.put(entry.getKey(), cloner.deepClone(entry.getValue()));
			}
		}
		return target;
	}

}
