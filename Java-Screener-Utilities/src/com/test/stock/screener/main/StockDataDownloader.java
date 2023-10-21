package com.test.stock.screener.main;

import java.util.Set;

import com.test.stock.screener.data.Stock;
import com.test.stock.screener.main.base.AbstractStockDataDownloader;
import com.test.stock.screener.utils.URLUtils;

public class StockDataDownloader extends AbstractStockDataDownloader {

	public StockDataDownloader() {
		setName("Stock Data Downloader");
	}

	public static void main(String[] args) throws Exception {
		new StockDataDownloader().run();
	}

	@Override
	protected Set<Stock> getStocksToProcess() throws Exception {
		return URLUtils.getStocksToProcess();
	}
}
