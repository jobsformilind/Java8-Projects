package com.test.java8.stream.object;

import java.util.List;
import java.util.stream.Collectors;

public class PersonStreamTest {
	public static void main(String[] args) {
		List<Person> persons = PersonCreator.getPersons();

		//Create a map, key as name_age and value as person object
		out(persons.stream()
					.collect(Collectors.toMap(
					   person->person.getName()+"_" + person.getAge(), 
					   person->person)));
	
		//Create a map, key as name and value as list of people by the name
		out(persons.stream()
				.collect(Collectors.groupingBy(Person::getName)));		
	
		//Create a map, key as name and value is all the ages of people by the name
		out(persons.stream()
				.collect(Collectors.groupingBy(Person::getName, Collectors.mapping(Person::getAge, Collectors.toList()))));
		
		//groups persons by gender
		out(persons.stream().collect(Collectors.groupingBy(Person::getGender)));
		
	}
	
	private static void out(Object obj) {
		System.out.println("");
		System.out.println(obj);
	}
}
