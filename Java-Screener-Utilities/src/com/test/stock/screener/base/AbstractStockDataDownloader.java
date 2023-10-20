package com.test.stock.screener.base;

import java.util.Objects;
import java.util.Set;

import com.test.stock.screener.meta.Counter;
import com.test.stock.screener.meta.Stock;
import com.test.stock.screener.utils.URLUtils;
import com.test.stock.screener.utils.Utils;

public abstract class AbstractStockDataDownloader {

	protected abstract Set<Stock> getStocksToProcess() throws Exception;

	protected void downloadStockData() throws Exception {
		URLUtils.init();
		Set<Stock> stocksSet = getStocksToProcess();
		Runtime.getRuntime().addShutdownHook(new DownloaderThread(stocksSet));
		downloadStockData(stocksSet);
		URLUtils.handleFailedStocks(stocksSet);
		URLUtils.cleanupTempFiles();
	}

	private void downloadStockData(Set<Stock> stocksSet) {
		Utils.log("------------------------------");
		Counter.initCounter(stocksSet.size());
		stocksSet.stream().filter(Objects::nonNull).sorted().forEach(s -> {
			Counter.getCounter().currentIncrease();
			Utils.log("------------------------------");
			Utils.log("Download stock data :{ " + Counter.getCounter() + " }: " + s);
			if (URLUtils.underProcess(s)) {
				Utils.log("Stock is already under process...: " + s);
			} else {
				URLUtils.downloadStockHTML(s);
			}
		});
		Utils.log("------------------------------");
	}

	private class DownloaderThread extends Thread {
		private Set<Stock> stocksSet = null;

		public DownloaderThread(Set<Stock> stocksSet) {
			this.stocksSet = stocksSet;
		}
		
		public void run() {
			URLUtils.cleanupTempFiles();
			URLUtils.handleFailedStocks(stocksSet);
		}
	}
}
