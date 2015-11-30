package com.framework.events;

import java.util.ArrayList;
import java.util.List;

public class ChangeSupport<T> {

	private T source;

	public ChangeSupport(T source) {
		this.source = source;
	}

	private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	public void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}

	public void removeChangeListener(ChangeListener l) {
		listeners.remove(l);
	}

	public void fireChange() {
		ChangeEvent e = new ChangeEvent(source);
		for (ChangeListener l : listeners) {
			l.change(e);
		}
	}

	public ChangeListener[] getListeners() {
		return listeners.toArray(new ChangeListener[0]);
	}

}
