package com.test.stock;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.test.stock.utils.Counter;
import com.test.stock.utils.URLUtils;
import com.test.stock.utils.Utils;

public class StockDataReader {
	static Set<Stock> failedStocks = new HashSet<>();
	static String inFile = Utils.getStocksHomeDir() + "in.txt";
	static String nseFile = Utils.getStocksHomeDir() + "nse.csv";
	static String bseFile = Utils.getStocksHomeDir() + "bse.csv";
	static String outFile = Utils.getStocksHomeDir() + "out.csv";
	static String singleFile = Utils.getStocksHomeDir() + "single.csv";
	static String symbol = null;
	static boolean update = false;
	static boolean dryrun = false;
	static int updateDays = -1;
	static boolean forceUpdate = false;
	static boolean downloadOnly = false;
	static int cagr;
	static int roe;
	static int profit;
	static long sleep = 3000;
	public static void main1(String[] args) throws Exception {
		update = true;
		downloadOnly = true;
		while(true) {
			Set<Stock> stocksSet = getStockSymbols();
			String collect = stocksSet.stream().limit(5).map(s->s.toString()).collect(Collectors.joining(","));;
			System.out.println(collect);
			Thread.sleep(500);
		}
	}

	public static void main(String[] args) throws Exception {
		URLUtils.logClean();
		checkArgs(args);
		if (symbol != null) {
			getDataForSingleSymbol(symbol);
		} else if (downloadOnly) {
			updateDataForAllSymbols();
		} else if (dryrun) {
			for(int i=0;i<10;i++) {
				Set<Stock> stocksSet = getStockSymbols();
				stocksSet.stream().limit(10).forEach(s->{
					System.out.print(s+", ");
				});
				System.out.println();
			}
		} else {
			getDataForAllSymbols();
		}
		URLUtils.cleanupTempFiles();
	}

	private static void checkArgs(String[] args) throws Exception {
		URLUtils.log("Run Command: screener -f -D -u=2 -s=TCS\n");
		if (args.length > 0) {
			for (String command : args) {
				URLUtils.log("Processing command: " + command);
				if (Utils.startsWith(command, "-f")) {
					update = true;
					forceUpdate = true;
				} else if (Utils.startsWith(command, "-run")) {
					dryrun = true;
				} else if (Utils.startsWith(command, "-D")) {
					update = true;
					downloadOnly = true;
				} else if (Utils.startsWith(command, "-s=")) {
					symbol = command.substring(3);
					update = true;
				} else if (Utils.startsWith(command, "-sleep=")) {
					sleep = Utils.toInt(command.substring(7));
				} else if (Utils.startsWith(command, "-u=")) {
					updateDays = Utils.toInt(command.substring(3));
				}
			}
			URLUtils.log("Starting with below parameters: ");
			URLUtils.log("symbol: " + symbol);
			URLUtils.log("update: " + update);
			URLUtils.log("downloadOnly: " + downloadOnly);
			URLUtils.log("sleep: " + sleep);
			URLUtils.log("updateDays: " + updateDays);
			URLUtils.log("");
			Thread.sleep(sleep);
		}
	}

	public static void updateDataForAllSymbols() throws Exception {
		Set<Stock> stocksSet = getStockSymbols();
		downloadStockData(stocksSet);
		verifyStockData(stocksSet);
	}

	public static void getDataForAllSymbols() throws Exception {
		Set<Stock> stocksSet = getStockSymbols();
		readStockMetaData(stocksSet);
		downloadStockData(stocksSet);
		verifyStockData(stocksSet);
		analyzeStockData(stocksSet);
		getDataForAllSymbols(stocksSet);
	}

	private static void readStockMetaData(Set<Stock> stocksSet) {
		// TODO Auto-generated method stub
		
	}

	public static void getDataForSingleSymbol(String symbol) throws Exception {
		Set<Stock> stocksSet = new HashSet<>();
		stocksSet.add(new Stock(symbol));
		downloadStockData(stocksSet);
		verifyStockData(stocksSet);
		analyzeStockData(stocksSet);
		getDataForSingleSymbol(stocksSet, symbol);
	}

	private static void getDataForAllSymbols(Set<Stock> stocksSet) throws Exception {
		stocksSet = checkCAGRRatio(stocksSet);
		stocksSet = checkROERatio(stocksSet);
		stocksSet = checkProfitRatio(stocksSet);

		String data = stocksSet.stream().sorted().map(s -> s.getCSV()).collect(Collectors.joining("\n"));
		data = Stock.getCSVHeader() + data;
		URLUtils.writeFile(outFile, data);
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
		URLUtils.log("------------------------------");
		URLUtils.log("Starting stocks data analysis");
		Counter.initCounter(stockSet.size());
		stockSet.stream().sorted().forEach(s -> {
			Counter.getCounter().currentIncrease();
			StockDataReader.analyzeStockData(s);
			URLUtils.log("");
		});
		URLUtils.log("Stocks Data analysis is completed.");
		if(failedStocks.size()>0) {
			URLUtils.log("Error in Stocks: " + failedStocks);
		}
		URLUtils.log("------------------------------");
	}
	public static synchronized Stock analyzeStockData1(Stock stock) {
		try {
			String data = URLUtils.readDataFromStart(stock, "QuarterlyResults", "NetProfit=", "=EPSinRs");
			data = URLUtils.readDataBetweenUsingEndTag(data, "=", "=EPSinRs");
			
			data = URLUtils.readDataFromStart(stock, "BSE:", "=");
			data = URLUtils.readDataBetween(data, "BSE:", "=");
			
			data = URLUtils.readDataFromEnd(stock, "=EPSinRs=", "=DividendPayout%=");
			data = URLUtils.readDataBetweenUsingEndTag(data, "=", "=DividendPayout%=");
		} catch (Exception e) {
			stock.setError(e.getMessage());
			failedStocks.add(stock);
			Utils.handleException(e);
		}
		return stock;
	}
	private static synchronized Stock analyzeStockData(Stock stock) {
		URLUtils.log("--------------Analyze stock: " + stock);
		try {
			stock.setHi3y(URLUtils.readDataBetween(stock, "High/Low=", "="));
			stock.setName(URLUtils.readDataBetween(stock, "name=", "="));
			stock.setCmp(URLUtils.readDataBetween(stock, "CurrentPrice=", "="));
			stock.setFaceValue(URLUtils.extractNumber(URLUtils.readTag(stock, "FaceValue")));
			stock.setBseSymbol(URLUtils.readDataBetween(stock, "BSE:", "="));
			stock.setNseSymbol(URLUtils.readDataBetween(stock, "NSE:", "="));
			stock.setSector(URLUtils.extractText(URLUtils.readDataBetween(stock, "Sector:", "=")));
			
			stock.setMarketCap(URLUtils.readDataBetween(stock, "=MarketCap=", "="));
			stock.setEPS(URLUtils.readDataBetweenUsingEndTag(stock, "=", "=DividendPayout%="));
			stock.setMedianPE(URLUtils.readDataBetween(stock, "MedianPE=", "="));
			stock.setPE(URLUtils.readDataBetween(stock, "StockPEarn=", "="));

			String cagrData = URLUtils.readDataBetween(stock, "StockPriceCAGR", "ReturnonEquity");
			URLUtils.parseCAGR(stock, cagrData);

			String roeData = URLUtils.readDataBetween(stock, "ReturnonEquity", "BalanceSheet");
			URLUtils.parseROE(stock, roeData);

			String profitData = URLUtils.readDataBetween(stock, "CompoundedProfitGrowth", "StockPriceCAGR");
			URLUtils.parseProfit(stock, profitData);

			String salesData = URLUtils.readDataBetween(stock, "CompoundedSalesGrowth", "CompoundedProfitGrowth");
			URLUtils.parseSales(stock, salesData);

			checkNSESymbol(stock, "NSE-SM:");
			checkNSESymbol(stock, "NSE-RR:");
			checkNSESymbol(stock, "NSE-BE:");
			checkNSESymbol(stock, "NSE-BZ:");

			URLUtils.log(stock.getCSV());
		} catch (Exception e) {
			Utils.handleException(e);
		}
		return stock;
	}

	private static synchronized void verifyStockData(Set<Stock> stocksSet) {
		URLUtils.log("------------------------------");
		URLUtils.log("Starting verification of stocks data");
		Set<Stock> filteredSet = stocksSet.stream().filter(URLUtils::notExists).sorted().collect(Collectors.toSet());
		Counter.initCounter(filteredSet.size());
		filteredSet.stream().sorted().forEach(s -> {
			URLUtils.log("-");
			Counter.getCounter().currentIncrease();
			StockDataReader.verifyStockData(s);
		});
		URLUtils.log("Stocks Data verification is completed.");
		URLUtils.log("------------------------------");
	}

	private static void verifyStockData(Stock stock) {
		if (URLUtils.notExists(stock)) {
			URLUtils.log("File not exists. Downloading file for: " + stock);
			downloadStockData(stock, true);
		} else {
			URLUtils.log("File exists. Ignoring for: " + stock);
		}
	}

	private static synchronized void checkNSESymbol(Stock stock, String prefix) throws Exception {
		if (Utils.isEmpty(stock.getNseSymbol())) {
			stock.setNseSymbol(URLUtils.readDataBetween(stock, prefix, "="));
		}
		if (Utils.isNotEmpty(stock.getNseSymbol())) {
			stock.setNseSymbol(stock.getNseSymbol().replaceAll("&amp;", "&"));
		}
	}

	private static void downloadStockData(Set<Stock> symbolsSet) {
		URLUtils.log("------------------------------");
		Counter.initCounter(symbolsSet.size());
		symbolsSet.stream().filter(Objects::nonNull).filter(p -> Boolean.valueOf(update)).sorted().forEach(s -> {
			Counter.getCounter().currentIncrease();			
			URLUtils.log("");
			if(URLUtils.underProcess(s)) {
				URLUtils.log("Stock is already under process...: " + s);
			} else {
				StockDataReader.downloadStockData(s, update);
			}
		});
		URLUtils.log("------------------------------");
	}

	public static void checkForceUpdate(Stock stock) {
		if(updateDays >= 0) {
			boolean needsUpdate = URLUtils.needsUpdate(stock, updateDays);
			stock.setForceUpdate(needsUpdate);
		}
	}

	private static synchronized void downloadStockData(Stock stock, boolean update) {
		try {
			URLUtils.getStockHtml(stock, update, sleep);
		} catch (Exception e) {
			Utils.handleException(e);
		}
	}

	private static synchronized Set<Stock> getStockSymbols() throws Exception {
		Set<String> readSymbols = URLUtils.readSymbols(inFile);
		Set<Stock> symbolsSet = readSymbols.stream().map(Utils::trimToNull).filter(Objects::nonNull).map(Stock::new).sorted()
				.collect(Collectors.toSet());
		
		symbolsSet.stream().filter(Objects::nonNull).filter(p -> Boolean.valueOf(update)).sorted().forEach(s -> {
			s.setForceUpdate(forceUpdate);
			s.setDaysToUpdate(updateDays);
		});
		return symbolsSet;
	}

	private static void getDataForSingleSymbol(Set<Stock> stocksSet, String symbol) throws Exception {
		String data = stocksSet.stream().filter(p -> p.getSymbol().equalsIgnoreCase(symbol))
				.map(s -> s.getCSV()).collect(Collectors.joining("\n"));
		data = Stock.getCSVHeader() + data;
		URLUtils.log(data);
		URLUtils.writeFile(singleFile, data);
	}

}
