package com.test.stock.screener.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.test.stock.screener.data.Constants;
import com.test.stock.screener.data.MultiValueMap;
import com.test.stock.screener.data.Stock;
import com.test.stock.screener.main.base.AbstractScreener;

public class Utils implements Constants {
	private static Random RANDOM = new Random();
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String formatDate(Date date) {
		return formatter.format(date);
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
			log("***ERROROO::: Got exception during sleep...");
		}
	}

	public static boolean startsWith(String command, String startsWith) {
		if (isNotEmpty(command) && isNotEmpty(startsWith)) {
			String upperCase = startsWith.toUpperCase();
			return command.startsWith(startsWith) || command.startsWith(upperCase);
		}
		return false;
	}

	public static boolean notCommented(String st) {
		return !st.startsWith("#");
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

	public static String getFullDataFromFile(String dataFile) throws IOException {
		StringBuffer buff = new StringBuffer();
		try (Stream<String> stream = Files.lines(Paths.get(dataFile))) {
			stream.filter(Objects::nonNull).map(s -> s.trim()).filter(Utils::isNotEmpty).forEach(buff::append);
		}
		return buff.toString();
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

	public static double toDoubleObject(Object str) {
		return str != null ? toDouble(str.toString()) : 0;
	}

	public static double toDouble(String str) {
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static double minDouble(double... dob) {
		try {
			return Arrays.stream(dob).filter(d -> d > 0).min().orElse(0);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static String getRequiredSystemProperty(String propertyName) {
		String property = System.getProperty(propertyName);
		if (isEmpty(property)) {
			property = System.getenv(propertyName);
			if (isEmpty(property)) {
				log("Please set system/env property: " + propertyName);
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

	public static String getFileName(String fileName, String extn) {
		if (fileName != null && fileName.indexOf(extn) > 0) {
			int start = fileName.indexOf(extn);
			fileName = fileName.substring(0, start);
		}
		return fileName;
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

	public static String ensureFile(String parentDirectory, String fileName) throws Exception {
		File file = null;
		if (parentDirectory != null && fileName != null) {
			file = new File(parentDirectory, fileName);
		} else if (fileName != null) {
			file = new File(fileName);
		}
		if (file != null) {
			file.createNewFile();
			return file.getAbsolutePath();
		}
		return null;
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

	public static void printUnprocessedMap(Map<String, String> unprocessedMap) {
		unprocessedMap.entrySet().stream().forEach(e -> {
			log("*** Not Processesd: " + e);
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

	public static MultiValueMap<String, String> readDuplicateData(String dataFile) throws Exception {
		MultiValueMap<String, String> data = new MultiValueMap<>();
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

	public static void copyFile(File soureFile, File targetFile) {
		try {
			Path sourePath = soureFile.toPath();
			Path targetPath = targetFile.toPath();
			log("SourePath=" + sourePath.getFileName());
			log(" -> TargetPath=" + targetPath.getFileName());
			Files.copy(sourePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			handleException(e);
		}
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
			handleException(e);
		}
		return trimToEmpty(str);
	}

	public static void sleepRandomly() {
		sleepRandomly(10);
	}

	public static void sleepRandomly(int max) {
		try {
			int sleepTime = 1000 * RANDOM.nextInt(max);
			sleepTime = sleepTime > 0 ? sleepTime : 5000;
			log("Sleeping for {} ms", sleepTime);
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			handleException(e);
		}
	}

	public static void handleException(Exception ex) {
		ex.printStackTrace();
	}

	public static String logError(String logString, Object... args) {
		return log(LOG_ERROR + logString, args);
	}

	public static String log(Object logString) {
		return log(logString.toString(), "");
	}

	public static String log(String logString, Object... args) {
		if (logString != null) {
			logString = logString.replace("{}", "%s");
			String str = String.format(logString, args);
			System.out.println(str);
			try {
				Files.write(Paths.get(URLUtils.logFile), (str + "\n").getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				handleException(e);
			}
			return str;
		}
		return "";
	}

	public static String getCacheFileName(Stock stock) {
		return DIR_CACHE + stock.getSymbol() + ".html";
	}

	public static String getTempFileName(Stock stock) {
		return DIR_SER_CACHE + stock.getSymbol() + ".tmp";
	}

	public static String getCacheRawFileName(Stock stock) {
		return DIR_RAW_CACHE + stock.getSymbol() + "_raw.html";
	}

	public static boolean isCacheFileExists(Stock stock) {
		Path path = Paths.get(getCacheFileName(stock));
		return Files.exists(path);
	}

	public static <T> List<T> ensureList(List<T> list) {
		return list == null ? new ArrayList<>() : list;
	}

	public static <S, J> Map<S, J> toMap(Collection<J> list, Function<J, S> mapper) {
		return list.stream().collect(Collectors.toMap(mapper, Function.identity()));
	}

	public static <T> void printCollection(Collection<T> collection, String message) {
		if (!collection.isEmpty()) {
			log(message);
			collection.stream().forEach(Utils::log);
		}
	}

	public static void ensureFolder(String folderName) {
		try {
			Files.createDirectories(Paths.get(folderName));
			log("Directory present: " + Paths.get(folderName));
		} catch (Exception e) {
			Utils.handleException(e);
		}
	}

	public static void ensureFile(String fileName) {
		try {
			Path path = Paths.get(fileName);
			if (!Files.exists(path)) {
				Files.createFile(Paths.get(fileName));
			}
			log("File Path present: " + path);
		} catch (Exception e) {
			Utils.handleException(e);
		}
	}

	public static void recreateFile(String fileName) {
		try {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		} catch (Exception e) {
			Utils.handleException(e);
		}
	}

	public static String constructMedianPEURL(Stock stock) {
		StringBuffer url = new StringBuffer("");
		url.append(URL_API);
		url.append(stock.getCompanyId());
		url.append(SUFFIX_MEDIAN_PE);
		if (stock.isConsolidated()) {
			url.append(SUFFIX_CONSOLIDATED);
		}
		return url.toString();
	}

	public static String constructPriceUrl(Stock stock) {
		StringBuffer url = new StringBuffer("");
		url.append(URL_API);
		url.append(stock.getCompanyId());
		url.append(SUFFIX_PRICE);
		if (stock.isConsolidated()) {
			url.append(SUFFIX_CONSOLIDATED);
		}
		return url.toString();
	}

	public static String getName(AbstractScreener screener) {
		String retValue = "None";
		try {
			retValue = screener.getName();
		} catch (Exception e) {
			Utils.handleException(e);
		}
		return retValue;
	}
}
