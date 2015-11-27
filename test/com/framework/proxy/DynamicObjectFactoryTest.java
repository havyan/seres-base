package com.framework.proxy;

import java.util.ArrayList;
import java.util.List;

import com.framework.mock.Person;
import com.framework.mock.Profile;
import com.framework.proxy.interfaces.Bean;
import com.framework.proxy.interfaces.DynamicCollection;

import junit.framework.TestCase;

public class DynamicObjectFactoryTest extends TestCase {

	public void setUp() {

	}

	public void testCreateDynamicBeanObject() throws Exception {
		Person person = new Person("Haowei", 31);
		List<Profile> profiles = new ArrayList<Profile>();
		Profile profile1 = new Profile("a", 170, 140);
		Profile profile2 = new Profile("b", 170, 140);
		Profile profile3 = new Profile("c", 170, 140);
		Profile profile4 = new Profile("d", 170, 140);
		profiles.add(profile1);
		profiles.add(profile2);
		profiles.add(profile3);
		profiles.add(profile4);
		person.setProfiles(profiles);
		person.setProfile(new Profile("e", 170, 140));
		Person bean = DynamicObjectFactory2.createDynamicBeanObject(person);
		assertTrue(bean instanceof Bean);
		assertTrue(bean.getProfile() instanceof Bean);
		assertTrue(bean.getProfiles() instanceof DynamicCollection);
		assertTrue(bean.getProfiles().get(0) instanceof Bean);
		bean.getProfiles().remove(profile3);
		assertTrue(bean.getProfiles().size() == 3);
		profiles = new ArrayList<Profile>();
		profiles.add(profile1);
		profiles.add(profile2);
		bean.getProfiles().removeAll(profiles);
		assertTrue(bean.getProfiles().size() == 1);
		assertTrue(bean.getProfiles().get(0).getFace().equals("d"));
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
