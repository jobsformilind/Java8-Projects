package com.test.java8.stream.object.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
	private String name;
	private Gender gender;
	private int age;
	
	public PersonDTO(Person person) {
		
	}
}
