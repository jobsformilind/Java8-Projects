package com.test.stock.utils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Objects;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.test.stock.Stock;

public class URLUtils {
	private static String stocksDir = "D:\\Temp-Stocks\\";
	private static String url = "https://www.screener.in/company/";
	private static String cacheFolder = stocksDir + "cache\\";
	private static String logFileName = stocksDir + "\\out.log";
	public static boolean DEBUG = false;

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

	public static void parseCAGR(Stock stock, String data) {
		if (data != null && data.length() > 3) {
			// 10Years:16%5Years:20%3Years:34%1Year:109%
			stock.setCagr1(readDataBetweenDoubly(stock, data, "1Year:=", "1Year:", "%"));
			stock.setCagr3(readDataBetweenDoubly(stock, data, "3Years:=", "3Years:", "%"));
			stock.setCagr5(readDataBetweenDoubly(stock, data, "5Years:=", "5Years:", "%"));
			stock.setCagr10(readDataBetweenDoubly(stock, data, "10Years:=", "10Years:", "%"));
			stock.setCagrAvg(average(stock.getCagr1(), stock.getCagr3(), stock.getCagr5(), stock.getCagr10()));
			stock.setCagrAvg35(average35(stock.getCagr1(), stock.getCagr3(), stock.getCagr5()));

		}
	}

	public static void parseROE(Stock stock, String data) {
		if (data != null && data.length() > 3) {
			// 10Years:40%5Years:38%3Years:42%LastYear:43%
			stock.setRoe1(readDataBetweenDoubly(stock, data, "LastYear:=", "LastYear:", "%"));
			stock.setRoe3(readDataBetweenDoubly(stock, data, "3Years:=", "3Years:", "%"));
			stock.setRoe5(readDataBetweenDoubly(stock, data, "5Years:=", "5Years:", "%"));
			stock.setRoe10(readDataBetweenDoubly(stock, data, "10Years:=", "10Years:", "%"));
			stock.setRoeAvg(average(stock.getRoe1(), stock.getRoe3(), stock.getRoe5(), stock.getRoe10()));
			stock.setRoeAvg35(average35(stock.getRoe1(), stock.getRoe3(), stock.getRoe5()));
		}
	}

	public static void parseProfit(Stock stock, String data) {
		if (data != null && data.length() > 3) {
			// 10Years:16%5Years:7%3Years:8%TTM:-4%
			stock.setProfit1(readDataBetweenDoubly(stock, data, "TTM:=", "TTM:", "%"));
			stock.setProfit3(readDataBetweenDoubly(stock, data, "3Years:=", "3Years:", "%"));
			stock.setProfit5(readDataBetweenDoubly(stock, data, "5Years:=", "5Years:", "%"));
			stock.setProfit10(readDataBetweenDoubly(stock, data, "10Years:=", "10Years:", "%"));
			stock.setProfitAvg(average(stock.getProfit1(), stock.getProfit3(), stock.getProfit5(), stock.getProfit10()));
			stock.setProfitAvg35(average35(stock.getProfit1(), stock.getProfit3(), stock.getProfit5()));
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
		return retVal;
	}

	public static String readDataBetween(Stock stock, String data, String stratTag, String endTag) {
		return readDataBetween(stock, data, stratTag, endTag, 0);
	}

	public static String readDataBetween(Stock stock, String data, String stratTag, String endTag, int ignorePos) {
		String retValue = "";
		if (data != null && data.indexOf(stratTag) > -1) {
			int start = data.indexOf(stratTag) + stratTag.length() + ignorePos;
			int end = data.indexOf(endTag, start);
			retValue = (data.substring(start, end));
		}
		log(stock + ": " + stratTag + ": " + retValue);
		return retValue;
	}

	public static Stream<String> getStockData(Stock stock) throws Exception {
		try {
			String fileName = cacheFolder + stock + ".html";
			return Files.lines(Paths.get(fileName), Charset.forName("Cp1252"));
		} catch (Exception e) {
			// Dont handle
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
		try {
			String fileName = cacheFolder + stock + ".html";
			FileTime lastModifiedTime = Files.getLastModifiedTime(Paths.get(fileName));
			long modified = lastModifiedTime.toInstant().getEpochSecond();
			long now = Instant.now().getEpochSecond();
			long diff = now - modified;
			long day = 5 * 24 * 60 * 60;
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
			log("update: " + update + ", File exists: " + exists);
			String str = downlaodData(url);
			if (str.indexOf("ViewConsolidated") > -1) {
				log("Consolidated balance sheet present.. downloading consolidated..");
				str = downlaodData(url + "/consolidated");
			}
			writeFile(fileName, str);
			log("Data downloaded for: " + stock);
		} else {
			log("Updated data already exists for : " + stock);
		}
	}

	public static String downlaodData(String url) {
		String str = downlaodUnparsedData(url);
		str = normalizeData(str);
		return str;
	}

	public static String downlaodUnparsedData(String url) {
		String str = "NA";
		try {
			log("Download: " + url);
			StringBuffer buff = new StringBuffer("");
			try (Scanner scanner = new Scanner(new URL(url).openStream())) {
				while (scanner.hasNext()) {
					buff.append(scanner.next());
				}
			}
			str = buff.toString();
		} catch (Exception e) {
			log("Got exception while downloaing data : " + url);
		}
		return str;
	}

	public static String normalizeData(String str) {
		str = str.replaceAll("</td><td>", "=");
		str = str.replaceAll("</td>", "=");
		str = str.replaceAll("</span>", "=");
		str = str.replaceAll("\\<[^>]*>", "");
		str = str.replaceAll("\\s+", "");
		str = str.replaceAll(",", "");
		str = str.replaceAll("\\u20b9", "=");
		str = str.replaceAll("==", "=");
		return str;
	}

	public static void writeFile(String fileName, String data) throws Exception {
		Files.write(Paths.get(fileName), data.getBytes());
	}

	public static void writeInCache(String fileName, String data) {
		try {
			Files.write(Paths.get(cacheFolder + fileName), data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Stream<String> getFromCache(String fileName) {
		try {
			return Files.lines(Paths.get(cacheFolder + fileName), Charset.forName("Cp1252"));
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
				if (Character.isDigit(ch) || ".".equals(ch)) {
					temp += ch;
				}
			}
			data = temp;
		}
		return data;
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
			if (line != null) {
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
		int avg = 0;
		int cnt = 0;
		int total = 0;

		int iprice1 = Utils.toInt(price1);
		int iprice3 = Utils.toInt(price3);
		int iprice5 = Utils.toInt(price5);
		int iprice10 = Utils.toInt(price10);
		if (iprice1 > 0) {
			total += iprice1 * 1;
			cnt += 1;
		}
		if (iprice3 > 0) {
			total += iprice3 * 3;
			cnt += 3;
		}
		if (iprice5 > 0) {
			total += iprice5 * 5;
			cnt += 5;
		}
		if (iprice10 > 0) {
			total += iprice10 * 10;
			cnt += 10;
		}

		if (cnt > 0) {
			avg = total / cnt;
			log("Total-Count-Avg: " + total + "/" + cnt + "=" + avg);
		}
		return avg;
	}

	public static void log(String data) {
		try {
			if(!DEBUG) {
				data = data + "\n";
				Files.write(Paths.get(logFileName), data.getBytes(), StandardOpenOption.APPEND);
			} else {
				System.out.println(data);
			}
		} catch (IOException e) {
			System.out.println("Error while writing to log file : " + logFileName);
		}
	}
	
	public static void logClean() {
		try {
			Files.write(Paths.get(logFileName), "\n".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.out.println("Error while writing to log file : " + logFileName);
		}
	}
}
