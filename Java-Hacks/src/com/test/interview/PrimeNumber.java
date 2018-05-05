package com.test.interview;

public class PrimeNumber {

	public static boolean isPrime1(int n) {
		if (n <= 1) {
			return false;
		}
		int m = n / 2;
		for (int i = 2; i <= m; i++) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;
	}

	public static boolean isPrime(int n) {
		if (n <= 1) {
			return false;
		}
		double sqrt = Math.sqrt(n);
		for (int i = 2; i <= sqrt; i++) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;
	}
}
