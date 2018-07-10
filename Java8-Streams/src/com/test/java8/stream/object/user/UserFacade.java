package com.test.java8.stream.object.user;

import java.util.List;
import java.util.stream.Collectors;

public class UserFacade {
	private PersonRepo personRepo;
	private PersonMapper mapper;
	
	public List<PersonDTO> getAllPersons() {
		//return personRepo.findAll().parallelStream().map(PersonDTO::new).collect(Collectors.toList());
		return personRepo.findAll().parallelStream().map(mapper::toDTO).collect(Collectors.toList());
	}

}


//@Component
class PersonMapper {
	public PersonDTO toDTO(Person person) {
		PersonDTO dto = new PersonDTO();
		dto.setName(person.getName());
		dto.setAge(person.getAge());
		return dto;
	}
}
