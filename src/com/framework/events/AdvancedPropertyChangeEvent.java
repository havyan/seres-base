package com.framework.events;

import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;

public class AdvancedPropertyChangeEvent extends PropertyChangeEvent {

	private List<Object> chain = new LinkedList<Object>();

	public AdvancedPropertyChangeEvent(List<Object> chain, Object source, String propertyName, Object oldValue, Object newValue) {
		super(source, propertyName, oldValue, newValue);
		if (chain != null) {
			this.chain.addAll(chain);
		}
		this.chain.add(source);
	}

	public boolean contains(Object target) {
		return chain.contains(target);
	}

	public List<Object> getChain() {
		return chain;
	}

	public void setChain(List<Object> chain) {
		this.chain = chain;
	}

}
