package com.framework.common;

import com.framework.mock.MockFactory;
import com.framework.mock.Person;

import junit.framework.TestCase;

public class BaseUtilsTest  extends TestCase{

	public void testConvertPropertyName() {
		assertEquals(BaseUtils.convertPropertyName("a.1.b.c"), "a[1].b.c");
		assertEquals(BaseUtils.convertPropertyName("1.a.b.c"), "[1].a.b.c");
		assertEquals(BaseUtils.convertPropertyName("a1.1.b1.2"), "a1[1].b1[2]");
		assertEquals(BaseUtils.convertPropertyName("1"), "[1]");
	}
	
	public void testGetProperty() {
		Person person = MockFactory.createPerson();
		assertEquals(BaseUtils.getProperty(person, "profile.face"), "e");
		assertEquals(BaseUtils.getProperty(person, "profiles.2.face"), "c");
		assertEquals(BaseUtils.getProperty(person, "name"), "Haowei");
	}
	
	public void testSetProperty() {
		Person person = MockFactory.createPerson();
		assertEquals(BaseUtils.getProperty(person, "map.attr1"), "value1");
		assertEquals(BaseUtils.getProperty(person.getMap(), "attr1"), "value1");
	}
	
}
