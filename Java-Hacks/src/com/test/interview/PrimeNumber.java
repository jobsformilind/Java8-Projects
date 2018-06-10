package com.test.interview;

public class PrimeNumber {

	public static void main(String[] args) {
		int[] arr = {15, 17, 19, 3, 7};
		System.out.println("Lowest Prime: " + getLowestPrime(arr));
		System.out.println("SUM: " + sumOfPrimeNumbers(100));
	}

	public static long sumOfPrimeNumbers(int upto) {
		long sum = 0;
		int pivot = 1;
		for (int count = 0; pivot <= upto; count++) {
			if (isPrime(count)) {
				sum += count;
				pivot++;
			}
		}
		return sum;
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
