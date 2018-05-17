package com.test.vm;

import java.util.HashMap;
import java.util.Map;

public class VMCache {
	public static Map<String, Integer> getChange(double change) {
		Map<String, Integer> changeCache = new HashMap<String, Integer>();
		double changeAmt = change * 100;
		int dollars = (int)changeAmt/100;
		int remainder = (int)changeAmt%100;
		int pennies = (int) remainder/10;
		int cents = (int) remainder%10;

		getCache();
		
		return changeCache;
	}

	private static int getDollars(double change) {
		int dollars = 0;
		while(change > 0) {
			dollars++;
		}
		return dollars;
	}

	private static Map<String, Integer> getCache() {
		Map<String, Integer> cache = new HashMap<String, Integer>();
		cache.put("1P", 20);
		cache.put("5P", 20);
		cache.put("10P", 20);
		cache.put("1$", 20);
		return cache;
	}
}
