package com.test.lombok.person3;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Person {
	private String firstName;
	private String lastName;
	private String ssn;
	private int age;

}
