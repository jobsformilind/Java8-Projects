package com.test.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class ReferenceTest {
	public static void main(String[] args) throws Exception {
		//testWeakReference();
		//testSoftReference();
		testPhantomReference();
		Thread.sleep(10000);
	}

	private static void testWeakReference() {
		Asteroid asteroid = new Asteroid("Ceres", 238.0d);
		WeakReference<Asteroid> reference = new WeakReference<Asteroid>(asteroid);
		System.out.println("Before GC 1: " + reference.get());
		System.gc();
		System.out.println("After GC 2: " + reference.get());
		asteroid = null;
		System.gc();
		System.out.println("After Null GC 3: " + reference.get());
	}

	private static void testSoftReference() {
		Asteroid asteroid = new Asteroid("Ceres", 238.0d);
		SoftReference<Asteroid> reference = new SoftReference<Asteroid>(asteroid);
		System.out.println("Before GC 1: " + reference.get());
		System.gc();
		System.out.println("After GC 2: " + reference.get());
		asteroid = null;
		System.gc();
		System.out.println("After Null GC 3: " + reference.get());
	}

	private static void testPhantomReference() {
		Asteroid asteroid = new Asteroid("Ceres", 238.0d);
		ReferenceQueue<Asteroid> queue = new ReferenceQueue<>();
		PhantomReference<Asteroid> reference = new PhantomReference<Asteroid>(asteroid, queue);
		System.out.println("Before GC 1: " + queue);
		System.gc();
		System.out.println("After GC 2: " + queue);
		asteroid = null;
		System.gc();
		System.out.println("After Null GC 3: " + queue);
	}

}
