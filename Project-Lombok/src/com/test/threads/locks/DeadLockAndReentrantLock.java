package com.test.threads.locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLockAndReentrantLock {

	public static void main(String[] args) throws InterruptedException {
		Object obj1 = new Object();
		Object obj2 = new Object();
		Object obj3 = new Object();
		Lock lock = new ReentrantLock();

		Thread t1 = new Thread(new SyncThread(obj1, obj2, lock), "t1");
		Thread t2 = new Thread(new SyncThread(obj2, obj3, lock), "t2");
		Thread t3 = new Thread(new SyncThread(obj3, obj1, lock), "t3");

		t1.start();
		Thread.sleep(5000);
		t2.start();
		Thread.sleep(5000);
		t3.start();

	}

}

class SyncThread implements Runnable {
	private Object obj1;
	private Object obj2;
	private Lock lock;

	public SyncThread(Object o1, Object o2, Lock lock) {
		this.obj1 = o1;
		this.obj2 = o2;
		this.lock = lock;
	}

	// This is with reentrant lock scenario
	public void run() {
		String name = Thread.currentThread().getName();
		if (lock.tryLock()) {
			System.out.println("acquiring lock : " + name);
			work();
			work();
			System.out.println("Releasing lock : " + name);
			lock.unlock();
		}
	}

	// This is deadlock scenario
	public void run1() {
		String name = Thread.currentThread().getName();
		System.out.println(name + " starting.");
		System.out.println(name + " acquiring lock on " + obj1);
		synchronized (obj1) {
			System.out.println(name + " acquired lock on " + obj1);
			work();
			System.out.println(name + " acquiring lock on " + obj2);
			synchronized (obj2) {
				System.out.println(name + " acquired lock on " + obj2);
				work();
			}
			System.out.println(name + " released lock on " + obj2);
		}
		System.out.println(name + " released lock on " + obj1);
		System.out.println(name + " finished execution.");

	}

	private void work() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}