package com.framework.events;

import javax.swing.event.EventListenerList;

public class ChangeSupport<T> {

	private T source;

	public ChangeSupport(T source) {
		this.source = source;
	}

	protected EventListenerList listenerList = new EventListenerList();

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	public void fireChange() {
		ChangeEvent e = new ChangeEvent(source);
		for (ChangeListener l : getListeners()) {
			l.change(e);
		}
	}

	public ChangeListener[] getListeners() {
		return listenerList.getListeners(ChangeListener.class);
	}

}
