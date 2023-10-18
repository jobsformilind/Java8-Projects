package com.test.stock.screener;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.test.stock.utils.Counter;

public class ScreenerStockReader {
	static Set<Stock> failedStocks = new HashSet<>();
	static String searchMapFile = ScreenerUtils.getStocksHomeDir() + "searchMap.txt";
	static String inFile = ScreenerUtils.getStocksHomeDir() + "input.txt";
	static String outFile = ScreenerUtils.getStocksHomeDir() + "output.csv";
	static boolean update = false;
	static int updateDays = -1;

	static int cagr;
	static int roe;
	static int profit;

	public static void main(String[] args) throws Exception {
		ScreenerStockReader reader = new ScreenerStockReader();
		reader.checkArgs(args);
		reader.initStockReader();
		reader.getDataForAllSymbols();
		ScreenerURLUtils.cleanupTempFiles();
	}

	private void initStockReader() throws Exception {
		ScreenerURLUtils.loadSearchURLMap(searchMapFile);		
	}

	public void getDataForAllSymbols() throws Exception {
		Set<Stock> stocksSet = ScreenerURLUtils.getStockSymbols(inFile, update, updateDays);
		readStockMetaData(stocksSet);
		//downloadStockData(stocksSet);
		//verifyStockData(stocksSet);
		//analyzeStockData(stocksSet);
		//getDataForAllSymbols(stocksSet);
	}

	private static void readStockMetaData(Set<Stock> stocksSet) {
		ScreenerURLUtils.log("------------------------------");
		Counter.initCounter(stocksSet.size());
		stocksSet.forEach(s -> {
			Counter.getCounter().currentIncrease();			
			ScreenerURLUtils.log("");
			if(ScreenerURLUtils.underProcess(s)) {
				ScreenerURLUtils.log("Stock is already under process...: " + s);
			} else {
				ScreenerURLUtils.updateJsonStock(s);
				ScreenerUtils.sleepRandomly();
			}
		});
		ScreenerURLUtils.log("------------------------------");		
	}

	private static void getDataForAllSymbols(Set<Stock> stocksSet) throws Exception {
		stocksSet = checkCAGRRatio(stocksSet);
		stocksSet = checkROERatio(stocksSet);
		stocksSet = checkProfitRatio(stocksSet);

		String data = stocksSet.stream().sorted().map(s -> s.getCSV()).collect(Collectors.joining("\n"));
		data = Stock.getCSVHeader() + data;
		ScreenerURLUtils.writeFile(outFile, data);
	}

	private static Set<Stock> checkProfitRatio(Set<Stock> stocksSet) {
		return stocksSet.stream().filter(s -> s.getCagrAvg() >= profit).collect(Collectors.toSet());
	}

	private static Set<Stock> checkROERatio(Set<Stock> stocksSet) {
		return stocksSet.stream().filter(s -> s.getCagrAvg() >= roe).collect(Collectors.toSet());
	}

	private static Set<Stock> checkCAGRRatio(Set<Stock> stocksSet) {
		return stocksSet.stream().filter(s -> s.getCagrAvg() >= cagr).collect(Collectors.toSet());
	}

	private static synchronized void analyzeStockData(Set<Stock> stockSet) {
		ScreenerURLUtils.log("------------------------------");
		ScreenerURLUtils.log("Starting stocks data analysis");
		Counter.initCounter(stockSet.size());
		stockSet.stream().sorted().forEach(s -> {
			Counter.getCounter().currentIncrease();
			analyzeStockData(s);
			ScreenerURLUtils.log("");
		});
		ScreenerURLUtils.log("Stocks Data analysis is completed.");
		if(failedStocks.size()>0) {
			ScreenerURLUtils.log("Error in Stocks: " + failedStocks);
		}
		ScreenerURLUtils.log("------------------------------");
	}
	public static synchronized Stock analyzeStockData1(Stock stock) {
		try {
			String data = ScreenerURLUtils.readDataFromStart(stock, "QuarterlyResults", "NetProfit=", "=EPSinRs");
			data = ScreenerURLUtils.readDataBetweenUsingEndTag(data, "=", "=EPSinRs");
			
			data = ScreenerURLUtils.readDataFromStart(stock, "BSE:", "=");
			data = ScreenerURLUtils.readDataBetween(data, "BSE:", "=");
			
			data = ScreenerURLUtils.readDataFromEnd(stock, "=EPSinRs=", "=DividendPayout%=");
			data = ScreenerURLUtils.readDataBetweenUsingEndTag(data, "=", "=DividendPayout%=");
		} catch (Exception e) {
			stock.setError(e.getMessage());
			failedStocks.add(stock);
			ScreenerUtils.handleException(e);
		}
		return stock;
	}
	private static synchronized Stock analyzeStockData(Stock stock) {
		ScreenerURLUtils.log("--------------Analyze stock: " + stock);
		try {
			stock.setHi3y(ScreenerURLUtils.readDataBetween(stock, "High/Low=", "="));
			stock.setName(ScreenerURLUtils.readDataBetween(stock, "name=", "="));
			stock.setCmp(ScreenerURLUtils.readDataBetween(stock, "CurrentPrice=", "="));
			stock.setFaceValue(ScreenerURLUtils.extractNumber(ScreenerURLUtils.readTag(stock, "FaceValue")));
			stock.setBseSymbol(ScreenerURLUtils.readDataBetween(stock, "BSE:", "="));
			stock.setNseSymbol(ScreenerURLUtils.readDataBetween(stock, "NSE:", "="));
			stock.setSector(ScreenerURLUtils.extractText(ScreenerURLUtils.readDataBetween(stock, "Sector:", "=")));
			
			stock.setMarketCap(ScreenerURLUtils.readDataBetween(stock, "=MarketCap=", "="));
			stock.setEPS(ScreenerURLUtils.readDataBetweenUsingEndTag(stock, "=", "=DividendPayout%="));
			stock.setMedianPE(ScreenerURLUtils.readDataBetween(stock, "MedianPE=", "="));
			stock.setPE(ScreenerURLUtils.readDataBetween(stock, "StockPEarn=", "="));

			String cagrData = ScreenerURLUtils.readDataBetween(stock, "StockPriceCAGR", "ReturnonEquity");
			ScreenerURLUtils.parseCAGR(stock, cagrData);

			String roeData = ScreenerURLUtils.readDataBetween(stock, "ReturnonEquity", "BalanceSheet");
			ScreenerURLUtils.parseROE(stock, roeData);

			String profitData = ScreenerURLUtils.readDataBetween(stock, "CompoundedProfitGrowth", "StockPriceCAGR");
			ScreenerURLUtils.parseProfit(stock, profitData);

			String salesData = ScreenerURLUtils.readDataBetween(stock, "CompoundedSalesGrowth", "CompoundedProfitGrowth");
			ScreenerURLUtils.parseSales(stock, salesData);

			checkNSESymbol(stock, "NSE-SM:");
			checkNSESymbol(stock, "NSE-RR:");
			checkNSESymbol(stock, "NSE-BE:");
			checkNSESymbol(stock, "NSE-BZ:");

			ScreenerURLUtils.log(stock.getCSV());
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
		return stock;
	}

	private static synchronized void verifyStockData(Set<Stock> stocksSet) {
		ScreenerURLUtils.log("------------------------------");
		ScreenerURLUtils.log("Starting verification of stocks data");
		Set<Stock> filteredSet = stocksSet.stream().filter(ScreenerURLUtils::notExists).sorted().collect(Collectors.toSet());
		Counter.initCounter(filteredSet.size());
		filteredSet.stream().sorted().forEach(s -> {
			ScreenerURLUtils.log("-");
			Counter.getCounter().currentIncrease();
			verifyStockData(s);
		});
		ScreenerURLUtils.log("Stocks Data verification is completed.");
		ScreenerURLUtils.log("------------------------------");
	}

	private static void verifyStockData(Stock stock) {
		if (ScreenerURLUtils.notExists(stock)) {
			ScreenerURLUtils.log("File not exists. Downloading file for: " + stock);
			downloadStockData(stock, true);
		} else {
			ScreenerURLUtils.log("File exists. Ignoring for: " + stock);
		}
	}

	private static synchronized void checkNSESymbol(Stock stock, String prefix) throws Exception {
		if (ScreenerUtils.isEmpty(stock.getNseSymbol())) {
			stock.setNseSymbol(ScreenerURLUtils.readDataBetween(stock, prefix, "="));
		}
		if (ScreenerUtils.isNotEmpty(stock.getNseSymbol())) {
			stock.setNseSymbol(stock.getNseSymbol().replaceAll("&amp;", "&"));
		}
	}

	private static void downloadStockData(Set<Stock> symbolsSet) {
		ScreenerURLUtils.log("------------------------------");
		Counter.initCounter(symbolsSet.size());
		symbolsSet.stream().filter(Objects::nonNull).filter(p -> Boolean.valueOf(update)).sorted().forEach(s -> {
			Counter.getCounter().currentIncrease();			
			ScreenerURLUtils.log("");
			if(ScreenerURLUtils.underProcess(s)) {
				ScreenerURLUtils.log("Stock is already under process...: " + s);
			} else {
				downloadStockData(s, update);
			}
		});
		ScreenerURLUtils.log("------------------------------");
	}

	public static void checkForceUpdate(Stock stock) {
		if(updateDays >= 0) {
			boolean needsUpdate = ScreenerURLUtils.needsUpdate(stock, updateDays);
		}
	}

	private static synchronized void downloadStockData(Stock stock, boolean update) {
		try {
			ScreenerURLUtils.getStockHtml(stock, update, 3000);
		} catch (Exception e) {
			ScreenerUtils.handleException(e);
		}
	}


	private void checkArgs(String[] args) throws Exception {
		ScreenerUtils.log("Run Command: screener -u -ud=2 ");
		ScreenerUtils.log("-u = Update ");
		ScreenerUtils.log("-ud = No of Update Days ");
		if (args.length > 0) {
			for (String command : args) {
				ScreenerUtils.log("Processing command: {} ", command);
				if (ScreenerUtils.startsWith(command, "-ud=")) {
					updateDays = ScreenerUtils.toInt(command.substring(4));
				} else if (ScreenerUtils.startsWith(command, "-u")) {
					update = true;
				}
			}
			updateDays = 10;
			update = true;
			ScreenerUtils.log("Starting with below parameters: ");
			ScreenerURLUtils.log("update: " + update);
			ScreenerURLUtils.log("updateDays: " + updateDays);
			ScreenerURLUtils.log("");
		}
	}

}
