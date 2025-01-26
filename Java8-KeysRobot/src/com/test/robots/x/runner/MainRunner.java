package com.test.robots.x.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainRunner {
	private static List<Runner> runnersList = new ArrayList<>();

	public static void addRunner(Runner runner) throws Exception {
		int number = runnersList.size() + 1;
		runner.setNumber(number);
		runnersList.add(runner);
	}

	public static void run(int number) throws Exception {
		Optional<Runner> runner = runnersList.stream().filter(r -> (r.getNumber() == number)).findFirst();
		if (runner.isPresent()) {
			runner.get().run();
		} else {
			System.out.println("Program runner not found for : " + number);
		}
	}
}
