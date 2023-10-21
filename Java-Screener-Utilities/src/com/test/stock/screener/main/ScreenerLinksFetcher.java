package com.test.stock.screener.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.test.stock.screener.main.base.AbstractScreener;
import com.test.stock.screener.utils.Functions;
import com.test.stock.screener.utils.URLUtils;
import com.test.stock.screener.utils.Utils;
import com.test.stock.screener.vo.Constants;

public class ScreenerLinksFetcher extends AbstractScreener implements Constants {

	public ScreenerLinksFetcher() {
		setName("Screen Links Fetcher");
	}

	public void run() throws Exception {
		URLUtils.init();
		List<String> urlsData = Utils.getDataFromFile(URLUtils.urlFile);
		List<String> allUrlsList = urlsData.stream().map(Functions.TO_PAGED_URL).collect(Collectors.toList());
		allUrlsList.forEach(u -> {
			try {
				String data = downlaodData(u);
				extractSymbols(u, data);
			} catch (Exception e) {
				Utils.handleException(e);
			}
		});
	}

	private void extractSymbols(String url, String data) {
		List<String> symbols = new ArrayList<>();
		int startIndex = data.indexOf(Constants.SUFFIX_COMPANY);
		while (startIndex > 0) {
			data = data.substring(startIndex + Constants.SUFFIX_COMPANY.length());
			int endIndex = data.indexOf("/");
			String symbol = data.substring(0, endIndex);
			symbols.add(symbol);
			startIndex = data.indexOf(Constants.SUFFIX_COMPANY);
		}
		Utils.log("-------------: {}", symbols.size());
		Utils.log("URL: {}", url);
		symbols.stream().sorted().distinct().forEach(System.out::println);
	}

	private String downlaodData(String url) throws Exception {
		int pages = 1;
		StringBuffer buff = new StringBuffer("");
		for (int i = 1; i <= pages; i++) {
			String downloadURL = url + i;
			StringBuffer inData = URLUtils.downlaodUnparsedData(downloadURL);
			buff.append(inData);
			if (i == 1) {
				pages = getPages(inData);
			}
			Utils.sleepSilently(5000);
		}
		String data = buff.toString();
		return data;
	}

	private int getPages(StringBuffer buff) {
		int pages = 1;
		String data = buff.toString();
		int start = data.indexOf("page1of");
		if (start > 0) {
			String substring = data.substring(start + 7, start + 10);
			pages = Utils.extractNumber(substring);
		}
		return pages;
	}
}
