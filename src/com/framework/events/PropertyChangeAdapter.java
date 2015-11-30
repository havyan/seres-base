package com.framework.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PropertyChangeAdapter implements PropertyChangeListener {
	
	private Object from;

	public PropertyChangeAdapter() {
		super();
	}

	public PropertyChangeAdapter(Object from) {
		super();
		this.from = from;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
	}

	public Object getFrom() {
		return from;
	}

	public void setFrom(Object from) {
		this.from = from;
	}

}
