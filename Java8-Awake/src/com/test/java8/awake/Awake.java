package com.test.java8.awake;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

public class Awake {
	public static void main(String[] args) throws Exception {
		Robot robot = new Robot();
		while (true) {
			robot.delay(50000);
			Point current = MouseInfo.getPointerInfo().getLocation();
			int x = Double.valueOf(current.getX()).intValue() + 1;
			int y = Double.valueOf(current.getY()).intValue() + 1;
			robot.mouseMove(x, y);
			System.out.print(".");
		}
	}
}