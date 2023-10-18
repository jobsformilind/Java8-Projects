package com.milind.stock.screener;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.stock.JsonStock;

@SuppressWarnings("rawtypes")
public class ScreenerDataReader {
	private static String stockDataURL = "https://intradayscreener.com/api/TechnicalAnaysis/stocksnapshot/";
	public static Gson gson = new Gson();
	private static Type clazz = new TypeToken<Map>(){}.getType();

	public static void main(String[] args) {
		StringBuffer stockData = getStockData(stockDataURL + "TARSONS");
		System.out.println(stockData);
		Map map = gson.fromJson(stockData.toString(), clazz);
		System.out.println(map);

	}
	
	private static StringBuffer getStockData(String url) {
		StringBuffer buff = new StringBuffer("");
		try {
			System.out.println("Download stock data: " + url);
			try (Scanner scanner = new Scanner(new URL(url).openStream())) {
				while (scanner.hasNext()) {
					buff.append(scanner.next());
				}
			}
		} catch (Exception e) {
			System.out.println("Got exception while downloaing data : " + url);
		}
		return buff;
	}
}
