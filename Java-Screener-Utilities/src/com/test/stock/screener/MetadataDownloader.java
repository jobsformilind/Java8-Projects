package com.test.stock.screener;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.test.stock.screener.meta.Counter;
import com.test.stock.screener.meta.JsonStock;
import com.test.stock.screener.meta.Stock;
import com.test.stock.screener.utils.URLUtils;
import com.test.stock.screener.utils.Utils;

public class MetadataDownloader {

	public static void main(String[] args) throws Exception {
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

	private static void readStockMetaData(Set<Stock> stocksSet) {
		Counter.initCounter(stocksSet.size());
		stocksSet.forEach(stock -> {
			Counter.getCounter().currentIncrease();
			Utils.log("------------------------------: {}", Counter.getCounter());
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

	private static class MetaDataThread extends Thread {
		private Set<Stock> stocksSet = null;

		public MetaDataThread(Set<Stock> stocksSet) {
			this.stocksSet = stocksSet;
		}

		public void run() {
			URLUtils.cleanupTempFiles();
			if (stocksSet != null && stocksSet.size() > 0) {
				MetadataDownloader.saveStockMetaData(stocksSet);
			}
		}
	}
}
