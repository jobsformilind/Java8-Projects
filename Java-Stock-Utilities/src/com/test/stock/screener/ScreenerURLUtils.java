package com.test.stock.screener;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ScreenerURLUtils implements Constants {
	static Gson GSON =  new GsonBuilder().setPrettyPrinting().create();
	static Type CLAZZ = new TypeToken<ArrayList<JsonStock>>() {}.getType();
	static HashMap<String, String> searchURLMap = new HashMap<>();

	public static void loadSearchURLMap(String filePath) throws Exception {
		Map<String, String> properties = readPropertyFile(filePath);
		searchURLMap.putAll(properties);
	}

	public static String readTag(Stock stock, String tag) throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		Stream<String> stream = ScreenerURLUtils.getStockData(stock);
		stream.filter(Objects::nonNull).forEach(line -> {
			tagValue.append(readDataBetween(stock, line, tag, "=", 1));
		});
		stream.close();
		if (tagValue.length() > 0 && !Character.isDigit(tagValue.charAt(0))) {
			tagValue.deleteCharAt(0);
		}
		log(stock + ": " + tag + "=" + tagValue);
		return tagValue.toString();
	}

	public static String readDataBetween(Stock stock, String stratTag, String endTag) throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		Stream<String> stream = getStockData(stock);
		stream.forEach(line -> {
			tagValue.append(readDataBetween(stock, line, stratTag, endTag));
		});
		stream.close();
		log(stock + ": " + tagValue);
		return tagValue.toString();
	}

	public static String readDataBetweenAfterIndex(Stock stock, String preTag, String stratTag, String endTag)
			throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		Stream<String> stream = getStockData(stock);
		stream.forEach(line -> {
			if (line != null && line.indexOf(preTag) > 0) {
				int preTagEnd = line.indexOf(preTag) + preTag.length();
				String dataBetween = readDataBetween(stock, line, preTagEnd, stratTag, endTag);
				tagValue.append(readDataBetweenUsingEndTag(stock, dataBetween, stratTag, endTag));
			}
		});
		stream.close();
		log(stock + ": " + tagValue);
		return tagValue.toString();
	}

	public static String readDataFromStart(Stock stock, String stratTag, String endTag) throws Exception {
		return readDataFromStart(stock, null, stratTag, endTag);
	}

	public static String readDataFromStart(Stock stock, String indexTag, String startTag, String endTag)
			throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		try (Stream<String> stream = getStockData(stock)) {
			stream.filter(Objects::nonNull).forEach(line -> {
				int indexTagStart = ScreenerUtils.indexOfIncludingString(line, indexTag);
				String data = readDataFromStart(line, indexTagStart, startTag, endTag);
				tagValue.append(data);
			});
		}
		log(stock + ": " + startTag + ": " + tagValue);
		return tagValue.toString();
	}

	public static String readDataFromEnd(Stock stock, String startTag, String endTag) throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		try (Stream<String> stream = getStockData(stock)) {
			stream.filter(Objects::nonNull).forEach(line -> {
				String data = readDataFromEnd(line, startTag, endTag);
				tagValue.append(data);
			});
		}
		log(stock + ": " + startTag + ": " + tagValue);
		return tagValue.toString();
	}

	public static String readDataBetweenUsingEndTag(Stock stock, String stratTag, String endTag) throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		try (Stream<String> stream = getStockData(stock)) {
			stream.forEach(line -> {
				tagValue.append(readDataBetweenUsingEndTag(line, stratTag, endTag));
			});
		}
		log(stock + ": " + endTag + ": " + tagValue);
		return tagValue.toString();
	}

	public static void parseCAGR(Stock stock, String data) {
		if (data != null && data.length() > 3) {
			// 10Years:16%5Years:20%3Years:34%1Year:109%
			stock.setCagr1(ScreenerUtils.toIntDefault(readDataBetweenDoubly(stock, data, "1Year:=", "1Year:", "%")));
			stock.setCagr3(ScreenerUtils.toIntDefault(readDataBetweenDoubly(stock, data, "3Years:=", "3Years:", "%")));
			stock.setCagr5(ScreenerUtils.toIntDefault(readDataBetweenDoubly(stock, data, "5Years:=", "5Years:", "%")));
			stock.setCagr10(ScreenerUtils.toIntDefault(readDataBetweenDoubly(stock, data, "10Years:=", "10Years:", "%")));
			stock.setCagrAvg(average(stock.getCagr1(), stock.getCagr3(), stock.getCagr5(), stock.getCagr10()));
			// stock.setCagrAvg35(average35(stock.getCagr1(), stock.getCagr3(),
			// stock.getCagr5()));

		}
	}

	public static void parseROE(Stock stock, String data) {
		if (data != null && data.length() > 3) {
			// 10Years:40%5Years:38%3Years:42%LastYear:43%
			// stock.setRoe1(readDataBetweenDoubly(stock, data, "LastYear:=", "LastYear:",
			// "%"));
			// stock.setRoe3(readDataBetweenDoubly(stock, data, "3Years:=", "3Years:",
			// "%"));
			// stock.setRoe5(readDataBetweenDoubly(stock, data, "5Years:=", "5Years:",
			// "%"));
			// stock.setRoe10(readDataBetweenDoubly(stock, data, "10Years:=", "10Years:",
			// "%"));
			// stock.setRoeAvg(average(stock.getRoe1(), stock.getRoe3(), stock.getRoe5(),
			// stock.getRoe10()));
			// stock.setRoeAvg35(average35(stock.getRoe1(), stock.getRoe3(),
			// stock.getRoe5()));
		}
	}

	public static void parseProfit(Stock stock, String data) {
		if (data != null && data.length() > 3) {
			// 10Years:16%5Years:7%3Years:8%TTM:-4%
			// stock.setProfit1(readDataBetweenDoubly(stock, data, "TTM:=", "TTM:", "%"));
			// stock.setProfit3(readDataBetweenDoubly(stock, data, "3Years:=", "3Years:",
			// "%"));
			// stock.setProfit5(readDataBetweenDoubly(stock, data, "5Years:=", "5Years:",
			// "%"));
			// stock.setProfit10(readDataBetweenDoubly(stock, data, "10Years:=", "10Years:",
			// "%"));
			// stock.setProfitAvg(average(stock.getProfit1(), stock.getProfit3(),
			// stock.getProfit5(), stock.getProfit10()));
			// stock.setProfitAvg35(average35(stock.getProfit1(), stock.getProfit3(),
			// stock.getProfit5()));
		}
	}

	public static void parseSales(Stock stock, String data) {
		if (data != null && data.length() > 3) {
			// 10Years:16%5Years:7%3Years:8%TTM:-4%
			// 10Years:=16%=5Years:=17%=3Years:=12%=TTM:=-8%
			stock.setSale1(ScreenerUtils.toIntDefault(readDataBetweenDoubly(stock, data, "TTM:=", "TTM:", "%")));
			stock.setSale3(ScreenerUtils.toIntDefault(readDataBetweenDoubly(stock, data, "3Years:=", "3Years:", "%")));
			stock.setSale5(ScreenerUtils.toIntDefault(readDataBetweenDoubly(stock, data, "5Years:=", "5Years:", "%")));
			stock.setSale10(ScreenerUtils.toIntDefault(readDataBetweenDoubly(stock, data, "10Years:=", "10Years:", "%")));
			stock.setSaleAvg(average(stock.getSale1(), stock.getSale3(), stock.getSale5(), stock.getSale10()));
			// stock.setSaleAvg35(average35(stock.getSale1(), stock.getSale3(),
			// stock.getSale5()));
		}
	}

	public static String readDataBetweenDoubly(Stock stock, String data, String stratTag, String anotherstratTag,
			String endTag) {
		String retVal = readDataBetween(stock, data, stratTag, endTag, 0);
		// log("1retVal=" + retVal);
		if (ScreenerUtils.isEmpty(retVal)) {
			retVal = readDataBetween(stock, data, anotherstratTag, endTag, 0);
		}
		// log("2retVal=" + retVal);
		retVal = (retVal != null && retVal.equals("=") ? "" : retVal);
		return ScreenerUtils.trimToEmpty(retVal);
	}

	private static String readDataBetween(Stock stock, String data, String stratTag, String endTag) {
		return readDataBetween(stock, data, stratTag, endTag, 0);
	}

	private static String readDataBetween(Stock stock, String data, String stratTag, String endTag, int ignorePos) {
		String retValue = "";
		if (data != null && data.indexOf(stratTag) > -1) {
			int start = data.indexOf(stratTag) + stratTag.length() + ignorePos;
			int end = data.indexOf(endTag, start);
			retValue = (data.substring(start, end));
		}
		log(stock + ": " + stratTag + ": " + retValue);
		return ScreenerUtils.trimToEmpty(retValue);
	}

	public static String readDataBetween(String line, String stratTag, String endTag) {
		String retValue = "";
		if (line != null && line.indexOf(stratTag) > -1) {
			int start = line.indexOf(stratTag) + stratTag.length();
			int end = line.indexOf(endTag, start);
			retValue = line.substring(start, end);
		}
		log("Extracted Data: " + retValue);
		return ScreenerUtils.trimToEmpty(retValue);
	}

	private static String readDataBetween(Stock stock, String data, int startPos, String stratTag, String endTag) {
		String retValue = "";
		if (data != null && data.indexOf(stratTag) > -1) {
			int start = data.indexOf(stratTag, startPos) + stratTag.length();
			int end = data.indexOf(endTag, start);
			retValue = (data.substring(start, end));
		}
		log(stock + ": " + stratTag + ": " + retValue);
		return ScreenerUtils.trimToEmpty(retValue);
	}

	private static String readDataFromStart(String line, int indexTagStart, String stratTag, String endTag) {
		String retValue = "";
		if (line != null && line.indexOf(stratTag) > -1) {
			int start = line.indexOf(stratTag, indexTagStart);
			int end = line.indexOf(endTag, start) + endTag.length();
			retValue = line.substring(start, end);
		}
		return ScreenerUtils.trimToEmpty(retValue);
	}

	private static String readDataFromEnd(String line, String stratTag, String endTag) {
		String retValue = "";
		try {
			if (line != null && line.indexOf(endTag) > 0) {
				int end = line.indexOf(endTag) + endTag.length();
				int start = line.lastIndexOf(stratTag, end - 1);
				retValue = line.substring(start, end);
			}
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
		return ScreenerUtils.trimToEmpty(retValue);
	}

	public static String readDataBetweenUsingEndTag(String data, String stratTag, String endTag) {
		String retValue = "";
		try {
			if (data != null && data.indexOf(endTag) > -1) {
				int end = data.indexOf(endTag);
				int start = data.lastIndexOf(stratTag, end - 1);
				double ttmValue = ScreenerUtils.toDouble(data.substring(start + 1, end));

				end = start;
				start = data.lastIndexOf(stratTag, end - 1);
				double preValue = ScreenerUtils.toDouble(data.substring(start + 1, end));

				retValue = Double.valueOf(ScreenerUtils.minDouble(ttmValue, preValue)).toString();
			}
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
		log("Extracted Data: " + retValue);
		return ScreenerUtils.trimToEmpty(retValue);
	}

	private static String readDataBetweenUsingEndTag(Stock stock, String data, String stratTag, String endTag) {
		String retValue = "";
		try {
			if (data != null && data.indexOf(endTag) > -1) {
				int end = data.indexOf(endTag);
				int start = data.lastIndexOf(stratTag, end - 1);
				String ttmValue = data.substring(start + 1, end);
				if (ScreenerUtils.isNotEmpty(ttmValue)) {
					end = start;
					start = data.lastIndexOf(stratTag, end - 1);
					retValue = data.substring(start + 1, end);
					if (ScreenerUtils.isNotDouble(retValue)) {
						retValue = ttmValue;
					}
				}
				if (ScreenerUtils.isNotDouble(retValue)) {
					retValue = "0";
				}
			}
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
		return ScreenerUtils.trimToEmpty(retValue);
	}

	public static Stream<String> getStockData(Stock stock) throws Exception {
		try {
			String fileName = DIR_CACHE + stock + ".html";
			Path path = Paths.get(fileName);
			return Files.lines(path, Charset.forName("UTF-8"));
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
		return Stream.empty();
	}

	public static boolean notExists(Stock stock) {
		return !exists(stock);
	}

	public static boolean exists(Stock stock) {
		try {
			String fileName = DIR_CACHE + stock + ".html";
			return Files.exists(Paths.get(fileName));
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
		return false;
	}

	public static synchronized boolean needsUpdate(Stock stock) {
		return needsUpdate(stock, stock.getDaysToUpdate());
	}

	public static synchronized boolean needsUpdate(Stock stock, int days) {
		if (stock.isUpdate()) {
			log("Force Update stock data.." + stock);
			return true;
		}
		try {
			log("Checking needsUpdate stock data for days: " + days);
			String fileName = DIR_CACHE + stock + ".html";
			Path path = Paths.get(fileName);
			if (Files.exists(path)) {
				FileTime lastModifiedTime = Files.getLastModifiedTime(path);
				long modified = lastModifiedTime.toInstant().getEpochSecond();
				long now = Instant.now().getEpochSecond();
				long diff = now - modified;
				long day = days * 24 * 60 * 60;
				boolean needsUpdate = (day - diff) < 0;
				log("needsUpdate : " + needsUpdate);
				return needsUpdate;
			}
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
		return true;
	}

	public static void getStockHtml(Stock stock, boolean update) throws Exception {
		getStockHtml(stock, update, 5000);
	}

	public static void getStockHtml(Stock stock, boolean update, long sleep) throws Exception {
		writeHTML(stock, URL_BASE + stock, DIR_CACHE + stock + ".html", update, sleep);
	}

	public static void writeHTML(Stock stock, String url, String fileName, boolean update) throws Exception {
		writeHTML(stock, url, fileName, update, 5000);
	}

	public static synchronized void writeHTML(Stock stock, String url, String fileName, boolean update, long sleep)
			throws Exception {
		if (!update) {
			return;
		}
		boolean needsUpdate = needsUpdate(stock);
		if (needsUpdate) {
			log("Downloading data for:{ " + Counter.getCounter() + " }: " + stock);
			boolean exists = exists(stock);
			url = getUrl(stock, url);
			log("update: " + update + ", File exists: " + exists);
			String str = downlaodData(stock, url);
			writeFile(stock, fileName, str);
			log("Data downloaded for: " + stock);
			int sleepTime = 5000;
			sleepTime = sleepTime > 1 ? sleepTime : 5000;
			log("Sleeping for: " + sleepTime);
			Thread.sleep(sleepTime);
		} else {
			log("Updated data already exists for : " + stock);
		}
	}

	private static String getUrl(Stock stock, String url) {
		try {
			String searchUrl = URL_SEARCH + getStockSymbolForSearch(stock.getSymbol());
			String data = downlaodUnparsedData(searchUrl).toString();
			if (data != null && data.startsWith("[")) {
				List<JsonStock> jsonData = GSON.fromJson(data, CLAZZ);
				if (jsonData != null) {
					String symbol = "/" + stock.getSymbol() + "/";
					JsonStock jsonStock = jsonData.stream().filter(s -> (s.getUrl().indexOf(symbol) > 0)).findAny()
							.orElse(null);
					if (jsonStock == null && !jsonData.isEmpty()) {
						jsonStock = jsonData.get(0);
					}
					if (jsonStock != null) {
						url = URL_BASE + jsonStock.getUrl();
						stock.setConsolidated(jsonStock.getUrl().indexOf("consolidated") > 0);
						stock.setCompanyId(jsonStock.getId());
						stock.setName(ScreenerUtils.substring(jsonStock.getName(), 19));
					}
				}
			}
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
		return url;
	}

	public static void writeFile(Stock stock, String fileName, String str) throws Exception {
		ensureCacheFolder();
		if (ScreenerUtils.isNotEmpty(str)) {
			writeFile(DIR_RAW_CACHE + stock + ".html", str);
			str = normalizeData(str);
			if (ScreenerUtils.isNotEmpty(str)) {
				str = "name=" + stock.getName() + "=" + str;
				writeFile(fileName, str);
			}
		}
	}

	public static String downlaodData(Stock stock, String url) {
		StringBuffer buff = new StringBuffer("");
		buff.append(downlaodUnparsedData(url));
		buff.append(downlaodMedianPEData(stock));
		return buff.toString();
	}

	private static StringBuffer downlaodMedianPEData(Stock stock) {
		StringBuffer buff = new StringBuffer("");
		if (stock.getCompanyId() > 0) {
			StringBuffer medianPEURL = new StringBuffer("");
			medianPEURL.append(URL_API);
			medianPEURL.append(stock.getCompanyId());
			medianPEURL.append(SUFFIX_MEDIAN_PE);
			if (stock.isConsolidated()) {
				medianPEURL.append(SUFFIX_CONSOLIDATED);
			}
			buff.append(downlaodUnparsedData(medianPEURL.toString()));
		}
		return buff;
	}

	public static StringBuffer downlaodUnparsedData(String url) {
		StringBuffer buff = new StringBuffer("");
		try {
			log("Downloading: " + url);
			try (Scanner scanner = new Scanner(new URL(url).openStream())) {
				while (scanner.hasNextLine()) {
					buff.append(scanner.nextLine());
				}
			}
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
		return buff;
	}

	public static String normalizeData(String str) {
		str = str.replaceAll("</td><td>", "=");
		str = str.replaceAll("</td>", "=");
		str = str.replaceAll("</span>", "=");
		str = str.replaceAll("\\<[^>]*>", "");
		str = str.replaceAll("\\s+", "");
		str = str.replaceAll(",", "");
		str = str.replaceAll("\\u20b9", "=");
		str = str.replaceAll("\"", "=");
		str = str.replaceAll("==", "=");
		str = str.replaceAll("=label", "label");
		str = str.replaceAll("StockP/E", "StockPEarn");
		return str;
	}

	public static void writeFile1(String fileName, String data) throws Exception {
		Files.write(Paths.get(fileName), data.getBytes());
	}

	public static void writeFile(String fileName, String data) {
		try {
			Files.write(Paths.get(fileName), data.getBytes());
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
	}

	public static void ensureCacheFolder() {
		try {
			Files.createDirectories(Paths.get(DIR_RAW_CACHE));
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
	}

	public static Stream<String> getFromCache(String fileName) {
		try {
			return Files.lines(Paths.get(DIR_CACHE + fileName), Charset.forName("UTF-8"));
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
		return Stream.empty();
	}

	public static String extractNumber(String data) {
		if (data != null) {
			if (data.indexOf(".") > -1) {
				data = data.substring(0, data.indexOf("."));
			}
			char[] charArray = data.toCharArray();
			String temp = "";
			for (char ch : charArray) {

				if (Character.isDigit(ch) || ".".equals(Character.toString(ch))) {
					temp += ch;
				}
			}
			data = temp;
		}
		return ScreenerUtils.toIntDefault(data);
	}

	public static String extractText(String data) {
		if (data != null) {
			data = replaceAll(data);
			char[] charArray = data.toCharArray();
			String temp = "";
			for (char ch : charArray) {
				if (!Character.isAlphabetic(ch)) {
					break;
				}
				temp += ch;
			}
			data = temp;
		}

		return data;
	}

	public static String getStockSymbolForSearch(String str) {
		if (str != null) {
			String string = searchURLMap.get(str);
			if (string != null) {
				str = string;
			} else {
				str = URLEncoder.encode(str, StandardCharsets.UTF_8);
			}
		}
		return str;
	}

	public static String replaceAll(String str) {
		if (str != null) {
			str = str.replaceAll("E-Commerce", "EComm");
			str = str.replaceAll("DiamondGemsandJewellery", "Jewellery");
			str = str.replaceAll("InfrastructureDevelopers", "Infra");
			str = str.replaceAll("QuickServiceRestaurant", "QSR");
		}
		return str;
	}

	public static Set<String> readSymbols(String dataFile) throws Exception {
		Set<String> symbolsSet = new HashSet<>();
		Stream<String> stream = Files.lines(Paths.get(dataFile));
		stream.forEach(line -> {
			if (ScreenerUtils.isNotEmpty(line)) {
				symbolsSet.add(line.trim());
			}
		});
		stream.close();
		return symbolsSet;
	}

	public static int average35(String price1, String price3, String price5) {
		int avg = average("0", price3, price5, "0");
		if (avg == 0) {
			avg = ScreenerUtils.toInt(price1);
		}
		return avg;
	}

	public static int average(String price1, String price3, String price5, String price10) {
		int iprice1 = ScreenerUtils.toInt(price1);
		int iprice3 = ScreenerUtils.toInt(price3);
		int iprice5 = ScreenerUtils.toInt(price5);
		int iprice10 = ScreenerUtils.toInt(price10);
		return getMinimum(iprice1, iprice3, iprice5, iprice10);
	}

	private static int getMinimum(int price1, int... prices) {
		int min = 0;
		if (prices != null) {
			min = Arrays.stream(prices).filter(Objects::nonNull).filter(i -> i > 0).min().orElse(0);
		}
		if (min == 0) {
			min = Math.max(price1, min);
		}
		return min;
	}

	public static int getDivisor(int iprice1, int iprice3, int iprice5, int iprice10) {
		int divisor = 1;
		if (iprice10 > 0) {
			divisor = 10;
		} else if (iprice5 > 0) {
			divisor = 5;
		} else if (iprice3 > 0) {
			divisor = 3;
		}
		return divisor;
	}

	public static void log(String data) {
		System.out.println(data);
	}

	public static synchronized boolean underProcess(Stock stock) {
		boolean isUnderProcess = false;
		try {
			Path path = Paths.get(ScreenerUtils.getCacheSerFileName(stock));
			File file = path.toFile();
			if (file.exists()) {
				isUnderProcess = true;
				log("file exists: " + file.getAbsolutePath());
			} else {
				file.createNewFile();
				log("file created: " + file.getAbsolutePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log("isUnderProcess: " + isUnderProcess);
		return isUnderProcess;
	}
	public static synchronized void cleanupTempFiles() {
		try {
			File file = new File(DIR_SER_CACHE);
			if(file.exists()) {
				Arrays.stream(file.listFiles((f, p) -> p.endsWith(".ser"))).forEach(File::delete);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Set<String> readSearchMap(String searchMapFile) throws Exception {
		Set<String> symbolsSet = new HashSet<>();
		Stream<String> stream = Files.lines(Paths.get(searchMapFile));
		stream.forEach(line -> {
			if (ScreenerUtils.isNotEmpty(line)) {
				symbolsSet.add(line.trim());
			}
		});
		stream.close();
		return symbolsSet;
	}

	public static HashMap<String, String> readPropertyFile(String filePath) throws Exception {
		HashMap<String, String> map = new HashMap<>();
		File file = new File(filePath);
		if (file.exists()) {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				if (nextLine != null && nextLine.indexOf("=") > 0) {
					String[] split = nextLine.split("=");
					map.put(split[0].trim(), split[1].trim());
				}
			}
			scanner.close();
		} else {
			throw new NullPointerException("File missing from location: " + file.getAbsolutePath());
		}
		return map;
	}
	
	public static void updateJsonStock(Stock stock) {
		try {
			String stockSymbolForSearch = getStockSymbolForSearch(stock.getSymbol());
			ScreenerUtils.log("Searching for symbol: {}", stockSymbolForSearch);
			String searchUrl = URL_SEARCH + stockSymbolForSearch;
			String data = downlaodUnparsedData(searchUrl).toString();
			JsonStock jsonStock = new JsonStock();
			jsonStock.setSymbol(stock.getSymbol());
			
			if (data == null) {
				String error = ScreenerUtils.logError("Json Stock is null for stock: {} and url: {}", stockSymbolForSearch, searchUrl);
				jsonStock.setComments(error);
			} else if ("[]".equals(data)) {
				String error = ScreenerUtils.logError("Json Stock is empty for stock: {} and url: {}", stockSymbolForSearch, searchUrl);
				jsonStock.setComments(error);
			} else if (data.startsWith("[")) {
				List<JsonStock> jsonData = GSON.fromJson(data, CLAZZ);
				if (jsonData != null) {
					ScreenerUtils.log("Found jsonData Size: {}", jsonData.size());
					if (jsonData.size() == 1) {
						jsonStock = jsonData.get(0);
					} else {
						String symbol = "/" + stock.getSymbol() + "/";
						jsonStock = jsonData.stream().filter(s -> (s.getUrl().indexOf(symbol) > 0)).findAny()
								.orElse(null);
					}
				}
			}
			updateStockMetaData(stock, jsonStock);
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
	}

	public static void updateStockMetaData(Stock stock, JsonStock jsonStock) {
		if(jsonStock != null) {
			jsonStock.setSymbol(stock.getSymbol());
			stock.setJsonStock(jsonStock);
			ScreenerUtils.log(jsonStock.toString());
		} else {
			ScreenerUtils.logError("Json Stock is null for stock: {}", stock.getSymbol());	
		}
	}

	public static synchronized Set<Stock> getStockSymbols(String inputFile) throws Exception {
		return getStockSymbols(inputFile, true, 10);
	}

	public static synchronized Set<Stock> getStockSymbols(String inputFile, boolean update, int updateDays) throws Exception {
		Set<String> readSymbols = ScreenerURLUtils.readSymbols(inputFile);
		Set<Stock> symbolsSet = readSymbols.stream()
				.map(ScreenerUtils::trimToNull)
				.filter(Objects::nonNull)
				.map(s->{
					Stock stock = new Stock(s);
					stock.setUpdate(update);
					stock.setDaysToUpdate(updateDays);
					return stock;
				}).sorted()
				.collect(Collectors.toSet());
		
		return symbolsSet;
	}

}
