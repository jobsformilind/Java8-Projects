package com.test.java8.stream.integers;

import java.util.OptionalInt;
import java.util.stream.IntStream;

public class IntSTreamWithMethods {
	public static void main(String[] args) {
		OptionalInt first = IntStream.of(1, 2, 3, 5, 4)
			.filter(IntSTreamWithMethods::isGT3)
			.filter(IntSTreamWithMethods::isEven)
			.map(IntSTreamWithMethods::doubleIt)
			.findFirst();
		System.out.println(first.getAsInt());
	}

	private static boolean isGT3(int number) {
		System.out.println("isGT3 : " + number);
		return number > 3;
	}

	private static boolean isEven(int number) {
		System.out.println("isEven : " + number);
		return number % 2 == 0;
	}

	private static int doubleIt(int number) {
		System.out.println("doubleIt : " + number);
		return number * 2;
	}
}
