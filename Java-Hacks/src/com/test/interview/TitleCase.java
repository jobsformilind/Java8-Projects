package com.test.interview;

public class TitleCase {
	public static void main(String[] args) {
		System.out.println(toTitleCase("this Is mY String"));
		System.out.println(toTitleCase("this Is i'mY String"));
	}

	public static String toTitleCase(String input) {
		StringBuilder titleCase = new StringBuilder();
		boolean nextTitleCase = true;
		char[] charArray = input.toLowerCase().toCharArray();

		for (char c : charArray) {
			if (!Character.isLetter(c)) {
				nextTitleCase = true;
			} else if (nextTitleCase) {
				c = Character.toTitleCase(c);
				nextTitleCase = false;
			}
			titleCase.append(c);
		}
		return titleCase.toString();
	}

}
