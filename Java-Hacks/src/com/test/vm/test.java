package com.test.vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {

	public static void main(String[] args) {
		System.out.println(reformatDate("20th Oct 2052"));
		System.out.println(reformatDate("3rd Sept 1948"));

		int[] a = new int[] { 6, 7, 9, 5, 6, 3, 2 };
		System.out.println(maxDifference(a));
	}

	static int maxDifference(int[] a) {
		int diff = 0;
		for (int i = 0; i < a.length - 1; i=i+2) {
			int max = Math.max(a[i], a[i+1]);
			int min = Math.min(a[i], a[i+1]);
			if (diff < (max - min)) {
				diff = max - min;
			}
		}
		return diff;
	}

	static int maxDifference1(int[] a) {
		Arrays.sort(a);
		if (a.length == 1) {
			return 0;
		}
		int max = a[a.length - 1];
		int min = a[0];
		System.out.println("max " + max);
		System.out.println("min " + min);
		return max - min;
	}

	static void reformatDate(String[] dates) {
		List<String> dates1 = new ArrayList<String>();
		List<String> list = Arrays.asList(dates);
		for (String st : list) {
			dates1.add(reformatDate(st));
		}
		dates1.toArray(new String[dates1.size()]);
	}

	static String reformatDate(String dates) {
		String[] arr = dates.split(" ");
		String day = getDay(arr[0]);
		String mon = getMonth(arr[1]);
		String year = arr[2];
		return year + "-" + mon + "-" + day;
	}

	static enum MONTHS {
		Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sept, Oct, Nov, Dec
	};

	static String getMonth(String monthString) {
		MONTHS mon = MONTHS.valueOf(monthString);
		return "" + mon.ordinal() + 1;
	}

	static String getDay(String dayString) {
		dayString = dayString.replaceAll("st", "");
		dayString = dayString.replaceAll("nd", "");
		dayString = dayString.replaceAll("rd", "");
		dayString = dayString.replaceAll("th", "");
		if (dayString.length() == 1) {

		}
		return dayString;
	}
}
