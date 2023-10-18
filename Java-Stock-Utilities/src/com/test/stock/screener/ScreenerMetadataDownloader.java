package com.test.stock.screener;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ScreenerMetadataDownloader {
	static String searchMapFile = ScreenerUtils.getStocksHomeDir() + "symbols_searchMap.txt";
	static String inputFile = ScreenerUtils.getStocksHomeDir() + "symbols_input.txt";
	static String metadataFile = ScreenerUtils.getStocksHomeDir() + "symbols_metadata.json";

	public static void main(String[] args) throws Exception {
		initScreenerMetadataDownloader();
		List<JsonStock> oldStocks = getHistoricalStockMetaData();
		Set<Stock> newStocks = ScreenerURLUtils.getStockSymbols(inputFile);
		updateHistoricalStocks(newStocks, oldStocks);
		readStockMetaData(newStocks, oldStocks);
		saveStockMetaData(newStocks);
	}

	private static void updateHistoricalStocks(Set<Stock> stocksSet, List<JsonStock> stockMetaData) {
		Set<String> metaStocks = stockMetaData.parallelStream().map(s -> s.getSymbol()).collect(Collectors.toSet());
		Set<String> stocks = stocksSet.parallelStream().map(s -> s.getSymbol()).collect(Collectors.toSet());
		metaStocks.stream().filter(t -> !stocks.contains(t)).forEach(m -> {
			stocksSet.add(new Stock(m));
		});
	}

	private static List<JsonStock> getHistoricalStockMetaData() throws Exception {
		String metadata = ScreenerUtils.getFullDataFromFile(metadataFile);
		List<JsonStock> jsonData = ScreenerURLUtils.GSON.fromJson(metadata, ScreenerURLUtils.CLAZZ);
		ScreenerUtils.log("Historical Stock MetaData read: {}", jsonData.size());
		return jsonData;
	}

	private static void saveStockMetaData(Set<Stock> stocksSet) {
		List<JsonStock> jsonDataList = stocksSet.stream().map(s -> s.getJsonStock()).collect(Collectors.toList());
		String jsonData = ScreenerURLUtils.GSON.toJson(jsonDataList);
		ScreenerURLUtils.writeFile(metadataFile, jsonData);
	}

	private static void readStockMetaData(Set<Stock> stocksSet, List<JsonStock> stockMetaData) {
		stocksSet.forEach(stock -> {
			ScreenerUtils.log("------------------------------");
			JsonStock jsonStock = getJsonData(stock, stockMetaData);
			if (jsonStock != null) {
				ScreenerURLUtils.updateStockMetaData(stock, jsonStock);
			} else {
				ScreenerURLUtils.updateJsonStock(stock);
				ScreenerUtils.sleepRandomly(5);
			}
		});
		ScreenerUtils.log("------------------------------");
	}

	private static JsonStock getJsonData(Stock stock, List<JsonStock> stockMetaData) {
		return stockMetaData.parallelStream().filter(m -> stock.getSymbol().equals(m.getSymbol())).findAny()
				.orElse(null);
	}

	private static void initScreenerMetadataDownloader() throws Exception {
		ScreenerURLUtils.loadSearchURLMap(searchMapFile);
	}
}
