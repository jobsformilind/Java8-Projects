package com.test.stock.screener;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.test.stock.screener.meta.Counter;
import com.test.stock.screener.meta.Stock;
import com.test.stock.screener.utils.URLUtils;
import com.test.stock.screener.utils.Utils;

public class SpecificStockDataDownloader {

	public static void main(String[] args) throws Exception {
		URLUtils.init();
		Set<Stock> stocksSet = URLUtils.getSpecificStocksToProcess();
		Runtime.getRuntime().addShutdownHook(new DownloaderThread(stocksSet));
		downloadStockData(stocksSet);
		handleFailedStocks(stocksSet);
		URLUtils.cleanupTempFiles();
	}

	private static void downloadStockData(Set<Stock> stocksSet) {
		Utils.log("------------------------------");
		Counter.initCounter(stocksSet.size());
		stocksSet.stream().filter(Objects::nonNull).sorted().forEach(s -> {
			Counter.getCounter().currentIncrease();
			Utils.log("--------------");
			if (URLUtils.underProcess(s)) {
				Utils.log("Stock is already under process...: " + s);
			} else {
				URLUtils.downloadStockHTML(s);
			}
		});
		Utils.log("------------------------------");
	}

	private static void handleFailedStocks(Set<Stock> stocksSet) {
		List<Stock> failed = stocksSet.stream().filter(Stock::isDownloadFailed).collect(Collectors.toList());
		Utils.printCollection(failed, "Below stock download failed: ");
	}

	private static class DownloaderThread extends Thread {
		private Set<Stock> stocksSet = null;

		public DownloaderThread(Set<Stock> stocksSet) {
			this.stocksSet = stocksSet;
		}
		
		public void run() {
			URLUtils.cleanupTempFiles();
			handleFailedStocks(stocksSet);
		}
	}
}
