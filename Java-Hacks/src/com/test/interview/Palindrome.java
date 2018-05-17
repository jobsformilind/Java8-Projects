package com.test.interview;

public class Palindrome {

	public static void main(String[] args) {
		System.out.println("hello : " + istPalindrome("hello".toCharArray()));
		System.out.println("andna : " + istPalindrome("andna".toCharArray()));
		System.out.println("a : " + istPalindrome("a".toCharArray()));
		System.out.println(" : " + istPalindrome("".toCharArray()));
	}

	public static boolean istPalindrome(char[] word) {
		int i1 = 0;
		int i2 = word.length - 1;
		while (i2 > i1) {
			if (word[i1] != word[i2]) {
				return false;
			}
			++i1;
			--i2;
		}
		return true;
	}

	public boolean isPa(char[] chars) {
		int start = 0;
		int end = chars.length - 1;
		while (start < end) {
			if (chars[start] != chars[end]) {
				return false;
			}
			++start;
			--end;
		}
		return true;
	}

	public boolean isPalindrome(String input) {
		char[] inputChars = input.toCharArray();
		int inputLength = inputChars.length;
		int inputMid = inputLength / 2;

		for (int i = 0; i <= inputMid; i++) {
			if (inputChars[i] != inputChars[inputLength - i - 1]) {
				return false;
			}
		}
		return true;
	}

}
