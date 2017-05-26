/**
 * 
 */
package com.framework.proxy.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.apache.commons.beanutils.BeanUtils;

import com.framework.proxy.DynamicObjectFactory2;
import com.framework.proxy.interfaces.Bean;

/**
 * @author HWYan
 * 
 */
public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Person person = new Person();
		person = DynamicObjectFactory2.createDynamicBeanObject(person);
		DynamicObjectFactory2.createDynamicBeanObject(new Person());
		DynamicObjectFactory2.createDynamicBeanObject(new Person());
		person.setName("haowei");
		person.setAge(27);
		person.toString();
		((Bean) person).setProperty("name", "xinxin");

		System.out.println(ArrayList.class.isAssignableFrom(List.class));
		System.out.println(BeanUtils.class.getResource("").getPath());
		System.out.println(JButton.class.isAssignableFrom(JComponent.class));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map = DynamicObjectFactory2.createDynamicObject(map);
		((Bean)map).addPropertyChangeListener(e -> {
			System.out.println(e.getPropertyName() + " changed from " + e.getOldValue() + " to " + e.getNewValue());
		});
		map.put("name", "haowei");
		map.put("age", 32);
		map.remove("name");
	}

}
