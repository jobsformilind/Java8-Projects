package com.test.interview;

public class StringReverse {
	public static void main(String[] args) {
		String hello = "hello";
		System.out.println(reverse1(hello));
		System.out.println(reverse2(hello));
	}

	private static String reverse1(String s) {
		StringBuilder buffer = new StringBuilder();
		for (int i = s.length() - 1; i >= 0; --i) {
			buffer.append(s.charAt(i));
		}
		return buffer.toString();
	}

	private static String reverse2(String s) {
		if (s.length() == 0) {
			return "";
		}
		return s.charAt(s.length() - 1) + reverse2(s.substring(0, s.length() - 1));
	}

}
