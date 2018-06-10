package com.test.lombok.person4;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class Person {
	private String firstName;
	private String lastName;
	private String ssn;
	private int age;

}
