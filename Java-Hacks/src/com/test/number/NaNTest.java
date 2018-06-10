package com.test.number;

public class NaNTest {
	public static void main(String[] args) {
		double dNumber = 1;
		System.out.println(dNumber + " is NaN: " + Double.isNaN(dNumber));

		double dNun = Double.NaN;
		System.out.println(dNun + " is NaN: " + Double.isNaN(dNun));
		System.out.println(dNun + " is NaN: " + Double.isNaN(dNun));
		System.out.println(dNun + " is NaN: " + Double.isNaN(dNun));
	}
}
