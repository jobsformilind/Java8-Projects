package com.test.stock.screener;

import java.util.Set;

import com.test.stock.screener.base.AbstractCSVGenerator;
import com.test.stock.screener.meta.Stock;
import com.test.stock.screener.utils.URLUtils;

public class StockCSVGenerator_Specific extends AbstractCSVGenerator {

	public static void main(String[] args) throws Exception {
		StockCSVGenerator_Specific generator = new StockCSVGenerator_Specific();
		generator.generateCSVFile();
	}

	@Override
	protected Set<Stock> getStocksToProcess() throws Exception {
		return URLUtils.getSpecificStocksToProcess();
	}

}
