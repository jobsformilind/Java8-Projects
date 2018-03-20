package com.test.hackerranks;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FindDigitSolution {

	public static void main(String[] args) {
		String st1 = findNumbers8(0, 0, 99);
		System.out.println(st1);
	}

	//Java8 solution
	static String findNumbers8(int digit, int start, int end) {
		String strDigit = Integer.toString(digit);
		List<String> collect = IntStream.range(0, 999).mapToObj(i -> Integer.toString(i)).filter(i -> i.contains(strDigit)).collect(Collectors.toList());
		return collect.toString();
	}
	
	//Java7 solution
	static String findNumbers7(int digit, int start, int end) {
		if (start > end || end >= 1000) {
			throw new RuntimeException("Invalid start and end range.");
		}
		StringBuffer buffer = new StringBuffer();
		for (int cnt = start; cnt <= end; cnt++) {
			if (hasDigit(digit, cnt)) {
				buffer.append(cnt).append(" ");
			}
		}
		return buffer.toString();
	}
	static boolean hasDigit(int digit, int number) {
		if(digit == number) {
			return true;
		}
		while (number != 0) {
			if (number % 10 == digit) {
				return true;
			}
			number = number / 10;
		}
		return false;
	}
	
}
