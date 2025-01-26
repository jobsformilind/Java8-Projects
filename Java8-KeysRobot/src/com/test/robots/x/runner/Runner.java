package com.test.robots.x.runner;

public abstract class Runner {
	private int order;
	private String name;

	public abstract void run() throws Exception;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
