package com.test.stock.screener;

import java.util.Set;
import java.util.stream.Collectors;

import com.test.stock.screener.meta.Counter;
import com.test.stock.screener.meta.Stock;
import com.test.stock.screener.utils.URLUtils;
import com.test.stock.screener.utils.Utils;

public class StockDataCSVGenerator {
	private static int filter_cagr = 0;
	private static int filter_roe = 0;
	private static int filter_profit = 0;

	public static void main(String[] args) throws Exception {
		URLUtils.init();
		Set<Stock> stocksSet = URLUtils.getStocksToProcess();
		Runtime.getRuntime().addShutdownHook(new CSVGeneratorThread(stocksSet));
		analyzeAllStockData(stocksSet);
		generateCSVFile(stocksSet);
		URLUtils.cleanupTempFiles();
	}

	private static void generateCSVFile(Set<Stock> stocksSet) throws Exception {
		stocksSet = filterByCAGRRatio(stocksSet, filter_cagr);
		stocksSet = filterByROERatio(stocksSet, filter_roe);
		stocksSet = filterByProfitRatio(stocksSet, filter_profit);

		String data = stocksSet.stream().filter(Stock::isCached).sorted().map(s -> s.getCSV()).collect(Collectors.joining("\n"));
		data = Stock.getCSVHeader() + data;
		URLUtils.writeFile(URLUtils.outFile, data);
	}

	private static Set<Stock> filterByProfitRatio(Set<Stock> stocksSet, int filter_profit) {
		return stocksSet.stream().filter(s -> s.getCagrAvg() >= filter_profit).collect(Collectors.toSet());
	}

	private static Set<Stock> filterByROERatio(Set<Stock> stocksSet, int filter_roe) {
		return stocksSet.stream().filter(s -> s.getCagrAvg() >= filter_roe).collect(Collectors.toSet());
	}

	private static Set<Stock> filterByCAGRRatio(Set<Stock> stocksSet, int filter_cagr) {
		return stocksSet.stream().filter(s -> s.getCagrAvg() >= filter_cagr).collect(Collectors.toSet());
	}

	private static void analyzeAllStockData(Set<Stock> stockSet) {
		Utils.log("------------------------------");
		Utils.log("Starting stocks data analysis");
		Utils.log("");
		Counter.initCounter(stockSet.size());
		stockSet.stream().sorted().forEach(s -> {
			Counter.getCounter().currentIncrease();
			if(s.isCached()) {
				Utils.log("--------------");
				extractSingleStockData(s);
			}
		});
		Utils.log("Stocks Data analysis is completed.");
		Utils.log("------------------------------");
	}
	
	private static Stock extractSingleStockData(Stock stock) {
		Utils.log("Analyzing stock data :{ " + Counter.getCounter() + " }: " + stock);		
		try {
			stock.setHi3y(URLUtils.readDataBetween(stock, "Hi3y", "High/Low=", "="));
			stock.setName(URLUtils.readDataBetween(stock, "Name", "name=", "="));
			stock.setCmp(URLUtils.readDataBetween(stock, "CMP", "CurrentPrice=", "="));
			stock.setFaceValue(URLUtils.extractNumber(URLUtils.readTag(stock, "FaceValue", "FaceValue")));
			stock.setBseSymbol(URLUtils.readDataBetween(stock, "Symbol", "BSE:", "="));
			stock.setNseSymbol(URLUtils.readDataBetween(stock, "NSESymbol", "NSE:", "="));
			stock.setSector(URLUtils.extractText(URLUtils.readDataBetween(stock, "Sector", "Sector:", "=")));
			
			stock.setMarketCap(URLUtils.readDataBetween(stock, "MarketCap", "=MarketCap=", "="));
			stock.setEPS(URLUtils.readDataBetweenUsingEndTag(stock, "EPS", "=", "=DividendPayout%="));
			stock.setMedianPE(URLUtils.readDataBetween(stock, "M-PE", "MedianPE=", "="));
			stock.setPE(URLUtils.readDataBetween(stock, "PE", "StockPEarn=", "="));

			String cagrData = URLUtils.readDataBetween(stock, "CAGR_DATA", "StockPriceCAGR", "ReturnonEquity");
			URLUtils.parseCAGR(stock, cagrData);

			String roeData = URLUtils.readDataBetween(stock, "ROE_DATA", "ReturnonEquity", "BalanceSheet");
			URLUtils.parseROE(stock, roeData);

			String profitData = URLUtils.readDataBetween(stock, "PROFIT_DATA", "CompoundedProfitGrowth", "StockPriceCAGR");
			URLUtils.parseProfit(stock, profitData);

			String salesData = URLUtils.readDataBetween(stock, "SALES_DATA", "CompoundedSalesGrowth", "CompoundedProfitGrowth");
			URLUtils.parseSales(stock, salesData);

			checkNSESymbol(stock, "NSE-SM:");
			checkNSESymbol(stock, "NSE-RR:");
			checkNSESymbol(stock, "NSE-BE:");
			checkNSESymbol(stock, "NSE-BZ:");
		} catch (Exception e) {
			Utils.handleException(e);
		}
		return stock;
	}

	private static void checkNSESymbol(Stock stock, String prefix) throws Exception {
		if (Utils.isEmpty(stock.getNseSymbol())) {
			stock.setNseSymbol(URLUtils.readDataBetween(stock, "NSE SYmbol", prefix, "="));
		}
		if (Utils.isNotEmpty(stock.getNseSymbol())) {
			stock.setNseSymbol(stock.getNseSymbol().replaceAll("&amp;", "&"));
		}
	}

	private static class CSVGeneratorThread extends Thread {
		private Set<Stock> stocksSet = null;

		public CSVGeneratorThread(Set<Stock> stocksSet) {
			this.stocksSet = stocksSet;
		}

		public void run() {
			URLUtils.cleanupTempFiles();
			if (stocksSet != null && stocksSet.size() > 0) {
				System.out.println("CSV Generator was killed....");
			}
		}
	}
}
