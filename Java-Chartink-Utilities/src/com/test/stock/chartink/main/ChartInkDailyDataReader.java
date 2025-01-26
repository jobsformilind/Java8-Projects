package com.test.stock.chartink.main;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.test.stock.chartink.pojo.LinkData;
import com.test.stock.chartink.pojo.Stock;
import com.test.stock.chartink.utils.Constants;
import com.test.stock.chartink.utils.Utils;

public class ChartInkDailyDataReader implements Constants {
	protected static DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy - hh:mm a");

	public static void main(String[] args) throws Exception {
		//LogManager.setSaveLogs(true);
		LogManager.setDisplayLogs(true);
		runChartInkScreener();
		
	}

	private static void runChartInkScreener() throws IOException, Exception {
		LogManager.addLog("====================================================");
		LogManager.addLog("Starting ChartInkDailyDataReader: " + formatter.format(new Date()));
		List<String> chartlinkData = Utils.getDataFromFile(chartinkFile);
		List<LinkData> dataList = Utils.toLinkData(chartlinkData);
		dataList.forEach(ChartInkScreener::screenStocks);
		dataList.stream().map(LinkData::getStocks).forEach(s->{
			s.stream().forEach(System.out::println);
		});
		LogManager.log();
	}
	
	
	protected static void saveStocks(List<LinkData> dataList) {
		dataList.forEach(data -> {
			String stocks = data.getStocks().stream().filter(Stock::isSaving).map(Stock::getSymbol)
					.collect(Collectors.joining("\n"));
			Utils.writeFile(data.getDataFile(), stocks);
		});
	}


}
