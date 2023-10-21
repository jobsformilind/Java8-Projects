package com.test.stock.screener.main.base;

import java.util.Set;
import java.util.stream.Collectors;

import com.test.stock.screener.data.Counter;
import com.test.stock.screener.data.Stock;
import com.test.stock.screener.utils.URLUtils;
import com.test.stock.screener.utils.Utils;

public abstract class AbstractCSVGenerator extends AbstractScreener {
	private int filter_cagr = 0;
	private int filter_roe = 0;
	private int filter_profit = 0;

	protected abstract Set<Stock> getStocksToProcess() throws Exception;

	public void run() throws Exception {		
		generateCSVFile();
	}

	protected void generateCSVFile() throws Exception {
		URLUtils.init();
		Set<Stock> stocksSet = getStocksToProcess();
		Runtime.getRuntime().addShutdownHook(new CSVGeneratorThread(stocksSet));
		analyzeAllStockData(stocksSet);
		generateCSVFile(stocksSet);
		URLUtils.cleanupTempFiles();
	}

	private void generateCSVFile(Set<Stock> stocksSet) throws Exception {
		stocksSet = filterByCAGRRatio(stocksSet, filter_cagr);
		stocksSet = filterByROERatio(stocksSet, filter_roe);
		stocksSet = filterByProfitRatio(stocksSet, filter_profit);

		String data = stocksSet.stream().filter(Stock::isCached).sorted().map(s -> s.getCSV())
				.collect(Collectors.joining("\n"));
		data = Stock.getCSVHeader() + data;
		URLUtils.writeFile(URLUtils.outFile, data);
	}

	private Set<Stock> filterByProfitRatio(Set<Stock> stocksSet, int filter_profit) {
		return stocksSet.stream().filter(s -> s.getCagrAvg() >= filter_profit).collect(Collectors.toSet());
	}

	private Set<Stock> filterByROERatio(Set<Stock> stocksSet, int filter_roe) {
		return stocksSet.stream().filter(s -> s.getCagrAvg() >= filter_roe).collect(Collectors.toSet());
	}

	private Set<Stock> filterByCAGRRatio(Set<Stock> stocksSet, int filter_cagr) {
		return stocksSet.stream().filter(s -> s.getCagrAvg() >= filter_cagr).collect(Collectors.toSet());
	}

	private void analyzeAllStockData(Set<Stock> stockSet) {
		Utils.log("------------------------------");
		Utils.log("Starting stocks data analysis");
		Utils.log("");
		Counter.initCounter(stockSet.size());
		stockSet.stream().sorted().forEach(s -> {
			Counter.getCounter().currentIncrease();
			if (s.isCached()) {
				Utils.log("--------------");
				extractSingleStockData(s);
			}
		});
		Utils.log("Stocks Data analysis is completed.");
		Utils.log("------------------------------");
	}

	private Stock extractSingleStockData(Stock stock) {
		Utils.log("Analyzing stock data :{ " + Counter.getCounter() + " }: " + stock);
		try {
			stock.setHi3y(URLUtils.readDataBetween(stock, "Hi3y", "High/Low=", "="));
			stock.setName(URLUtils.readDataBetween(stock, "Name", "name=", "="));
			stock.setCmp(URLUtils.readDataBetween(stock, "CMP", "CurrentPrice=", "="));
			stock.setFaceValue(URLUtils.extractNumber(URLUtils.readTag(stock, "FaceValue", "FaceValue")));
			stock.setSector(URLUtils.extractText(URLUtils.readDataBetween(stock, "Sector", "Sector:", "=")));

			stock.setMarketCap(URLUtils.readDataBetween(stock, "MarketCap", "=MarketCap=", "="));
			stock.setEPS(URLUtils.readDataBetweenUsingEndTag(stock, "EPS", "=", "=DividendPayout%="));
			stock.setMedianPE(URLUtils.readDataBetween(stock, "M-PE", "MedianPE=", "="));
			stock.setPE(URLUtils.readDataBetween(stock, "PE", "StockPEarn=", "="));

			String cagrData = URLUtils.readDataBetween(stock, "CAGR_DATA", "StockPriceCAGR", "ReturnonEquity");
			URLUtils.parseCAGR(stock, cagrData);

			String roeData = URLUtils.readDataBetween(stock, "ROE_DATA", "ReturnonEquity", "BalanceSheet");
			URLUtils.parseROE(stock, roeData);

			String profitData = URLUtils.readDataBetween(stock, "PROFIT_DATA", "CompoundedProfitGrowth",
					"StockPriceCAGR");
			URLUtils.parseProfit(stock, profitData);

			String salesData = URLUtils.readDataBetween(stock, "SALES_DATA", "CompoundedSalesGrowth",
					"CompoundedProfitGrowth");
			URLUtils.parseSales(stock, salesData);

			String highestPrice = URLUtils.readHighestPrice(stock);
			stock.setHi3y("0".equals(highestPrice) ? stock.getHi3y() : highestPrice);
		} catch (Exception e) {
			stock.setFailed();
			Utils.handleException(e);
		}
		return stock;
	}

	private class CSVGeneratorThread extends Thread {
		private Set<Stock> stocksSet = null;

		public CSVGeneratorThread(Set<Stock> stocksSet) {
			this.stocksSet = stocksSet;
		}

		public void run() {
			URLUtils.cleanupTempFiles();
			URLUtils.handleFailedStocks(stocksSet);
		}
	}
}
