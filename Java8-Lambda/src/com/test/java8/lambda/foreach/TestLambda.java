package com.test.java8.lambda.foreach;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestLambda {
	public static void main(String[] args) {
		testOrdinal();
	}

	private static void testOrdinal() {
		List<Holder> list = new ArrayList<Holder>();
		AtomicInteger ordinal = new AtomicInteger(0);
		list.forEach(s -> {
			s.setOrdinal(ordinal.getAndIncrement());
		});
	}
	class Holder {
		int ordinal;
		public void setOrdinal(int ordinal) {
			this.ordinal = ordinal;
		}
	}
}
