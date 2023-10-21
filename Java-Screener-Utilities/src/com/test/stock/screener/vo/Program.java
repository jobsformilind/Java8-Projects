package com.test.stock.screener.vo;

import com.test.stock.screener.main.base.AbstractScreener;

public class Program {
	int number;
	String name;
	AbstractScreener screener;

	public Program(int number, String name, AbstractScreener screener) {
		this.number = number;
		this.name = name;
		this.screener = screener;
	}

	public int getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}


	@Override
	public String toString() {
		return number + ". " + name;
	}

	public AbstractScreener getScreener() {
		return screener;
	}

}
