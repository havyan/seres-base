package com.framework.events;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PropertyChangeSupportProxy extends PropertyChangeSupport {

	private Set<String> propertyNames = new HashSet<String>();

	public PropertyChangeSupportProxy(Object sourceBean) {
		super(sourceBean);
	}

	public void removeAllPropertyChangeListenerFrom(Object from) {
		removePropertyChangeListenerFrom(from);
		for (String propertyName : propertyNames) {
			for (PropertyChangeListener l : this.getPropertyChangeListeners(propertyName)) {
				if (l instanceof PropertyChangeListenerProxy && ((PropertyChangeListenerProxy) l).getFrom() == from) {
					removePropertyChangeListener(propertyName, l);
				}
			}
		}
	}

	public void removePropertyChangeListenerFrom(Object from) {
		for (PropertyChangeListener l : this.getPropertyChangeListeners()) {
			if (l instanceof PropertyChangeListenerProxy && ((PropertyChangeListenerProxy) l).getFrom() == from) {
				removePropertyChangeListener(l);
			}
		}
	}

	public boolean hasPropertyChangeListenerFrom(Object from) {
		for (PropertyChangeListener l : this.getPropertyChangeListeners()) {
			if (l instanceof PropertyChangeListenerProxy && ((PropertyChangeListenerProxy) l).getFrom() == from) {
				return true;
			}
		}
		for (String propertyName : propertyNames) {
			for (PropertyChangeListener l : this.getPropertyChangeListeners(propertyName)) {
				if (l instanceof PropertyChangeListenerProxy && ((PropertyChangeListenerProxy) l).getFrom() == from) {
					return true;
				}
			}
		}
		return false;
	}

	public PropertyChangeListenerProxy[] getPropertyChangeListenersFrom(Object from) {
		return getPropertyChangeListenersFrom(getPropertyChangeListeners(), from);
	}

	private PropertyChangeListenerProxy[] getPropertyChangeListenersFrom(PropertyChangeListener[] listeners, Object from) {
		if (listeners != null) {
			List<PropertyChangeListenerProxy> list = new ArrayList<PropertyChangeListenerProxy>();
			for (PropertyChangeListener l : listeners) {
				if (l instanceof PropertyChangeListenerProxy && ((PropertyChangeListenerProxy) l).getFrom() == from) {
					list.add((PropertyChangeListenerProxy) l);
				}
			}
			return list.toArray(new PropertyChangeListenerProxy[0]);
		}

		return new PropertyChangeListenerProxy[0];
	}

	public PropertyChangeListenerProxy[] getPropertyChangeListenersFrom(Object from, String propertyName) {
		return getPropertyChangeListenersFrom(getPropertyChangeListeners(propertyName), from);
	}

	public Map<String, PropertyChangeListenerProxy[]> getPropertyChangeListenersMapFrom(Object from) {
		Map<String, PropertyChangeListenerProxy[]> map = new HashMap<String, PropertyChangeListenerProxy[]>();
		for (String propertyName : propertyNames) {
			map.put(propertyName, getPropertyChangeListenersFrom(from, propertyName));
		}
		return map;
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyNames.add(propertyName);
		super.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		super.removePropertyChangeListener(propertyName, listener);
		if (!hasListeners(propertyName)) {
			propertyNames.remove(propertyName);
		}
	}

}
