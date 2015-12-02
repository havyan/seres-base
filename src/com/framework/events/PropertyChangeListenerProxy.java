package com.framework.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PropertyChangeListenerProxy implements PropertyChangeListener {
	
	private Object from;
	
	private PropertyChangeListener listener;

	public PropertyChangeListenerProxy() {
		super();
	}

	public PropertyChangeListenerProxy(Object from) {
		super();
		this.from = from;
	}

	public PropertyChangeListenerProxy(Object from, PropertyChangeListener listener) {
		super();
		this.from = from;
		this.listener = listener;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(this.listener != null) {
			listener.propertyChange(evt);
		}
	}

	public Object getFrom() {
		return from;
	}

	public void setFrom(Object from) {
		this.from = from;
	}

	public PropertyChangeListener getListener() {
		return listener;
	}

	public void setListener(PropertyChangeListener listener) {
		this.listener = listener;
	}

}
