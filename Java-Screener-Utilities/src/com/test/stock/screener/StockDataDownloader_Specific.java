package com.test.stock.screener;

import java.util.Set;

import com.test.stock.screener.base.AbstractStockDataDownloader;
import com.test.stock.screener.meta.Stock;
import com.test.stock.screener.utils.URLUtils;

public class StockDataDownloader_Specific extends AbstractStockDataDownloader {

	public static void main(String[] args) throws Exception {
		StockDataDownloader_Specific downloader = new StockDataDownloader_Specific();
		downloader.downloadStockData();
	}

	@Override
	protected Set<Stock> getStocksToProcess() throws Exception {
		return URLUtils.getSpecificStocksToProcess();
	}

}
