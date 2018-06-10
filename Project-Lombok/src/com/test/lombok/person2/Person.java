package com.test.lombok.person2;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Person {
	private String firstName;
	private String lastName;
	private String ssn;
	private int age;

}
