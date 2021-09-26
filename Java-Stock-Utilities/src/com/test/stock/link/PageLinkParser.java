package com.test.stock.link;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.test.stock.utils.URLUtils;

public class PageLinkParser {
	private static String url = "https://www.screener.in/company/compare/00000062/?sort=Current+Price&order=desc&limit=25&page=";
	private static int pages = 13;
	private static String part = "ZSTART";
	private static int partLength = part.length();
	private static List<String> symbols = new ArrayList<>();
	static String stocksDir = "D:\\Temp-Stocks\\";
	static String outFile = stocksDir + "in.txt";

	public static void main(String[] args) throws Exception {
		for (int i = 1; i <= pages; i++) {
			Thread.sleep(5000);
			processPageData(String.valueOf(i), url+i);
		}
		String data = symbols.stream().sorted().collect(Collectors.joining("\n"));
		System.out.println("SYmbols: " + symbols.size());
		URLUtils.writeFile(outFile, data);
	}
	
	
	private static synchronized void processPageData(String pageNo, String url) {
		String data = URLUtils.downlaodUnparsedData(url);
		data = normalizeData(data);
		//System.out.println("---"+data);
		boolean breakMe = true;
		while(breakMe) {
			int start = data.indexOf(part);
			//System.out.println("start: "+start);
			if(start>0) {
				int end = data.indexOf("ZEND", start+partLength);
				//System.out.println(end);
				String name = data.substring(start+partLength, end);
				symbols.add(name);
				data = data.substring(end);
			} else {
				breakMe = false;
			}
		}
	}
	
	public static String normalizeData(String str) {
		str = str.replaceAll("/company/", "ZSTART");
		str = str.replaceAll("/", "ZEND");
		str = str.replaceAll("ZSTARTcompareZEND", "");		
		str = str.replaceAll("\\s+", "");
		str = str.replaceAll(",", "");
		str = str.replaceAll("\\u20b9", "=");
		str = str.replaceAll("==", "=");
		return str;
	}

}
