package com.test.java8.joda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class LocalTimeTest {

	public static void main(String[] args) {
		LocalTime time = LocalTime.now();
		System.out.println("Time : " + time);

		LocalDate date = LocalDate.now();
		System.out.println("Date : " + date);
		String format = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
		System.out.println("Formated Date : " + format);

		LocalDateTime dt = LocalDateTime.now();
		System.out.println("Date Time : " + dt);
		
		
		LocalDateTime past = LocalDateTime.of(1978, 2, 9, 5, 15);
		LocalDateTime now = LocalDateTime.now();
		Period between = Period.between(past.toLocalDate(), now.toLocalDate());
		System.out.println(between.getYears());
		
	}
}
