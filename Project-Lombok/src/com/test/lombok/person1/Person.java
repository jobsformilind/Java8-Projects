package com.test.lombok.person1;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude= {"ssn", "age"})
public class Person {
	private String firstName;
	private String lastName;
	private String ssn;
	private int age;

}
