package com.test.threads.forkjoin.user;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserForkJoinTestClass {
	public static final int THRESHOLD = 200;
	private static final String alphabet = "ABCDEFGHIJK123456789LMNOPQRSTUVWXYZ";
	private static ForkJoinPool commonPool = ForkJoinPool.commonPool();
	public static ForkJoinPool forkJoinPool = new ForkJoinPool(10);
	private static Random random = new Random();


	public static void main(String[] args) {
		List<User> users = getDataList(10000, 5);
		long start = System.currentTimeMillis();
		System.out.println(getCurrentThreadName() + "Users to process: " + users.size());
		UserRecursiveTask task = new UserRecursiveTask(users);
		forkJoinPool.submit(task);
		List<User> processedUsers = task.join();
		System.out.println(getCurrentThreadName() + "Users processed: " + processedUsers.size());
		System.out.println(getCurrentThreadName() + "Users processed time taken: " + (System.currentTimeMillis()-start) + "ms");
	}

	public static List<User> getDataList(int counter, int length) {
		List<String> data = IntStream.rangeClosed(1, counter).parallel().mapToObj(i -> generateString(length)).distinct().collect(Collectors.toList());
		return data.parallelStream().map(s -> new User(s)).collect(Collectors.toList());
	}

	public static String generateString(int length) {
		return IntStream.rangeClosed(1, length).parallel().mapToObj(i -> alphabet.charAt(random.nextInt(alphabet.length()))).map(c -> (length + c.toString())).collect(Collectors.joining());
	}

	public static String getCurrentThreadName() {
		return Thread.currentThread().getName() + ": ";		
	}
}
