package com.test.stock.utils;

public class Counter {
	int total = 0;
	int current = 0;
	static Counter counter;

	public static Counter getCounter() {
		if(counter == null) {
			counter = new Counter(1);
		}
		return counter;
	}

	public static void initCounter(int total) {
		counter = new Counter(total);
	}

	public Counter(int total) {
		this.total = total;
	}

	public int currentIncrease() {
		return current++;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(current).append("/").append(total);
		return builder.toString();
	}

}
