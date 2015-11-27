package com.framework.proxy.impl;

import java.util.Collection;

import com.framework.events.ChangeListener;
import com.framework.events.ChangeSupport;
import com.framework.proxy.interfaces.DynamicCollection;

public class DynamicCollectionImpl implements DynamicCollection {

	private ChangeSupport<Collection<?>> changeSupport;

	private Collection<?> source;

	public DynamicCollectionImpl(Collection<?> source) {
		super();
		this.source = source;
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

}
