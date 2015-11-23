/**
 * 
 */
package com.framework.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.CallbackFilter;

/**
 * @author HWYan
 *
 */
public class DynamicCallbackFilter implements CallbackFilter{

	@Override
	public int accept(Method method) {
		return 0;
	}

}
