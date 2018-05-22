package com.test.interview;

public class PrimeNumber {

	public static void main(String[] args) {
		int[] arr = {};
		System.out.println(getLowestPrime(arr));
	}

	public static int getLowestPrime(int[] someNumbers) {
		int lowest = 0;
		if (someNumbers != null) {
			for (int i : someNumbers) {
				if (isPrime(i)) {
					if (lowest == 0 || lowest > i) {
						lowest = i;
					}
				}
			}
		}
		return lowest;
	}

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
