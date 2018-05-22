package com.test.interview;

public class InitOrderStaticDefaultBlocks {
	public InitOrderStaticDefaultBlocks() {
		System.out.println("Parent Constructor");
	}
	static {
		System.out.println("Parent static block 2");
	}
	static {
		System.out.println("Parent static block 1");
	}
	{
		System.out.println("Parent initialisation  block 1");
	}
	{
		System.out.println("Parent initialisation  block 2");
	}
	public static void main(String[] args) {
		new Child();
	}
}

class Child extends InitOrderStaticDefaultBlocks {
	{
		System.out.println("Child initialisation block 2");
	}	{
		System.out.println("Child initialisation block 1");
	}
	static {
		System.out.println("Child static block 1");
	}
	static {
		System.out.println("Child static block 2");
	}
	public Child() {
		System.out.println("Child Constructor");
	}

}
