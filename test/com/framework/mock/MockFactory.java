package com.framework.mock;

import java.util.ArrayList;
import java.util.List;

public class MockFactory {
	
	public static Person createPerson() {
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
		return person;
	}

}
