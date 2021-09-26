package com.test.stock.csv;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.test.stock.utils.Utils;

public class DuplicateRemover {
	private static String stocksDir = "D:\\Temp-Stocks\\";
	private static String dupsInFile = stocksDir + "dups.txt";
	private static String dupsOutFile = stocksDir + "dupsOut.txt";

	public static void main(String[] args) throws Exception {
		removeDuplicateLines();
	}

	private static void removeDuplicateLines() throws Exception {
		List<String> dataList = Utils.getDataFromFile(dupsInFile);
		Map<String, Integer> dataMap = dataList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(e -> 1)));
		System.out.println(dataMap);
		dataMap.entrySet().stream().filter(e -> e.getValue() <= 1).forEach(e->{
			System.out.println(e);
		});
		List<String> list = dataMap.entrySet().stream().filter(e -> e.getValue() <= 1).map(p -> p.getKey()).collect(Collectors.toList());
		//list.stream().forEach(System.out::println);
		String data = list.stream().sorted().collect(Collectors.joining("\n"));
		Utils.writeFile(dupsOutFile, data);
	}

}
