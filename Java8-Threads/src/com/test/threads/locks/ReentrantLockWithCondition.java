package com.test.threads.locks;

import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockWithCondition {
	public static void main(String[] args) throws Exception {
		Runner runner = new Runner();
		Thread t1 = new Thread(() -> { runner.first(); });
		Thread t2 = new Thread(() -> { runner.second(); });
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		System.out.println(runner.getCount());
	}
}

class Runner {
	private int counter;
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	
	public void first() {
		try {
			System.out.println("First lock");
			lock.lock();
			System.out.println("First Waiting..");
			condition.await();
			System.out.println("First Woken up");
			increment();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("FIrst Finish");
			lock.unlock();	
		}
	}
	public void second() {
		try {
			Thread.sleep(1000);
			lock.lock();
			System.out.println("Second Press return key");
			new Scanner(System.in).nextLine();
			System.out.println("Second Got return key");
			condition.signal();
			increment();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Second Finish");
			lock.unlock();	
		}
	}
	public int getCount() {
		return counter;
	}
	private void increment() {
		for (int i = 0; i < 10000; i++) {
			counter++;
		}
	}
}