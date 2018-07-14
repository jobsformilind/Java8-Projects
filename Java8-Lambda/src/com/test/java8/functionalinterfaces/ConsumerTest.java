package com.test.java8.functionalinterfaces;

import java.util.function.Consumer;

public class ConsumerTest {
	public static void main(String[] args) {
		Consumer<String> c  = s -> System.out.println(s);
		c.accept("Hello");
	}
}

//package java.util.function
//@FunctionalInterface
interface COnsumer<T> {
	void accept(T t);
}
