package com.test.interview;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringReverser {
	public static void main(String[] args) {
		String myString = "This is dummy string";
		System.out.println("Original: " + myString);
		System.out.println("Reversed1: " + reverseSentence(myString, StringReverser::reverse1));
		System.out.println("Reversed2: " + reverseSentence(myString, StringReverser::reverse2));
		System.out.println("Reversed3: " + reverseSentence(myString, StringReverser::reverse3));
	}

	private static String reverseSentence(String sentence, Function<String, String> mapper) {
		return Optional.ofNullable(sentence).map(s -> {
			String[] strArray = s.split(" ");
			return Arrays.stream(strArray).map(mapper).collect(Collectors.joining(" "));
		}).orElse(null);
	}

	private static String reverse1(String word) {
		return Optional.ofNullable(word).map(w -> {
			int length = w.length() - 1;
			char[] chars = w.toCharArray();
			char[] revWord = new char[length + 1];
			for (int i = 0; i < w.length(); i++) {
				revWord[i] = chars[length--];
			}
			return String.valueOf(revWord);
		}).orElse(null);
	}

	public static String reverse2(String word) {
		char ch[] = word.toCharArray();
		String rev = "";
		for (int i = ch.length - 1; i >= 0; i--) {
			rev += ch[i];
		}
		return rev;
	}

	public static String reverse3(String word) {
		StringBuilder builder = new StringBuilder();
		char ch[] = word.toCharArray();
		for (int i = ch.length - 1; i >= 0; i--) {
			builder.append(ch[i]);
		}
		return builder.toString();
	}
}
