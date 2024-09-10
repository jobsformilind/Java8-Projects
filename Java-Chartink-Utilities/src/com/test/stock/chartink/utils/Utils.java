package com.test.stock.chartink.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.test.stock.chartink.pojo.Enums;
import com.test.stock.chartink.pojo.LinkData;
import com.test.stock.chartink.pojo.Stock;

public class Utils implements Constants {

	public static void appendToFile(String fileName, String data) throws Exception {
		Files.write(Paths.get(fileName), data.getBytes(), StandardOpenOption.APPEND);
	}

	public static void copyFile(File soureFile, File targetFile) {
		try {
			Path sourePath = soureFile.toPath();
			Path targetPath = targetFile.toPath();
			System.out.print("SourePath=" + sourePath.getFileName());
			System.out.println(" -> TargetPath=" + targetPath.getFileName());
			Files.copy(sourePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String defaultIfEmpty(String st, String defaultStr) {
		String retValue = isEmpty(st) ? defaultStr : st.trim();
		if ("None".equalsIgnoreCase(retValue)) {
			retValue = "0";
		}
		return retValue;
	}

	public static String ensureDirectory(String parentDirectory, String directoryName) {
		File dir = null;
		if (parentDirectory != null && directoryName != null) {
			dir = new File(parentDirectory, directoryName);
		} else if (directoryName != null) {
			dir = new File(directoryName);
		}
		if (dir != null) {
			dir.mkdirs();
			return dir.getAbsolutePath();
		}
		return null;
	}

	public static String ensureFile(String fileName) {
		try {
			return ensureFile(null, fileName);
		} catch (Exception e) {
			//Ignore
		}
		return null;
	}

	public static String ensureFile(String parentDirectory, String fileName) throws Exception {
		File file = null;
		if (parentDirectory != null && fileName != null) {
			file = new File(parentDirectory, fileName);
		} else if (fileName != null) {
			file = new File(fileName);
		}
		if (file != null && !file.exists()) {
			file.createNewFile();
			return file.getAbsolutePath();
		}
		return null;
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

	public static List<String> getDataFromFile(String dataFile) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(dataFile))) {
			return stream.filter(Objects::nonNull).map(s -> s.trim()).filter(Utils::notCommented).filter(Utils::isNotEmpty)
					.collect(Collectors.toList());
		}
	}

	public static String getFileName(String fileName, String extn) {
		if (fileName != null && fileName.indexOf(extn) > 0) {
			int start = fileName.indexOf(extn);
			fileName = fileName.substring(0, start);
		}
		return fileName;
	}

	public static String getFullDataFromFile(String dataFile) throws IOException {
		StringBuffer buff = new StringBuffer();
		try (Stream<String> stream = Files.lines(Paths.get(dataFile))) {
			stream.filter(Objects::nonNull).map(s -> s.trim()).filter(Utils::isNotEmpty).forEach(buff::append);
		}
		return buff.toString();
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

	public static int indexOfIncludingString(String data, String str) {
		if (str != null && data != null && data.indexOf(str) > 0) {
			return data.indexOf(str) + str.length();
		}
		return 0;
	}

	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isEmpty(String st) {
		if (st == null)
			return true;
		if (st.trim().length() == 0)
			return true;
		return false;
	}

	public static boolean isNotDouble(String str) {
		return !isDouble(str);
	}

	public static boolean isNotEmpty(String st) {
		return !isEmpty(st);
	}

	public static double minDouble(double... dob) {
		try {
			return Arrays.stream(dob).filter(d -> d > 0).min().orElse(0);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static boolean notCommented(String st) {
		return !st.startsWith("#");
	}

	public static void printUnprocessedMap(Map<String, String> unprocessedMap) {
		unprocessedMap.entrySet().stream().forEach(e -> {
			System.out.println("*** Not Processesd: " + e);
		});
	}

	public static Map<String, String> readData(String dataFile) throws Exception {
		Map<String, String> data = new HashMap<>();
		Stream<String> stream = Files.lines(Paths.get(dataFile));
		stream.filter(Objects::nonNull).filter(Utils::notCommented).forEach(line -> {
			if (line.indexOf("=") > 0) {
				String[] split = line.split("=");
				data.put(Utils.trimToEmpty(split[0]), Utils.trimToEmpty(split[1]));
			} else if (line.indexOf(",") > 0) {
				String[] split = line.split(",");
				data.put(Utils.trimToEmpty(split[0]), Utils.trimToEmpty(split[1]));
			}
		});
		stream.close();
		return data;
	}

	public static List<LinkData> toLinkData(List<String> propertyList) throws Exception {
		List<LinkData> data = new ArrayList<>();
		propertyList.stream().filter(Objects::nonNull).filter(Utils::notCommented).forEach(line -> {
			int start = line.indexOf("=");
			if (start > 0) {
				String key = Utils.trimToEmpty(line.substring(0, start));
				String value = Utils.trimToEmpty(line.substring(start + 1));
				LinkData linkData = new LinkData(key, value);
				data.add(linkData);
				ensureFile(linkData.getDataFile());
			}
		});
		return data;
	}

	public static void validateWithExistingLinkData(List<LinkData> linkDataList) throws Exception {
		linkDataList.stream().filter(Objects::nonNull).forEach(data -> {
			try (Stream<String> stream = Files.lines(Paths.get(data.getDataFile()))) {
				stream.filter(Objects::nonNull).filter(Utils::notCommented).forEach(line -> {
					Stock stock = data.getStock(line);
					if (stock == null) {
						stock = new Stock(line, line, line);
						stock.setStatus(Enums.Status.REMOVE);
						data.addStock(stock);
					} else {
						stock.setStatus(Enums.Status.KEEP);
					}
				});
				data.getStocks().stream().forEach(s -> {
					if (s.getStatus() == null) {
						s.setStatus(Enums.Status.NEW);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static List<File> readFilesList(String directory, String extn) throws Exception {
		return Files.walk(Paths.get(directory), 1).filter(Files::isRegularFile).map(Path::toFile)
				.filter(f -> f.getName().endsWith(extn)).collect(Collectors.toList());
	}

	public static Map<String, File> readFilesMap(String directory, String extn) throws Exception {
		Map<String, File> filesMap = new HashMap<>();
		List<File> filesList = Files.walk(Paths.get(directory), 1).filter(Files::isRegularFile).map(Path::toFile)
				.filter(f -> f.getName().endsWith(extn)).collect(Collectors.toList());
		for (File file : filesList) {
			filesMap.put(Utils.getFileName(file.getName(), extn), file);
		}
		return filesMap;
	}

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

	public static void sleepSilentlyMinutes(int minutes) {
		try {
			Thread.sleep(minutes*60000);
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

	public static String substring(String str, int length) {
		try {
			if (str != null) {
				int length2 = str.length();
				if (length2 > length) {
					str = str.substring(0, length);
				}
			}
		} catch (Exception e) {
		}
		return str;
	}

	public static double toDouble(String str) {
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static int toInt(String st) {
		try {
			return Integer.parseInt(st);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	public static String toIntDefault(String st) {
		return isEmpty(st) ? "0" : st.trim();
	}

	public static int toIntOrDefault(String st, int def) {
		try {
			return Integer.parseInt(st);
		} catch (NumberFormatException e) {
		}
		return def;
	}

	public static String trimToEmpty(String st) {
		return defaultIfEmpty(st, "");
	}

	public static String trimToNull(String st) {
		return defaultIfEmpty(st, null);
	}

	public static void writeFile(String fileName, String data) {
		try {
			Files.write(Paths.get(fileName), data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void log(String data, boolean saveLogs) {
		try {
			System.out.println(data);
			if(saveLogs) {
				data += "\n";
				Files.write(Paths.get(logFileName), data.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
