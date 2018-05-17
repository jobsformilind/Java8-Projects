package com.test.threads.latch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoundownLatch {
	public static void main(String[] args) throws Exception {
		CountDownLatch latch = new CountDownLatch(3);
		ExecutorService executor = Executors.newFixedThreadPool(3);
		for (int i = 1; i <= 3; i++) {
			executor.submit(new Processor(i, latch));
		}
		latch.await();
		executor.shutdown();
		System.out.println("Finished out..");
	}
}

class Processor implements Runnable {
	private CountDownLatch latch;
	private int counter;

	public Processor(int counter, CountDownLatch latch) {
		this.counter = counter;
		this.latch = latch;
	}

	public void run() {
		System.out.println("Started : " + counter);
		try {
			Thread.sleep(counter * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done : " + counter);
		latch.countDown();

	}
}