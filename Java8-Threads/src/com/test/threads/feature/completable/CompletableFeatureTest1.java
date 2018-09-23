package com.test.threads.feature.completable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.test.threads.utils.Utils;

public class CompletableFeatureTest1 {
	private static ExecutorService executor = Executors.newCachedThreadPool();
	public static void main(String[] args) throws Exception {
		CompletableFuture<String> feature = calculateAsync();
		System.out.println(feature.get());
		Utils.shutdownAndAwaitTermination(executor);
	}

	private static CompletableFuture<String> calculateAsync() {
		CompletableFuture<String> feature = new CompletableFuture<>();
		Executors.newCachedThreadPool().submit(() -> {
			Utils.sleepSilently(20_000);
			feature.complete("Hello");
		});
		return feature;
	}

}
