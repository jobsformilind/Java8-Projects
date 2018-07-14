package com.test.java8.functionalinterfaces;

import java.util.function.Supplier;

public class SupplierTest {

	public static void main(String[] args) {
		Supplier<String> s = () -> Long.toString(System.nanoTime());
		System.out.println(s.get());
		System.out.println(s.get());
		System.out.println(s.get());
		System.out.println(s.get());
	}

}


//package java.util.function
//@FunctionalInterface
interface SUpplier<R> {
	R get();
}