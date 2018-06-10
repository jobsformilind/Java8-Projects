package com.test.lombok.person4;

import lombok.NonNull;

public class TestMainPerson {
	public static void main(String[] args) {
		Person person = Person.builder().firstName("John").lastName("Abraham").age(50).build();
		System.out.println(person);
		
		testNotNull(null);
	}

	private static void testNotNull(@NonNull Person person) {
		System.out.println(person);
	}
}
