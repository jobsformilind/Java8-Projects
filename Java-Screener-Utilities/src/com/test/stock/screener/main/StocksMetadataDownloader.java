package com.test.stock.screener.main;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.test.stock.screener.data.Counter;
import com.test.stock.screener.data.JsonStock;
import com.test.stock.screener.data.Stock;
import com.test.stock.screener.main.base.AbstractScreener;
import com.test.stock.screener.utils.URLUtils;
import com.test.stock.screener.utils.Utils;

public class StocksMetadataDownloader extends AbstractScreener {

	public StocksMetadataDownloader() {
		setName("Stocks Metadata Downloader");
	}

	public void run() throws Exception {
		URLUtils.init();
		Set<Stock> stocksSet = URLUtils.getStocksToProcess();
		Runtime.getRuntime().addShutdownHook(new MetaDataThread(stocksSet));
		readStockMetaData(stocksSet);
		saveStockMetaData(stocksSet);
	}

	private static void saveStockMetaData(Set<Stock> stocksSet) {
		List<JsonStock> jsonDataList = stocksSet.stream().map(s -> s.getJsonStock()).filter(Objects::nonNull)
				.filter(t -> t.getId() > 0).collect(Collectors.toList());
		Utils.log("Saved metadata file for records : {}", jsonDataList.size());
		String jsonData = URLUtils.GSON.toJson(jsonDataList);
		URLUtils.writeFile(URLUtils.metadataFile, jsonData);
		Utils.log("Saved metadata file: {}", URLUtils.metadataFile);
	}

	private void readStockMetaData(Set<Stock> stocksSet) {
		Counter.initCounter(stocksSet.size());
		stocksSet.forEach(stock -> {
			Counter.getCounter().currentIncrease();
			Utils.log("------------------------------");
			Utils.log("Read stock meta data :{ " + Counter.getCounter() + " }: " + stock);
			JsonStock jsonStock = stock.getJsonStock();
			if (jsonStock != null) {
				URLUtils.updateStockMetaData(stock, jsonStock);
			} else {
				URLUtils.updateJsonStock(stock);
				Utils.sleepRandomly(5);
			}
		});
		Utils.log("------------------------------");
	}

	private class MetaDataThread extends Thread {
		private Set<Stock> stocksSet = null;

		public MetaDataThread(Set<Stock> stocksSet) {
			this.stocksSet = stocksSet;
		}

		public void run() {
			URLUtils.cleanupTempFiles();
			URLUtils.handleFailedStocks(stocksSet);
			if (stocksSet != null && stocksSet.size() > 0) {
				StocksMetadataDownloader.saveStockMetaData(stocksSet);
			}
		}
	}
}
