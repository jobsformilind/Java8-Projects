package com.test.performance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PerformanceTester {
	private static List<Integer> permStore = new ArrayList<>();

	public static void main(String[] args) {
		PerformanceTester tester = new PerformanceTester();
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(() -> tester.test(), 1, 1, TimeUnit.SECONDS);
	}

	public void test() {
		slowLeakingMethod(1_000, 30);
		notLeakingMethod(50_000);
		System.out.println("Test is executed on : " + new Timestamp(System.currentTimeMillis()) + ", Size: " +  permStore.size());
	}

	private void slowLeakingMethod(int addCounter, int removeCounter) {
		for (int i = 0; i < addCounter; i++) {
			permStore.add(i);
		}
		for (int i = removeCounter - 1; i > 0; i--) {
			permStore.remove(i);
		}
	}

	private void notLeakingMethod(int counter) {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < counter; i++) {
			list.add(i);
		}
	}
}