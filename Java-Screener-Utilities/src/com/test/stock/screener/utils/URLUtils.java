package com.test.stock.screener.utils;

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
import java.util.Date;
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
import com.test.stock.screener.meta.Constants;
import com.test.stock.screener.meta.Counter;
import com.test.stock.screener.meta.JsonStock;
import com.test.stock.screener.meta.Stock;

public class URLUtils implements Constants {
	private static HashMap<String, String> searchURLMap = new HashMap<>();

	public static String metadataFile = Utils.getStocksHomeDir() + "symbols_metadata.json";
	public static String searchMapFile = Utils.getStocksHomeDir() + "symbols_searchMap.txt";
	public static String inputFile = Utils.getStocksHomeDir() + "symbols_input.txt";
	public static String outFile = Utils.getStocksHomeDir() + "output.csv";
	public static String logFile = Utils.getStocksHomeDir() + "run.log";
	public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static Type CLAZZ = new TypeToken<ArrayList<JsonStock>>() {
	}.getType();

	public static void init() throws Exception {
		Utils.recreateFile(logFile);
		loadSearchURLMap(searchMapFile);
		Utils.ensureFile(metadataFile);
		Utils.ensureFolder(DIR_CACHE);
		Utils.ensureFolder(DIR_RAW_CACHE);
		Utils.ensureFolder(DIR_SER_CACHE);
	}

	public static String readTag(Stock stock, String key, String tag) throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		Stream<String> stream = getStockData(stock);
		stream.filter(Objects::nonNull).forEach(line -> {
			tagValue.append(readDataBetween(stock, key, line, tag, "=", 1));
		});
		stream.close();
		if (tagValue.length() > 0 && !Character.isDigit(tagValue.charAt(0))) {
			tagValue.deleteCharAt(0);
		}
		// Utils.log(stock + ": " + tag + "=" + tagValue);
		return tagValue.toString();
	}

	public static String readDataBetween(Stock stock, String key, String stratTag, String endTag) throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		Stream<String> stream = getStockData(stock);
		stream.forEach(line -> {
			tagValue.append(readDataBetween(stock, key, line, stratTag, endTag));
		});
		stream.close();
		return tagValue.toString();
	}

	public static String readDataBetweenUsingEndTag(Stock stock, String key, String stratTag, String endTag)
			throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		try (Stream<String> stream = getStockData(stock)) {
			stream.forEach(line -> {
				tagValue.append(readDataBetweenUsingEndTag(line, key, stratTag, endTag));
			});
		}
		// Utils.log(stock + ": " + endTag + ": " + tagValue);
		return tagValue.toString();
	}

	public static void parseCAGR(Stock stock, String data) {
		if (data != null && data.length() > 3) {
			// 10Years:16%5Years:20%3Years:34%1Year:109%
			stock.setCagr1(Utils.toIntDefault(readDataBetweenDoubly(stock, "Cagr1", data, "1Year:=", "1Year:", "%")));
			stock.setCagr3(Utils.toIntDefault(readDataBetweenDoubly(stock, "Cagr3", data, "3Years:=", "3Years:", "%")));
			stock.setCagr5(Utils.toIntDefault(readDataBetweenDoubly(stock, "Cagr5", data, "5Years:=", "5Years:", "%")));
			stock.setCagr10(
					Utils.toIntDefault(readDataBetweenDoubly(stock, "Cagr10", data, "10Years:=", "10Years:", "%")));
			stock.setCagrAvg(average(stock.getCagr1(), stock.getCagr3(), stock.getCagr5(), stock.getCagr10()));
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
			stock.setSale1(Utils.toIntDefault(readDataBetweenDoubly(stock, "Sale1", data, "TTM:=", "TTM:", "%")));
			stock.setSale3(Utils.toIntDefault(readDataBetweenDoubly(stock, "Sale3", data, "3Years:=", "3Years:", "%")));
			stock.setSale5(Utils.toIntDefault(readDataBetweenDoubly(stock, "Sale5", data, "5Years:=", "5Years:", "%")));
			stock.setSale10(
					Utils.toIntDefault(readDataBetweenDoubly(stock, "Sale10", data, "10Years:=", "10Years:", "%")));
			stock.setSaleAvg(average(stock.getSale1(), stock.getSale3(), stock.getSale5(), stock.getSale10()));
			// stock.setSaleAvg35(average35(stock.getSale1(), stock.getSale3(),
			// stock.getSale5()));
		}
	}

	private static String readDataBetweenDoubly(Stock stock, String key, String data, String stratTag,
			String anotherstratTag, String endTag) {
		String retVal = readDataBetween(stock, key, data, stratTag, endTag, 0);
		// Utils.log("1retVal=" + retVal);
		if (Utils.isEmpty(retVal)) {
			retVal = readDataBetween(stock, key, data, anotherstratTag, endTag, 0);
		}
		// Utils.log("2retVal=" + retVal);
		retVal = (retVal != null && retVal.equals("=") ? "" : retVal);
		return Utils.trimToEmpty(retVal);
	}

	private static String readDataBetween(Stock stock, String key, String data, String stratTag, String endTag) {
		return readDataBetween(stock, key, data, stratTag, endTag, 0);
	}

	private static String readDataBetween(Stock stock, String key, String data, String stratTag, String endTag,
			int ignorePos) {
		String retValue = "";
		if (data != null && data.indexOf(stratTag) > -1) {
			int start = data.indexOf(stratTag) + stratTag.length() + ignorePos;
			int end = data.indexOf(endTag, start);
			retValue = (data.substring(start, end));
			Utils.log("Extracted Key={}, Value={} ", key, retValue);
		}
		return Utils.trimToEmpty(retValue);
	}

	private static String readDataBetweenUsingEndTag(String data, String key, String stratTag, String endTag) {
		String retValue = "";
		try {
			if (data != null && data.indexOf(endTag) > -1) {
				int end = data.indexOf(endTag);
				int start = data.lastIndexOf(stratTag, end - 1);
				double ttmValue = Utils.toDouble(data.substring(start + 1, end));

				end = start;
				start = data.lastIndexOf(stratTag, end - 1);
				double preValue = Utils.toDouble(data.substring(start + 1, end));

				retValue = Double.valueOf(Utils.minDouble(ttmValue, preValue)).toString();
				Utils.log("Extracted Key={}, Value={} ", key, retValue);
			}
		} catch (Exception e) {
			Utils.handleException(e);
		}
		return Utils.trimToEmpty(retValue);
	}

	public static String readHighestPrice(Stock stock) {
		String highestPrice = "0";
		try (Stream<String> stream = getStockRawData(stock)) {
			final StringBuffer tagValue = new StringBuffer("");
			stream.forEach(line -> {
				tagValue.append(readDataBetween(stock, "Hi3Y", line, "PriceDataStart=", "=PriceDataEnd"));
			});
			if (!tagValue.isEmpty()) {
				List<Double> pricesList = new ArrayList<>();
				Map<?, ?> map = GSON.fromJson(tagValue.toString(), Map.class);
				if (map.containsKey("datasets")) {
					Object datasets = map.get("datasets");
					if (datasets instanceof List) {
						List<?> list = (List<?>) datasets;
						Object object2 = list.get(0);
						if (object2 instanceof Map) {
							Map<?, ?> map1 = (Map<?, ?>) object2;
							if (map1.containsKey("metric") && map1.containsValue("Price")
									&& map1.containsKey("values")) {
								Object object3 = map1.get("values");
								if (object3 instanceof List) {
									List<?> list1 = (List<?>) object3;
									if (list1 != null && !list1.isEmpty()) {
										for (Object object4 : list1) {
											if (object4 instanceof List) {
												List<?> list2 = (List<?>) object4;
												if (list2 != null && list2.size() > 1) {
													Object object5 = list2.get(1);
													pricesList.add(Utils.toDoubleObject(object5));
												}
											}
										}
									}
								}
							}
						}
					}
				}
				highestPrice = Double.toString(pricesList.stream().mapToDouble(Double::doubleValue).max().orElse(0));
			}

		} catch (Exception e) {
			Utils.handleException(e);
		}
		return highestPrice;
	}

	private static Stream<String> getStockData(Stock stock) throws Exception {
		return streamFile(Utils.getCacheFileName(stock));
	}

	private static Stream<String> getStockRawData(Stock stock) throws Exception {
		return streamFile(Utils.getCacheRawFileName(stock));
	}

	private static Stream<String> streamFile(String fileName) throws Exception {
		try {
			Path path = Paths.get(fileName);
			return Files.lines(path, Charset.forName("UTF-8"));
		} catch (Exception e) {
			Utils.handleException(e);
		}
		return Stream.empty();
	}

	private static boolean needsUpdate(Stock stock) throws Exception {
		boolean needsUpdate = true;
		int days = stock.getDaysToUpdate();
		Path path = Paths.get(Utils.getCacheFileName(stock));
		if (Files.exists(path)) {
			FileTime lastModifiedTime = Files.getLastModifiedTime(path);
			Utils.log("Verify last Modified Time for path: {}", path);
			Utils.log("last Modified Time: {}", Utils.formatDate(new Date(lastModifiedTime.toMillis())));
			long modified = lastModifiedTime.toInstant().getEpochSecond();
			long now = Instant.now().getEpochSecond();
			long diff = now - modified;
			long day = days * 24 * 60 * 60;
			long diffDays = diff / (24 * 60 * 60);
			Utils.log("File exists so checking needsUpdate stock for days: {} with diffDays: {}", days, diffDays);
			needsUpdate = (day - diff) < 0;
		}
		Utils.log("needsUpdate : " + needsUpdate);
		return needsUpdate;
	}

	public static void downloadStockHTML(Stock stock) {
		try {
			boolean needsUpdate = needsUpdate(stock);
			if (needsUpdate) {
				Utils.log("Downloading data for:{ " + Counter.getCounter() + " }: " + stock);
				String str = downloadStockFullHTML(stock);
				writeFile(stock, str);
				Utils.log("Data downloaded for: " + stock);
				Utils.sleepRandomly(5);
			} else {
				Utils.log("Updated data already exists for : " + stock);
			}
		} catch (Exception e) {
			stock.setFailed();
			Utils.handleException(e);
		}
	}

	private static void writeFile(Stock stock, String str) throws Exception {
		try {
			if (Utils.isNotEmpty(str) && !stock.isFailed()) {
				writeFile(Utils.getCacheRawFileName(stock), str);
				str = normalizeData(str);
				if (Utils.isNotEmpty(str)) {
					str = "name=" + stock.getName() + "=" + str;
					writeFile(Utils.getCacheFileName(stock), str);
				}
			}
		} catch (Exception e) {
			stock.setFailed();
			Utils.handleException(e);
			throw e;
		}
	}

	private static String downloadStockFullHTML(Stock stock) throws Exception {
		StringBuffer buff = new StringBuffer("");
		buff.append(downlaodUnparsedData(stock, stock.getCompanyURL()));
		buff.append("=MedianPEDataStart=");
		buff.append(downlaodUnparsedData(stock, stock.getMedianPEURL()));
		buff.append("=MedianPEDataEnd=PriceDataStart=");
		buff.append(downlaodUnparsedData(stock, stock.getPriceUrl()));
		buff.append("=PriceDataEnd=");
		return buff.toString();
	}

	private static StringBuffer downlaodUnparsedData(Stock stock, String url) throws Exception {
		StringBuffer buff = new StringBuffer("");
		try {
			Utils.log("Downloading: " + url);
			try (Scanner scanner = new Scanner(new URL(url).openStream())) {
				while (scanner.hasNextLine()) {
					buff.append(scanner.nextLine());
				}
			}
		} catch (Exception e) {
			stock.setFailed();
			Utils.handleException(e);
			throw e;
		}
		return buff;
	}

	private static String normalizeData(String str) {
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

	public static void writeFile(String fileName, String data) {
		try {
			Utils.log("Writing file to path: {}", fileName);
			Files.write(Paths.get(fileName), data.getBytes());
		} catch (Exception e) {
			Utils.handleException(e);
		}
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
		return Utils.toIntDefault(data);
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

	private static String getStockSymbolForSearch(String str) {
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

	private static String replaceAll(String str) {
		if (str != null) {
			str = str.replaceAll("E-Commerce", "EComm");
			str = str.replaceAll("DiamondGemsandJewellery", "Jewellery");
			str = str.replaceAll("InfrastructureDevelopers", "Infra");
			str = str.replaceAll("QuickServiceRestaurant", "QSR");
		}
		return str;
	}

	private static Set<String> readSymbols(String dataFile) throws Exception {
		Set<String> symbolsSet = new HashSet<>();
		Stream<String> stream = Files.lines(Paths.get(dataFile));
		stream.forEach(line -> {
			if (Utils.isNotEmpty(line)) {
				symbolsSet.add(line.trim());
			}
		});
		stream.close();
		return symbolsSet;
	}

	private static int average(String price1, String price3, String price5, String price10) {
		int iprice1 = Utils.toInt(price1);
		int iprice3 = Utils.toInt(price3);
		int iprice5 = Utils.toInt(price5);
		int iprice10 = Utils.toInt(price10);
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

	public static boolean underProcess(Stock stock) {
		boolean isUnderProcess = false;
		try {
			Path path = Paths.get(Utils.getTempFileName(stock));
			File file = path.toFile();
			if (file.exists()) {
				isUnderProcess = true;
				Utils.log("file exists: " + file.getAbsolutePath());
			} else {
				file.createNewFile();
				Utils.log("file created: " + file.getAbsolutePath());
			}
		} catch (Exception e) {
			Utils.handleException(e);
		}
		Utils.log("isUnderProcess: " + isUnderProcess);
		return isUnderProcess;
	}

	public static void cleanupTempFiles() {
		try {
			File file = new File(DIR_SER_CACHE);
			if (file.exists()) {
				Utils.sleepRandomly(5);
				Utils.log("Cleaning up all temporary .tmp files from: {} ", file.getAbsolutePath());
				Arrays.stream(file.listFiles((f, p) -> p.endsWith(".tmp"))).forEach(File::delete);
			}
		} catch (Exception e) {
			Utils.handleException(e);
		}
	}

	private static HashMap<String, String> readPropertyFile(String filePath) throws Exception {
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
			Utils.log("Searching for symbol: {}", stockSymbolForSearch);
			String searchUrl = URL_SEARCH + stockSymbolForSearch;
			String data = downlaodUnparsedData(stock, searchUrl).toString();
			JsonStock jsonStock = new JsonStock();
			jsonStock.setSymbol(stock.getSymbol());

			if (data == null) {
				String error = Utils.logError("Json Stock is null for stock: {} and url: {}", stockSymbolForSearch,
						searchUrl);
				jsonStock.setComments(error);
			} else if ("[]".equals(data)) {
				String error = Utils.logError("Json Stock is empty for stock: {} and url: {}", stockSymbolForSearch,
						searchUrl);
				jsonStock.setComments(error);
			} else if (data.startsWith("[")) {
				List<JsonStock> jsonData = GSON.fromJson(data, CLAZZ);
				if (jsonData != null) {
					Utils.log("Found jsonData Size: {}", jsonData.size());
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
			Utils.handleException(e);
		}
	}

	public static void updateStockMetaData(Stock stock, JsonStock jsonStock) {
		if (jsonStock != null) {
			jsonStock.setSymbol(stock.getSymbol());
			stock.setJsonStock(jsonStock);
			if (!jsonStock.getUrl().startsWith(URL_BASE)) {
				jsonStock.setUrl(URL_BASE + jsonStock.getUrl());
			}
			jsonStock.setMedianPEURL(Utils.constructMedianPEURL(stock));
			jsonStock.setPriceUrl(Utils.constructPriceUrl(stock));
			Utils.log(jsonStock.toString());
		} else {
			Utils.logError("Json Stock is null for stock: {}", stock.getSymbol());
		}
	}

	public static Set<Stock> getStocksToProcess() throws Exception {
		List<JsonStock> stockMetaData = getStockMetaData();
		Set<Stock> stocksSet = getStockSymbols();
		updateJsonStock(stocksSet, stockMetaData);
		stocksSet.stream().forEach(s -> s.setCached(Utils.isCacheFileExists(s)));
		return stocksSet;
	}

	public static Set<Stock> getSpecificStocksToProcess() throws Exception {
		List<JsonStock> stockMetaData = getStockMetaData();
		Set<Stock> stocksSet = getStockSymbols();
		updateSpecificJsonStock(stocksSet, stockMetaData);
		stocksSet.stream().forEach(s -> s.setCached(Utils.isCacheFileExists(s)));
		return stocksSet;
	}

	private static Set<Stock> getStockSymbols() throws Exception {
		Set<String> readSymbols = readSymbols(inputFile);
		Set<Stock> symbolsSet = readSymbols.stream().map(Utils::trimToNull).filter(Objects::nonNull).map(Stock::new)
				.sorted().collect(Collectors.toSet());

		return symbolsSet;
	}

	public static void handleFailedStocks(Set<Stock> stocksSet) {
		List<Stock> failed = stocksSet.stream().filter(Stock::isFailed).collect(Collectors.toList());
		Utils.printCollection(failed, "Below stocks failed: ");
	}

	private static List<JsonStock> getStockMetaData() throws Exception {
		List<JsonStock> jsonData = null;
		String metadata = Utils.getFullDataFromFile(metadataFile);
		if (Utils.isNotEmpty(metadata)) {
			jsonData = GSON.fromJson(metadata, CLAZZ);
			Utils.log("Stock MetaData read size: {}", jsonData.size());
		}
		return Utils.ensureList(jsonData);
	}

	private static void updateJsonStock(Set<Stock> stocksSet, List<JsonStock> stockMetaData) {
		Map<String, JsonStock> metaStocksMap = Utils.toMap(stockMetaData, JsonStock::getSymbol);
		Map<String, Stock> stocksMap = Utils.toMap(stocksSet, Stock::getSymbol);

		stocksMap.entrySet().forEach(e -> {
			if (metaStocksMap.containsKey(e.getKey())) {
				JsonStock jsonStock = metaStocksMap.get(e.getKey());
				e.getValue().setJsonStock(jsonStock);
				e.getValue().setName(jsonStock.getName());
			}
		});
		metaStocksMap.entrySet().forEach(e -> {
			if (!stocksMap.containsKey(e.getKey())) {
				JsonStock jsonStock = e.getValue();
				Stock stock = new Stock(e.getKey());
				stock.setJsonStock(jsonStock);
				stock.setName(jsonStock.getName());
				stocksSet.add(stock);
			}
		});
	}

	private static void updateSpecificJsonStock(Set<Stock> stocksSet, List<JsonStock> stockMetaData) {
		Map<String, JsonStock> metaStocksMap = Utils.toMap(stockMetaData, JsonStock::getSymbol);
		Map<String, Stock> stocksMap = Utils.toMap(stocksSet, Stock::getSymbol);

		stocksMap.entrySet().forEach(e -> {
			if (metaStocksMap.containsKey(e.getKey())) {
				JsonStock jsonStock = metaStocksMap.get(e.getKey());
				e.getValue().setJsonStock(jsonStock);
				e.getValue().setName(jsonStock.getName());
			}
		});
	}

	private static void loadSearchURLMap(String filePath) throws Exception {
		Map<String, String> properties = readPropertyFile(filePath);
		searchURLMap.putAll(properties);
	}

}
