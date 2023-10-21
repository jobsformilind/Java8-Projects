package com.test.stock.screener.main;

import java.util.Set;

import com.test.stock.screener.data.Stock;
import com.test.stock.screener.main.base.AbstractStockDataDownloader;
import com.test.stock.screener.utils.URLUtils;

public class StockDataDownloader_Specific extends AbstractStockDataDownloader {

	public StockDataDownloader_Specific() {
		setName("Stock Data Downloader Specific");
	}

	@Override
	protected Set<Stock> getStocksToProcess() throws Exception {
		return URLUtils.getSpecificStocksToProcess();
	}

}
