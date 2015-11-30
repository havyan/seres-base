package com.framework.events;

public class ChangeAdapter implements ChangeListener {

	private Object from;

	public ChangeAdapter() {
		super();
	}

	public ChangeAdapter(Object from) {
		super();
		this.from = from;
	}

	@Override
	public void change(ChangeEvent e) {

	}

	public Object getFrom() {
		return from;
	}

	public void setFrom(Object from) {
		this.from = from;
	}
}
