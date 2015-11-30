package com.framework.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		Map<String, String> map = new HashMap<String, String>();
		map.put("attr1", "value1");
		map.put("attr2", "value2");
		person.setMap(map);
		return person;
	}

}
