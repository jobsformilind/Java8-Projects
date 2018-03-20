package com.test.hackerranks;

public class AnagramSolution {
	public static void main(String[] args) {
		System.out.println("Expect true : " + isAnagram("hello", "Hello"));
		System.out.println("Expect true : " + isAnagram("Anagram", "margana"));
		System.out.println("Expect false : " + isAnagram(null, "margana"));
		System.out.println("Expect false : " + isAnagram(null, ""));
		System.out.println("Expect false : " + isAnagram(null, null));
		System.out.println("Expect true : " + isAnagram("Hi", "HI"));
	}

	static boolean isAnagram(String str1, String str2) {
		if (str1 == null || str2 == null)
			return false;
		String st1 = str1.toLowerCase();
		String st2 = str2.toLowerCase();
		if (st1.equals(st2))
			return true;
		return new StringBuffer(st1).reverse().toString().equals(st2);
	}

}
