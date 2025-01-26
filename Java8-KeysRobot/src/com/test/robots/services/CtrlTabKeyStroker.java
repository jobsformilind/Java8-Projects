package com.test.robots.services;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import com.test.robots.x.runner.Runner;

public class CtrlTabKeyStroker extends Runner {
	private static int counter = 0;
	private static int maxCounter = 50;
	private static int startDelay = 5000;
	private static int middleDelay = 2000;
	
	public static void main(String[] args) throws Exception {
		new CtrlTabKeyStroker().run();
	}

	private static void startCtrlTabKeyStroker(Robot robot) throws Exception {
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(middleDelay);
	}

	public void run() throws Exception {
		System.out.println("Starting CtrlTabKeyStroker...");
		Robot robot = new Robot();
		robot.delay(startDelay);
		while (counter < maxCounter) {
			startCtrlTabKeyStroker(robot);
			System.out.println("Done: " + counter);
			counter++;
		}
		System.out.println("CtrlTabKeyStroker done...");
	}
}
