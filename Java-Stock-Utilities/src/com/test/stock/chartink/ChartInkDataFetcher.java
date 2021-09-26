package com.test.stock.chartink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ChartInkDataFetcher {
	private static String PROCESS_URL = "https://chartink.com/screener/process";

	public static void main(String[] args) throws Exception {
		// String data =
		// URLUtils.downlaodData("https://chartink.com/screener/copy-volume-spike-on-1-minute-candle-5007");
		// System.out.println(data);
		downloadChartInkData();
		//String csrf = getCSRFToken();
	}

	public static void downloadChartInkData() throws Exception {
		Map<String, String> headers = getRequestHeaders();
		URL url = new URL(PROCESS_URL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		con.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
		con.setRequestProperty("Referer", "https://chartink.com/screener/copy-volume-spike-on-1-minute-candle-5007");
		headers.entrySet().forEach(e->{
			con.setRequestProperty(e.getKey(), e.getValue());
		});
		con.setDoOutput(true);
		String request = "scan_clause=(+%7Bcash%7D+(+%5B0%5D+5+minute+volume+%3E+%5B-1%5D+5+minute+sma(+volume%2C1000+)+*+10+)+)+";
		try (OutputStream os = con.getOutputStream()) {
			byte[] input = request.getBytes("utf-8");
			os.write(input, 0, input.length);
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			System.out.println(response.toString());
		}
	}

	public static Map<String, String> getRequestHeaders() throws Exception {
		Map<String, String> headers = new HashMap<String, String>();
		URL url = new URL("https://chartink.com/screener/copy-volume-spike-on-1-minute-candle-5007");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.connect();
		Map<String, List<String>> headerFields = con.getHeaderFields();
		List<String> list = headerFields.get("Set-Cookie");
		Optional<String> cookie = list.stream().filter(t->t.contains("XSRF-TOKEN")).findFirst();
		headers.put("set-cookie", cookie.get());
		try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			response = new StringBuilder(response.toString().replaceAll("\\s+", ""));
			int start = response.indexOf("metaname=\"csrf-token\"");
			int end = response.indexOf("meta", start + 5);
			if (start > 0 && end > 0) {
				String csrf = response.substring(start + 30, end - 4);
				headers.put("", csrf);
			}
		}
		return headers;
	}



}
