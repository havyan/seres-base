package com.framework.proxy.impl;

import java.util.Collection;

import com.framework.common.BaseUtils;
import com.framework.events.ChangeListener;
import com.framework.events.ChangeSupport;
import com.framework.proxy.interfaces.AbstractBean;
import com.framework.proxy.interfaces.DynamicCollection;

public class DynamicCollectionImpl extends AbstractBean implements DynamicCollection {

	private ChangeSupport<Collection<?>> changeSupport;

	private Object[] origin;

	private Collection<?> source;

	public DynamicCollectionImpl(Collection<?> source) {
		super();
		this.source = source;
		this.origin = source.toArray();
		this.changeSupport = new ChangeSupport<Collection<?>>(source);
	}

	public void addChangeListener(ChangeListener l) {
		changeSupport.addChangeListener(l);
	}

	public void removeChangeListener(ChangeListener l) {
		changeSupport.removeChangeListener(l);
	}

	public void fireChange() {
		changeSupport.fireChange();
	}

	@Override
	public Collection<?> getSource() {
		return source;
	}

	@Override
	public void setSource(Object source) {
		this.source = (Collection<?>) source;
	}

	@Override
	public boolean isChanged() {
		boolean changed = isDifferent(origin, source.toArray());
		if (changed) {
			return changed;
		}
		return false;
	}

	protected boolean isDifferent(Object[] array1, Object[] array2) {
		if (array1.length != array2.length) {
			return true;
		} else {
			for (int i = 0; i < array1.length; i++) {
				if (array1[i] != array2[i]) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		BaseUtils.setProperty(source, propertyName, value);
	}

	@Override
	public Object getProperty(String propertyName) {
		return BaseUtils.getProperty(source, propertyName);
	}

}
