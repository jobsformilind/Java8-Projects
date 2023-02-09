package com.test.stock.screener;

import java.util.ArrayList;
import java.util.List;

import com.test.stock.utils.URLUtils;
import com.test.stock.utils.Utils;

public class ScreenerFetcher {
	static String mainURL  = "https://www.screener.in/screens/97687/capacity-expansion/";
	
	private static String url  = mainURL + "?page=";
	private static String start = "/company/";
	private static int startLength = start.length();
	
	public static void main(String[] args) throws Exception {
		String data = getData();
		extractSymbols(data);
	}

	private static void extractSymbols(String data) {
		List<String> symbols = new ArrayList<>();
		int startIndex = data.indexOf(start);
		while(startIndex>0) {
			data = data.substring(startIndex+startLength);
			int endIndex = data.indexOf("/");
			String symbol = data.substring(0, endIndex);
			symbols.add(symbol);
			startIndex = data.indexOf(start);
		}
		
		System.out.println("------------- : " + symbols.size());
		symbols.stream().sorted().distinct().forEach(System.out::println);
	}

	private static String getData() {
		int pages = 1;
		StringBuffer buff = new StringBuffer("");
		for (int i = 1; i <= pages; i++) {
			String downloadURL = url + i;
			StringBuffer inData = URLUtils.downlaodUnparsedData(downloadURL);
			buff.append(inData);
			if(i==1) {
				pages = getPages(inData);
			}
			Utils.sleepSilently(5000);
		}
		String data = buff.toString();
		return data;
	}


	private static int getPages(StringBuffer buff) {
		int pages = 1;
		String data = buff.toString();
		int start = data.indexOf("page1of");
		if(start > 0) {
			String substring = data.substring(start+7, start+10);
			pages = Utils.extractNumber(substring);
		}
		return pages;
	}
}
