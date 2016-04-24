package com.framework.events;

import java.beans.PropertyChangeEvent;

public class AdvancedPropertyChangeEvent extends PropertyChangeEvent {

	private Object target;

	public AdvancedPropertyChangeEvent(Object target, Object source, String propertyName, Object oldValue, Object newValue) {
		super(source, propertyName, oldValue, newValue);
		this.target = target;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

}
