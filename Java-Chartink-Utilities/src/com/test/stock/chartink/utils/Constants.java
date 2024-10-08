package com.test.stock.chartink.utils;

public interface Constants {
	String DIR_DATA_DIR = Utils.getStocksHomeDir() + "data\\";
	String chartinkFile = DIR_DATA_DIR + "chartink_input.properties";
	String chartinkIntradayFile = DIR_DATA_DIR + "chartink_input_intraday.properties";
	String logFileName = Utils.getStocksHomeDir() + "chartink_logs.log";
}
