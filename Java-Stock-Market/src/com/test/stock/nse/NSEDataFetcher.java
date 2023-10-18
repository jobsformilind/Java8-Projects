package com.test.stock.nse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NSEDataFetcher {

	private static String NSE_URL = "https://www.nseindia.com/api/quote-equity?symbol=";

	public static void main(String[] args) {
		System.out.println(getStockData("INFY"));
	}

	public static String getStockData(String symbol) {
		StringBuffer buff = new StringBuffer();
		try {
			URL url = new URL(NSE_URL + symbol);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("scheme", "https");			
			conn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
			conn.setRequestProperty("accept-language", "en-US,en;q=0.9,mr;q=0.8");
			conn.setRequestProperty("cookie", "_ga=GA1.2.1815520817.1593487441;");
			conn.setRequestProperty("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"90\", \"Google Chrome\";v=\"90\"");
			conn.setRequestProperty("sec-fetch-dest", "document");
			conn.setRequestProperty("upgrade-insecure-requests", "1");
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36");
			conn.setRequestProperty("symbol", symbol);

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				buff.append(output);
			}
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buff.toString();
	}

	public static String getNSEData(String symbol) {
		StringBuffer buff = new StringBuffer("");
		String url = NSE_URL + symbol;
		System.out.println("Trying to connect  " + url);
		try (Scanner scanner = new Scanner(new URL(url).openStream())) {
			while (scanner.hasNext()) {
				String line = scanner.next();
				System.out.println("Got response: " + line);
				buff.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buff.toString();
	}

}
