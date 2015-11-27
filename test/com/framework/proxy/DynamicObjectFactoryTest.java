package com.framework.proxy;

import java.util.ArrayList;
import java.util.List;

import com.framework.mock.Person;
import com.framework.proxy.interfaces.Bean;
import com.framework.proxy.interfaces.DynamicCollection;

import junit.framework.TestCase;

public class DynamicObjectFactoryTest extends TestCase {

	private Person person = new Person();

	public void setUp() {

	}

	public void testCreateDynamicBeanObject() throws Exception {
		Person bean = DynamicObjectFactory2.createDynamicBeanObject(person);
		assertTrue(bean instanceof Bean);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testCreateDynamicListObject() throws Exception {
		List<Person> persons = new ArrayList<Person>();
		persons.add(new Person("Haowei", 31));
		persons.add(new Person("Zhangsan", 15));
		persons.add(new Person("Lisi", 16));
		persons.add(new Person("Kobe", 17));
		persons.add(new Person("James", 20));
		List list = DynamicObjectFactory2.createDynamicListObject(persons);
		assertTrue(list instanceof DynamicCollection);
		assertTrue(list.get(0) instanceof Bean);
		DynamicCollection dlist = (DynamicCollection) list;
		List<Object> events = new ArrayList<Object>();
		dlist.addChangeListener((e) -> {
			events.add(e);
		});
		list.get(3);
		list.isEmpty();
		list.hashCode();
		assertTrue(events.size() == 0);
		list.add(new Person());
		assertTrue(events.size() == 1);
	}
	
	public void testEquals() {
		Person person1 = new Person("Haowei", 31);
		Person person2 = DynamicObjectFactory2.createDynamicBeanObject(person1);
		assertTrue(person2.equals(person1));
	}

}
