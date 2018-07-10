package com.test.java8.stream.object.user;

import java.util.List;

public class PersonRepo {

	public List<Person> findAll() {
		return PersonCreator.getPersons();
	}
}
