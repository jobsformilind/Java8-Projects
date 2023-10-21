package com.test.stock.screener.main;

import java.util.Set;

import com.test.stock.screener.data.Stock;
import com.test.stock.screener.main.base.AbstractCSVGenerator;
import com.test.stock.screener.utils.URLUtils;

public class StockCSVGenerator_Specific extends AbstractCSVGenerator {

	public StockCSVGenerator_Specific() {
		setName("Stock CSV Generator Specific");
	}

	protected Set<Stock> getStocksToProcess() throws Exception {
		return URLUtils.getSpecificStocksToProcess();
	}

}
