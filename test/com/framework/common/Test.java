package com.framework.common;

import java.util.ArrayList;
import java.util.List;

import com.framework.mock.Person;
import com.framework.mock.Profile;
import com.framework.proxy.DynamicObjectFactory2;

public class Test {

	public static void main(String[] args) {
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
		
		Profile profile = new Profile("e", 170, 140);
		person.setProfile(profile);
		profile.setPerson(person);
		Person bean = DynamicObjectFactory2.createDynamicBeanObject(person);
		bean.getProfile().getPerson().setAge(45);;

	}

}
