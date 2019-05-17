package com.test.threads.forkjoin.folder;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;

public class FolderForkJoinTestClass {
	private static ForkJoinPool commonPool = ForkJoinPool.commonPool();
	public static ForkJoinPool forkJoinPool = new ForkJoinPool(10);
	public static AtomicLong filesScanned = new AtomicLong(); 
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		FolderProcessor temp = new FolderProcessor("C:\\temp", "txt");
		forkJoinPool.submit(temp);
		List<String> data = temp.join();
		System.out.println("Files Size: " + data.size());
		if (data.size() <= 50) {
			data.forEach(System.out::println);
		}
		System.out.println("Total files canned: " + filesScanned.get());
		System.out.println("***Time taken: " + (System.currentTimeMillis()-start) + "ms");
	}
}
