package com.framework.events;

public class ChangeEvent {

	private Object source;

	public ChangeEvent(Object source) {
		super();
		this.source = source;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

}
