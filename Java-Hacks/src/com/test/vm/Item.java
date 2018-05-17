package com.test.vm;

public class Item {
	double price;
	String name;

	public Item(String name, double price) {
		this.name = name;
		this.price = price;
	}

	public double getPrice() {
		return price;
	}
}
