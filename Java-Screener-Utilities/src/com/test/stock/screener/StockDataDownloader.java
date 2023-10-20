package com.test.stock.screener;

import java.util.Set;

import com.test.stock.screener.base.AbstractStockDataDownloader;
import com.test.stock.screener.meta.Stock;
import com.test.stock.screener.utils.URLUtils;

public class StockDataDownloader extends AbstractStockDataDownloader {

	public static void main(String[] args) throws Exception {
		StockDataDownloader downloader = new StockDataDownloader();
		downloader.downloadStockData();
	}

	@Override
	protected Set<Stock> getStocksToProcess() throws Exception {
		return URLUtils.getStocksToProcess();
	}

}