package com.framework.proxy.impl;

import java.util.Collection;

import com.framework.common.BaseUtils;
import com.framework.events.ChangeAdapter;
import com.framework.events.ChangeListener;
import com.framework.events.ChangeSupport;
import com.framework.proxy.interfaces.AbstractBean;
import com.framework.proxy.interfaces.Bean;
import com.framework.proxy.interfaces.DynamicCollection;
import com.rits.cloning.Cloner;

public class DynamicCollectionImpl extends AbstractBean<Collection<?>> implements DynamicCollection {

	private ChangeSupport<Collection<?>> changeSupport;

	private Object[] origin;

	public DynamicCollectionImpl(Collection<?> source) {
		super(source);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object cloneSource() {
		Cloner cloner = new Cloner();
		Collection target = (Collection) BaseUtils.newInstance(this.getSource().getClass());
		for (Object e: this.getSource()) {
			if (e instanceof Bean) {
				target.add(((Bean)e).cloneSource());
			} else {
				target.add(cloner.deepClone(e));
			}
		}
		return target;
	}

}
