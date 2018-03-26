package com.test.java8.stream.object;

import java.util.List;
import java.util.stream.Collectors;

public class PersonStreamTest {
	public static void main(String[] args) {
		List<Person> persons = PersonCreator.getPersons();

		//Create a map, key as name_age and value as person object
		persons.stream()
			   .collect(Collectors.toMap(
					   person->person.getName()+"_" + person.getAge(), 
					   person->person));
	
		//Create a map, key as name and value as list of people
		persons.stream()
		   .collect(Collectors.toMap(
				   person->person.getName(), 
				   person->person));
		
	
	}
}
