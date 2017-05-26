package com.framework.proxy.impl;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.framework.common.BaseUtils;
import com.framework.events.ChangeAdapter;
import com.framework.events.ChangeEvent;
import com.framework.events.ChangeListener;
import com.framework.events.ChangeSupport;
import com.framework.events.PropertyChangeListenerProxy;
import com.framework.log.Logger;
import com.framework.proxy.interfaces.AbstractBean;
import com.framework.proxy.interfaces.Bean;
import com.framework.proxy.interfaces.DynamicCollection;
import com.rits.cloning.Cloner;

import net.sf.cglib.proxy.MethodProxy;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DynamicCollectionImpl extends AbstractBean<Collection> implements DynamicCollection {

	private ChangeSupport<Collection> changeSupport;

	private boolean changed = false;

	private Collection proxy;

	public DynamicCollectionImpl(Collection source) {
		super(source);
		this.createProxy();
		this.changeSupport = new ChangeSupport<Collection>(source);
	}

	private void createProxy() {
		proxy = (Collection) BaseUtils.newInstance(this.source.getClass());
		for (Object e : source) {
			Object result = convert2DynamicObject(e);
			if (result instanceof Bean) {
				bindBean((Bean) result);
			}
			proxy.add(result);
		}
	}

	@Override
	public Object invoke(Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		Object[] beforeArray = this.proxy.toArray();
		args = this.convertArgs(args, beforeArray);
		Object result = methodProxy.invoke(this.proxy, args);
		Object[] afterArray = this.proxy.toArray();
		if (this.isDifferent(beforeArray, afterArray)) {
			Logger.debug("List changed");
			this.syncData(afterArray);
			this.handleRemoved(beforeArray, this.proxy);
			Logger.debug("Fire list changed");
			this.fireChange();
		}
		return result;
	}

	private void syncData(Object[] proxyArray) {
		Object[] sourceArray = new Object[proxyArray.length];
		this.source.clear();
		this.proxy.clear();
		for (int i = 0; i < proxyArray.length; i++) {
			Object proxyElement = proxyArray[i];
			Object sourceElement = proxyElement;
			if (proxyElement instanceof Bean) {
				sourceElement = ((Bean) proxyElement).getSource();
			} else {
				proxyElement = this.convert2DynamicObject(proxyElement);
			}
			if (proxyElement instanceof Bean) {
				this.bindBean((Bean) proxyElement);
			}
			proxyArray[i] = proxyElement;
			sourceArray[i] = sourceElement;
		}
		this.proxy.addAll(Arrays.asList(proxyArray));
		this.source.addAll(Arrays.asList(sourceArray));
	}

	protected void handleRemoved(Object[] array, Collection collection) {
		if (ArrayUtils.isNotEmpty(array) && collection != null) {
			for (Object e : array) {
				if (!collection.contains(e)) {
					if (e != null && e instanceof Bean) {
						if (e instanceof DynamicCollection) {
							((DynamicCollection) e).removeChangeListenerByFrom(this);
						}
						((Bean) e).removeAllPropertyChangeListenerFrom(this);
					}
				}
			}
		}
	}

	protected boolean isDifferent(Object[] beforeArray, Object[] afterArray) {
		if (beforeArray.length != afterArray.length) {
			return true;
		} else {
			for (int i = 0; i < afterArray.length; i++) {
				if (beforeArray[i] != afterArray[i]) {
					return true;
				}
			}
		}
		return false;
	}

	protected void bindBean(Bean bean) {
		if (bean != null) {
			if (bean instanceof DynamicCollection) {
				DynamicCollection dynamicCollection = (DynamicCollection) bean;
				if (!dynamicCollection.hasChangeListenerFrom(this)) {
					dynamicCollection.addChangeListener(new ChangeAdapter(this) {
						public void change(ChangeEvent e) {
							int index = findIndex(source.toArray(), bean);
							firePropertyChange(null, index + "", null, e.getSource());
						}
					});
				}
			}
			if (!bean.hasPropertyChangeListenerFrom(this)) {
				bean.addPropertyChangeListener(new PropertyChangeListenerProxy(this) {
					public void propertyChange(PropertyChangeEvent e) {
						int index = findIndex(source.toArray(), bean);
						if (index != -1) {
							List<Object> chain = BaseUtils.getChain(e);
							if (!chain.contains(source)) {
								firePropertyChange(chain, index + "." + e.getPropertyName(), e.getOldValue(), e.getNewValue());
							}
						}
					}
				});
			}
		}
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

	public void removeChangeListenerByFrom(Object from) {
		for (ChangeListener l : changeSupport.getListeners()) {
			if (l instanceof ChangeAdapter && ((ChangeAdapter) l).getFrom() == from) {
				removeChangeListener(l);
			}
		}
	}

	public boolean hasChangeListenerFrom(Object from) {
		for (ChangeListener l : changeSupport.getListeners()) {
			if (l instanceof ChangeAdapter && ((ChangeAdapter) l).getFrom() == from) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isChanged() {
		return changed;
	}

	@Override
	public Object cloneSource() {
		Cloner cloner = new Cloner();
		Collection target = (Collection) BaseUtils.newInstance(this.getSource().getClass());
		for (Object e : this.getSource()) {
			if (e instanceof Bean) {
				target.add(((Bean) e).cloneSource());
			} else {
				target.add(cloner.deepClone(e));
			}
		}
		return target;
	}

}
