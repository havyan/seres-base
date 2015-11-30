package com.framework.proxy.impl;

import java.util.Collection;

import com.framework.events.ChangeAdapter;
import com.framework.events.ChangeListener;
import com.framework.events.ChangeSupport;
import com.framework.proxy.interfaces.AbstractBean;
import com.framework.proxy.interfaces.DynamicCollection;

public class DynamicCollectionImpl extends AbstractBean<Collection<?>> implements DynamicCollection {

	private ChangeSupport<Collection<?>> changeSupport;

	private Object[] origin;

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

	public void removeChangeListenerByFrom(Object from) {
		for (ChangeListener l : changeSupport.getListeners()) {
			if (l instanceof ChangeAdapter && ((ChangeAdapter) l).getFrom() == from) {
				removeChangeListener(l);
			}
		}
	}

	public boolean hasChangeListenerFrom(Object from) {
		for (ChangeListener l : changeSupport.getListeners()) {
			if (l instanceof ChangeAdapter && ((ChangeAdapter) l).getFrom() == from) {
				return true;
			}
		}
		return false;
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

}
