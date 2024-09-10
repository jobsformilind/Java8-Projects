package com.test.stock.chartink.main;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.test.stock.chartink.pojo.LinkData;
import com.test.stock.chartink.utils.Utils;

public class ChartInkContinuousScreener extends ChartInkScreener {

	public static void main(String[] args) throws Exception {
		runChartInkScreener();
	}

	private static void runChartInkScreener() throws IOException, Exception {
		LogManager.addLog("====================================================");
		LogManager.addLog("Starting ChartInkScreener: " + formatter.format(new Date()));
		while (true) {		
			List<String> chartlinkData = Utils.getDataFromFile(chartinkFile);
			List<LinkData> dataList = Utils.toLinkData(chartlinkData);
			dataList.forEach(ChartInkScreener::screenStocks);
			Utils.validateWithExistingLinkData(dataList);
			saveAndPrintStocks(dataList);
			LogManager.log();
			Utils.sleepSilentlyMinutes(5);
		}
	}

}
