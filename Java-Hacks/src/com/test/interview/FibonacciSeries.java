package com.test.interview;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FibonacciSeries {
	public static void main(String[] args) {
		int upto = 10;
		printFibonacci1(upto);
		printFibonacci2(upto);
		printFibonacci3(upto);
	}

	private static void printFibonacci1(int upto) {
		List<Long> list = new ArrayList<Long>();
		for (int cnt = 0; cnt < upto; cnt++) {
			list.add(fibonacci1(cnt));
		}
		System.out.println(list);
	}
	private static void printFibonacci2(int upto) {		
		List<Long> list = new ArrayList<Long>();
		for (int cnt = 0; cnt < upto; cnt++) {
			list.add(fibonacci2(cnt));
		}
		System.out.println(list);
	}

	private static long fibonacci1(long n) {
		if (n == 0) {
			return 0;
		} else if (n == 1) {
			return 1;
		}
		return fibonacci1(n - 1) + fibonacci1(n - 2);
	}

	public static long fibonacci2(long n) {
		return n <= 0 ? 0 : n == 1 ? 1 : fibonacci2(n - 1) + fibonacci2(n - 2);
	}

	public static void printFibonacci3(int series) {
		List<Integer> list = Stream.iterate(new int[]{0, 1}, s -> new int[]{s[1], s[0] + s[1]})
                .limit(series)
                .map(n -> n[0])
                .collect(Collectors.toList());
        System.out.println(list);
    }
}
