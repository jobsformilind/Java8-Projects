package com.test.stock.screener.main.base;

public abstract class AbstractScreener {
	protected String name;

	public abstract void run() throws Exception;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
