package com.test.interview;

import java.util.stream.IntStream;

public class Factorial {
	public static void main(String[] args) {

	}

	private static int factorial1(int n) {
		if (n == 0)
			return 1;
		else
			return (n * factorial1(n - 1));
	}

	private static int factorial2(int n) {
		return IntStream.rangeClosed(2, n).reduce(1, (a, b) -> a * b);
	}

}
