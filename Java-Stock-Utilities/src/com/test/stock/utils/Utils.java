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
		if (isNotEmpty(command) && isNotEmpty(startsWith)) {
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

	public static String trimToNull(String st) {
		return defaultIfEmpty(st, null);
	}

	public static String trimToEmpty(String st) {
		return defaultIfEmpty(st, "");
	}

	public static String defaultIfEmpty(String st, String defaultStr) {
		String retValue = isEmpty(st) ? defaultStr : st.trim();
		if ("None".equalsIgnoreCase(retValue)) {
			retValue = "0";
		}
		return retValue;
	}

	public static int indexOfIncludingString(String data, String str) {
		if (str != null && data != null && data.indexOf(str) > 0) {
			return data.indexOf(str) + str.length();
		}
		return 0;
	}

	public static void writeFile(String fileName, String data) throws Exception {
		Files.write(Paths.get(fileName), data.getBytes());
	}

	public static void appendToFile(String fileName, String data) throws Exception {
		Files.write(Paths.get(fileName), data.getBytes(), StandardOpenOption.APPEND);
	}

	public static List<String> getDataFromFile(String dataFile) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(dataFile))) {
			return stream.filter(Objects::nonNull).map(s -> s.trim()).filter(Utils::isNotEmpty)
					.collect(Collectors.toList());
		}
	}

	public static String toIntDefault(String st) {
		return isEmpty(st) ? "0" : st.trim();
	}

	public static int toInt(String st) {
		try {
			return Integer.parseInt(st);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	public static int toIntOrDefault(String st, int def) {
		try {
			return Integer.parseInt(st);
		} catch (NumberFormatException e) {
		}
		return def;
	}

	public static boolean isNotDouble(String str) {
		return !isDouble(str);
	}

	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String getRequiredSystemProperty(String propertyName) {
		String property = System.getProperty(propertyName);
		if (isEmpty(property)) {
			property = System.getenv(propertyName);
			if (isEmpty(property)) {
				System.out.println("Please set system/env property: " + propertyName);
				System.exit(0);
			}
		}
		return property;
	}

	public static String getStocksHomeDir() {
		return Utils.getRequiredSystemProperty("MY_STOCKS_HOME") + "\\";
	}

	public static int extractNumber(String data) {
		int pages = 1;
		if (isNotEmpty(data)) {
			char[] charArray = data.toCharArray();
			String num = "";
			for (int i = 0; i < charArray.length; i++) {
				if (Character.isDigit(charArray[i])) {
					num = num + Character.toString(charArray[i]);
				}
			}
			pages = toIntOrDefault(num, 1);
		}
		return pages;
	}
}
