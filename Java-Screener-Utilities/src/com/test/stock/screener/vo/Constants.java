package com.test.stock.screener.vo;

import com.test.stock.screener.utils.Utils;

public interface Constants {
	static String SUFFIX_MEDIAN_PE = "/chart/?q=Price+to+Earning-Median+PE-EPS&days=1825";
	static String SUFFIX_PRICE = "/chart/?q=Price&days=1095";
	static String SUFFIX_CONSOLIDATED = "&consolidated=true";
	static String SUFFIX_COMPANY = "/company/";
	static String URL_BASE = "https://www.screener.in";
	static String URL_COMPANY = "https://www.screener.in/company/";
	static String URL_API = "https://www.screener.in/api/company/";
	static String URL_INTRADAY = "https://intradayscreener.com/api/TechnicalAnaysis/stocksnapshot/";
	static String URL_SEARCH = "https://www.screener.in/api/company/search/?q=";
	static String DIR_CACHE = Utils.getStocksHomeDir() + "tmp\\cache\\";
	static String DIR_SER_CACHE = Utils.getStocksHomeDir() + "tmp\\cache_tmp\\";
	static String DIR_RAW_CACHE = Utils.getStocksHomeDir() + "tmp\\cache_raw\\";
	static String LOG_ERROR = "ERRROR>>: ";

}
