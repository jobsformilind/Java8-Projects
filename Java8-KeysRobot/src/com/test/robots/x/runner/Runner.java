package com.test.robots.x.runner;

public abstract class Runner {
	private int number;
	private String name;

	public abstract void run() throws Exception;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
