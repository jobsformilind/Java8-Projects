package com.test.lombok.person1;

public class TestMainPerson {
	public static void main(String[] args) {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Abraham");
		person.setSsn("ABCD12345KJ");
		person.setAge(50);
		
		System.out.println(person);
	}
}
