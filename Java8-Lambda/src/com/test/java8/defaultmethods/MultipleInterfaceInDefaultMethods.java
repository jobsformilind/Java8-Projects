package com.test.java8.defaultmethods;

public class MultipleInterfaceInDefaultMethods implements Interface1, Interface2 {

	@Override
	public void method2() {
		System.out.println("Inside TestDefaultMethod.method2");
	}

	@Override
	public void method1(String str) {
		System.out.println("Inside TestDefaultMethod.method1");
	}

	@Override
	public void log(String str) {
		Interface2.super.log(str);
		System.out.println("Inside TestDefaultMethod.log");
		Interface1.print("abc");
	}
}

@FunctionalInterface
interface Interface1 {
	void method1(String str);

	default void log(String str) {
		System.out.println("Inside Interface1.log : str=" + str);
	}

	static void print(String str) {
		System.out.println("Inside Interface1.print : str=" + str);
	}

	// trying to override Object method gives compile time error as
	// "A default method cannot override a method from java.lang.Object"

	// default String toString(){
	// return "i1";
	// }
}

@FunctionalInterface
interface Interface2 {
	void method2();

	default void log(String str) {
		System.out.println("Inside Interface2.log : str=" + str);
	}

	static void print(String str) {
		System.out.println("Inside Interface2.print : str=" + str);
	}
}