package com.test.java8.integers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

	public static void main(String[] args) {
		String st1 = findNumbers(0, 0, 99);
		//System.out.println(st1);
		
		System.out.println(isAnagram("hello", "Hello"));

	}

	static boolean isAnagram(String str1, String str2) {
		if (str1 == null || str2 == null) return false;
		String st1 = str1.toLowerCase();
		String st2 = str2.toLowerCase();
		if(st1.equals(st2)) return true;
		return new StringBuffer(st1).reverse().toString().equals(st2);
	}

	static String findNumbers(int digit, int start, int end) {
		String strDigit = Integer.toString(digit);
		List<String> collect = IntStream.range(0, 999).mapToObj(i -> Integer.toString(i)).filter(i -> i.contains(strDigit)).collect(Collectors.toList());
		return collect.toString();
	}

}
