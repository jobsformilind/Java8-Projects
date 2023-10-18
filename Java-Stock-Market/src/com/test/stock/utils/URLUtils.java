package com.test.stock.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.stock.JsonStock;
import com.test.stock.Stock;

public class URLUtils {
	private static String medianPESuffix = "/chart/?q=Price+to+Earning-Median+PE-EPS&days=1825";
	private static String consolidatedSuffix = "&consolidated=true";
	private static String baseURL = "https://www.screener.in";
	private static String url = "https://www.screener.in/company/";
	private static String apiURL = "https://www.screener.in/api/company/";
	private static String searchURL = "https://www.screener.in/api/company/search/?q=";
	private static String cacheFolder = Utils.getStocksHomeDir() + "cache\\";
	private static String rawcacheFolder = cacheFolder + "rawcache\\";
	private static String logFileName = Utils.getStocksHomeDir() + "\\out.log";
	public static Gson gson = new Gson();
	private static Type clazz = new TypeToken<ArrayList<JsonStock>>(){}.getType();

	public static String readTag(Stock stock, String tag) throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		Stream<String> stream = URLUtils.getStockData(stock);
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
		Stream<String> stream = URLUtils.getStockData(stock);
		stream.forEach(line -> {
			tagValue.append(readDataBetween(stock, line, stratTag, endTag));
		});
		stream.close();
		log(stock + ": " + tagValue);
		return tagValue.toString();
	}
	
	public static String readDataBetweenAfterIndex(Stock stock, String preTag, String stratTag, String endTag) throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		Stream<String> stream = URLUtils.getStockData(stock);
		stream.forEach(line -> {
			if(line != null && line.indexOf(preTag)>0) {
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

	public static String readDataFromStart(Stock stock, String indexTag, String startTag, String endTag) throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		try (Stream<String> stream = URLUtils.getStockData(stock)) {
			stream.filter(Objects::nonNull).forEach(line -> {
				int indexTagStart = Utils.indexOfIncludingString(line, indexTag);
				String data = readDataFromStart(line, indexTagStart, startTag, endTag);
				tagValue.append(data);
			});
		}
		log(stock + ": " + startTag + ": " + tagValue);
		return tagValue.toString();
	}
	
	public static String readDataFromEnd(Stock stock, String startTag, String endTag) throws Exception {
		final StringBuffer tagValue = new StringBuffer("");
		try (Stream<String> stream = URLUtils.getStockData(stock)) {
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
		try (Stream<String> stream = URLUtils.getStockData(stock)) {
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
			stock.setCagr1(Utils.toIntDefault(readDataBetweenDoubly(stock, data, "1Year:=", "1Year:", "%")));
			stock.setCagr3(Utils.toIntDefault(readDataBetweenDoubly(stock, data, "3Years:=", "3Years:", "%")));
			stock.setCagr5(Utils.toIntDefault(readDataBetweenDoubly(stock, data, "5Years:=", "5Years:", "%")));
			stock.setCagr10(Utils.toIntDefault(readDataBetweenDoubly(stock, data, "10Years:=", "10Years:", "%")));
			stock.setCagrAvg(average(stock.getCagr1(), stock.getCagr3(), stock.getCagr5(), stock.getCagr10()));
			//stock.setCagrAvg35(average35(stock.getCagr1(), stock.getCagr3(), stock.getCagr5()));

		}
	}

	public static void parseROE(Stock stock, String data) {
		if (data != null && data.length() > 3) {
			// 10Years:40%5Years:38%3Years:42%LastYear:43%
			//stock.setRoe1(readDataBetweenDoubly(stock, data, "LastYear:=", "LastYear:", "%"));
			//stock.setRoe3(readDataBetweenDoubly(stock, data, "3Years:=", "3Years:", "%"));
			//stock.setRoe5(readDataBetweenDoubly(stock, data, "5Years:=", "5Years:", "%"));
			//stock.setRoe10(readDataBetweenDoubly(stock, data, "10Years:=", "10Years:", "%"));
			//stock.setRoeAvg(average(stock.getRoe1(), stock.getRoe3(), stock.getRoe5(), stock.getRoe10()));
			//stock.setRoeAvg35(average35(stock.getRoe1(), stock.getRoe3(), stock.getRoe5()));
		}
	}

	public static void parseProfit(Stock stock, String data) {
		if (data != null && data.length() > 3) {
			// 10Years:16%5Years:7%3Years:8%TTM:-4%
			//stock.setProfit1(readDataBetweenDoubly(stock, data, "TTM:=", "TTM:", "%"));
			//stock.setProfit3(readDataBetweenDoubly(stock, data, "3Years:=", "3Years:", "%"));
			//stock.setProfit5(readDataBetweenDoubly(stock, data, "5Years:=", "5Years:", "%"));
			//stock.setProfit10(readDataBetweenDoubly(stock, data, "10Years:=", "10Years:", "%"));
			//stock.setProfitAvg(average(stock.getProfit1(), stock.getProfit3(), stock.getProfit5(), stock.getProfit10()));
			//stock.setProfitAvg35(average35(stock.getProfit1(), stock.getProfit3(), stock.getProfit5()));
		}
	}

	public static void parseSales(Stock stock, String data) {
		if (data != null && data.length() > 3) {
			// 10Years:16%5Years:7%3Years:8%TTM:-4%
			// 10Years:=16%=5Years:=17%=3Years:=12%=TTM:=-8%
			stock.setSale1(Utils.toIntDefault(readDataBetweenDoubly(stock, data, "TTM:=", "TTM:", "%")));
			stock.setSale3(Utils.toIntDefault(readDataBetweenDoubly(stock, data, "3Years:=", "3Years:", "%")));
			stock.setSale5(Utils.toIntDefault(readDataBetweenDoubly(stock, data, "5Years:=", "5Years:", "%")));
			stock.setSale10(Utils.toIntDefault(readDataBetweenDoubly(stock, data, "10Years:=", "10Years:", "%")));
			stock.setSaleAvg(average(stock.getSale1(), stock.getSale3(), stock.getSale5(), stock.getSale10()));
			//stock.setSaleAvg35(average35(stock.getSale1(), stock.getSale3(), stock.getSale5()));
		}
	}

	public static String readDataBetweenDoubly(Stock stock, String data, String stratTag, String anotherstratTag,
			String endTag) {
		String retVal = readDataBetween(stock, data, stratTag, endTag, 0);
		// log("1retVal=" + retVal);
		if (Utils.isEmpty(retVal)) {
			retVal = readDataBetween(stock, data, anotherstratTag, endTag, 0);
		}
		// log("2retVal=" + retVal);
		retVal = (retVal != null && retVal.equals("=") ? "" : retVal);
		return Utils.trimToEmpty(retVal);
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
		return Utils.trimToEmpty(retValue);
	}

	public static String readDataBetween(String line, String stratTag, String endTag) {
		String retValue = "";
		if (line != null && line.indexOf(stratTag) > -1) {
			int start = line.indexOf(stratTag) + stratTag.length();
			int end = line.indexOf(endTag, start);
			retValue = line.substring(start, end);
		}
		log("Extracted Data: " + retValue);
		return Utils.trimToEmpty(retValue);
	}
	private static String readDataBetween(Stock stock, String data, int startPos, String stratTag, String endTag) {
		String retValue = "";
		if (data != null && data.indexOf(stratTag) > -1) {
			int start = data.indexOf(stratTag, startPos) + stratTag.length();
			int end = data.indexOf(endTag, start);
			retValue = (data.substring(start, end));
		}
		log(stock + ": " + stratTag + ": " + retValue);
		return Utils.trimToEmpty(retValue);
	}
	
	private static String readDataFromStart(String line, int indexTagStart, String stratTag, String endTag) {
		String retValue = "";
		if (line != null && line.indexOf(stratTag) > -1) {
			int start = line.indexOf(stratTag, indexTagStart);
			int end = line.indexOf(endTag, start) + endTag.length();
			retValue = line.substring(start, end);
		}
		return Utils.trimToEmpty(retValue);
	}
	
	private static String readDataFromEnd(String line, String stratTag, String endTag) {
		String retValue = "";
		try {
			if (line != null && line.indexOf(endTag) > 0) {
				int end = line.indexOf(endTag) + endTag.length();
				int start = line.lastIndexOf(stratTag, end-1);
				retValue = line.substring(start, end);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Utils.trimToEmpty(retValue);
	}
	
	public static String readDataBetweenUsingEndTag(String data, String stratTag, String endTag) {
		String retValue = "";
		try {
			if (data != null && data.indexOf(endTag) > -1) {
				int end = data.indexOf(endTag);
				int start = data.lastIndexOf(stratTag, end-1);
				double ttmValue = Utils.toDouble(data.substring(start+1, end));

				end = start;
				start = data.lastIndexOf(stratTag, end-1);
				double preValue = Utils.toDouble(data.substring(start+1, end));
				
				retValue = Double.valueOf(Utils.minDouble(ttmValue, preValue)).toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log("Extracted Data: " + retValue);
		return Utils.trimToEmpty(retValue);
	}
	private static String readDataBetweenUsingEndTag(Stock stock, String data, String stratTag, String endTag) {
		String retValue = "";
		try {
			if (data != null && data.indexOf(endTag) > -1) {
				int end = data.indexOf(endTag);
				int start = data.lastIndexOf(stratTag, end-1);
				String ttmValue = data.substring(start+1, end);
				if(Utils.isNotEmpty(ttmValue)) {
					end = start;
					start = data.lastIndexOf(stratTag, end-1);
					retValue = data.substring(start+1, end);
					if(Utils.isNotDouble(retValue)) {
						retValue = ttmValue;
					}
				} 
				if(Utils.isNotDouble(retValue)) {
					retValue = "0";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Utils.trimToEmpty(retValue);
	}

	public static Stream<String> getStockData(Stock stock) throws Exception {
		try {
			String fileName = cacheFolder + stock + ".html";
			Path path = Paths.get(fileName);
			return Files.lines(path, Charset.forName("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Stream.empty();
	}

	public static boolean notExists(Stock stock) {
		return !exists(stock);
	}

	public static boolean exists(Stock stock) {
		try {
			String fileName = cacheFolder + stock + ".html";
			return Files.exists(Paths.get(fileName));
		} catch (Exception e) {
			// Dont handle
		}
		return false;
	}

	public static boolean needsUpdate(Stock stock) {
		return needsUpdate(stock, stock.getDaysToUpdate());
	}

	public static boolean needsUpdate(Stock stock, int days) {
		if(stock.isForceUpdate()) {
			return true;
		}
		try {
			String fileName = cacheFolder + stock + ".html";
			FileTime lastModifiedTime = Files.getLastModifiedTime(Paths.get(fileName));
			long modified = lastModifiedTime.toInstant().getEpochSecond();
			long now = Instant.now().getEpochSecond();
			long diff = now - modified;
			long day = days * 24 * 60 * 60;
			return (day - diff) < 0;
		} catch (Exception e) {
			// Dont handle
		}
		return true;
	}

	public static void getStockHtml(Stock stock, boolean update) throws Exception {
		getStockHtml(stock, update, 5000);
	}

	public static void getStockHtml(Stock stock, boolean update, long sleep) throws Exception {
		writeHTML(stock, url + stock, cacheFolder + stock + ".html", update, sleep);
	}

	public static void writeHTML(Stock stock, String url, String fileName, boolean update) throws Exception {
		writeHTML(stock, url, fileName, update, 5000);
	}

	public static void writeHTML(Stock stock, String url, String fileName, boolean update, long sleep)
			throws Exception {
		if (!update) {
			return;
		}
		boolean needsUpdate = needsUpdate(stock);
		log("Downloading data for:{ " + Counter.getCounter() + " }: " + stock);
		if (needsUpdate) {
			Thread.sleep(5000);
			boolean exists = exists(stock);
			url = getUrl(stock, url);
			log("update: " + update + ", File exists: " + exists);
			String str = downlaodData(stock, url);
			writeFile(stock, fileName, str);
			log("Data downloaded for: " + stock);
		} else {
			log("Updated data already exists for : " + stock);
		}
	}
	
	private static String getUrl(Stock stock, String url) {
		try {
			String searchUrl = searchURL + stock.getSymbol();			
			String data = downlaodUnparsedData(searchUrl).toString();
			if(data != null && data.startsWith("[")) {
				List<JsonStock> jsonData = gson.fromJson(data, clazz);
				if(jsonData != null) {
					String symbol = "/" + stock.getSymbol() + "/";
					JsonStock jsonStock = jsonData.stream().filter(s->(s.getUrl().indexOf(symbol)>0)).findAny().orElse(null);
					if(jsonStock == null && !jsonData.isEmpty()) {
						jsonStock = jsonData.get(0);
					}
					if(jsonStock != null) {
						url = baseURL + jsonStock.getUrl();
						stock.setConsolidated(jsonStock.getUrl().indexOf("consolidated")>0);
						stock.setCompanyId(jsonStock.getId());
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	public static void writeFile(Stock stock, String fileName, String str) throws Exception {
		ensureCacheFolder();
		writeFile(rawcacheFolder + stock + ".html", str);
		str = normalizeData(str);
		writeFile(fileName, str);
	}

	
	public static String downlaodData(Stock stock, String url) {
		StringBuffer buff = new StringBuffer("");
		buff.append(downlaodUnparsedData(url));
		buff.append(downlaodMedianPEData(stock));
		return buff.toString();
	}

	private static StringBuffer downlaodMedianPEData(Stock stock) {
		StringBuffer buff = new StringBuffer("");
		if(stock.getCompanyId()>0) {
			StringBuffer medianPEURL = new StringBuffer("");
			medianPEURL.append(apiURL);
			medianPEURL.append(stock.getCompanyId());
			medianPEURL.append(medianPESuffix);
			if(stock.isConsolidated()) {
				medianPEURL.append(consolidatedSuffix);	
			}
			buff.append(downlaodUnparsedData(medianPEURL.toString()));
		}
		return buff;
	}

	public static StringBuffer downlaodUnparsedData(String url) {
		StringBuffer buff = new StringBuffer("");
		try {
			log("Download: " + url);
			try (Scanner scanner = new Scanner(new URL(url).openStream())) {
				while (scanner.hasNext()) {
					buff.append(scanner.next());
				}
			}
		} catch (Exception e) {
			log("Got exception while downloaing data : " + url);
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
			e.printStackTrace();
		}
	}

	public static void ensureCacheFolder() {
		try {
			Files.createDirectories(Paths.get(rawcacheFolder));
		} catch (Exception e) {
			//Ignore exception
		}
	}

	public static Stream<String> getFromCache(String fileName) {
		try {
			return Files.lines(Paths.get(cacheFolder + fileName), Charset.forName("UTF-8"));
		} catch (Exception e) {
			// Dont handle
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

	public static String replaceAll(String str) {
		if (str != null) {
			str = str.replaceAll("E-Commerce", "EComm");
			str = str.replaceAll("DiamondGemsandJewellery", "Jewellery");
			str = str.replaceAll("InfrastructureDevelopers", "Infra");
			str = str.replaceAll("QuickServiceRestaurant", "QSR");
		}
		return str;
	}

	public static SortedSet<String> readSymbols(String dataFile) throws Exception {
		SortedSet<String> symbolsSet = new TreeSet<>();
		Stream<String> stream = Files.lines(Paths.get(dataFile));
		stream.forEach(line -> {
			if (Utils.isNotEmpty(line)) {
				symbolsSet.add(line.trim());
			}
		});
		stream.close();
		return symbolsSet;
	}

	public static int average35(String price1, String price3, String price5) {
		int avg = average("0", price3, price5, "0");
		if(avg==0) {
			avg = Utils.toInt(price1);
		}
		return avg;
	}

	public static int average(String price1, String price3, String price5, String price10) {
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

	public static int getDivisor(int iprice1, int iprice3, int iprice5, int iprice10) {
		int divisor = 1;
		if(iprice10>0) {
			divisor = 10;
		} else if(iprice5>0) {
			divisor = 5;
		} else if(iprice3>0) {
			divisor = 3;
		}
		return divisor;
	}

	public static void log(String data) {
		try {
			System.out.println(data);
			data = data + "\n";
			Files.write(Paths.get(logFileName), data.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.out.println("Error while writing to log file : " + logFileName);
		}
	}

	public static void logClean() {
		try {
			File file = Paths.get(logFileName).toFile();
			if (!file.exists()) {
				file.createNewFile();
			}
			Files.write(Paths.get(logFileName), "\n".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.out.println("Error while writing to log file : " + logFileName);
		}
	}
}
