package com.test.java8.lambda.variables;

public class VariableScopeTest {
	int x = 10;

	public void test1() {
		int y = 20;
		IParent parent = () -> {
			System.out.println("X : " + x);
			System.out.println("Y : " + y);

			// this is doable
			x = 20;

			// Not doable - Local variable y defined in an enclosing scope must be final or
			// effectively final
			// y = 30;
		};
		parent.function1();
	}

	public static void main(String[] args) {
		new VariableScopeTest().test1();
	}
}

interface IParent {
	public void function1();
}
