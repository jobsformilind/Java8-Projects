package com.test.stock.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

	public static void shutdownAndAwaitTermination(ExecutorService pool) {
		if (pool != null) {
			pool.shutdown(); // Disable new tasks from being submitted

			try {
				// Wait a while for existing tasks to terminate
				if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
					pool.shutdownNow(); // Cancel currently executing tasks
					// Wait a while for tasks to respond to being cancelled
					if (!pool.awaitTermination(60, TimeUnit.SECONDS))
						System.err.println("Pool did not terminate");
				}
			} catch (InterruptedException ie) {
				// (Re-)Cancel if current thread also interrupted
				pool.shutdownNow();
				// Preserve interrupt status
				Thread.currentThread().interrupt();
			}
		}
	}

	public static void sleepSilently(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			System.out.println("***ERROROO::: Got exception during sleep...");
		}
	}

	public static boolean startsWith(String command, String startsWith) {
		if(isNotEmpty(command) && isNotEmpty(startsWith)) {
			String upperCase = startsWith.toUpperCase();
			return command.startsWith(startsWith) || command.startsWith(upperCase);
		}
		return false;
	}

	public static boolean isNotEmpty(String st) {
		return !isEmpty(st);
	}

	public static boolean isEmpty(String st) {
		if (st == null)
			return true;
		if (st.trim().length() == 0)
			return true;
		return false;
	}

	public static void writeFile(String fileName, String data) throws Exception {
		Files.write(Paths.get(fileName), data.getBytes());
	}

	public static void appendToFile(String fileName, String data) throws Exception {
		Files.write(Paths.get(fileName), data.getBytes(), StandardOpenOption.APPEND);
	}

	public static List<String> getDataFromFile(String dataFile) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(dataFile))) {
			return stream.filter(Objects::nonNull).map(s->s.trim()).filter(Utils::isNotEmpty).collect(Collectors.toList());
		}
	}

	public static int toInt(String st) {
		try {
			return Integer.parseInt(st);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

}
