package com.test.stock.screener;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.test.stock.screener.main.ScreenerLinksFetcher;
import com.test.stock.screener.main.StockCSVGenerator;
import com.test.stock.screener.main.StockCSVGenerator_Specific;
import com.test.stock.screener.main.StockDataDownloader;
import com.test.stock.screener.main.StockDataDownloader_Specific;
import com.test.stock.screener.main.StocksMetadataDownloader;
import com.test.stock.screener.main.base.AbstractScreener;
import com.test.stock.screener.vo.Program;

public class MainRunner {
	private static List<Program> programsList = new ArrayList<>();

	private static void addPrograms() throws Exception {
		addProgram(new StockDataDownloader());
		addProgram(new StockDataDownloader_Specific());
		addProgram(new StocksMetadataDownloader());
		addProgram(new StockCSVGenerator());
		addProgram(new StockCSVGenerator_Specific());
		addProgram(new ScreenerLinksFetcher());
	}

	public static void main(String[] args) throws Exception {
		addPrograms();
		System.out.println("Welcome to Screener Main RUnner...");
		Scanner scanner = new Scanner(System.in);
		System.out.println("Which program you want to run: ");
		programsList.forEach(System.out::println);
		int index = scanner.nextInt()-1;
		scanner.close();
		Program program = programsList.get(index);
		if (program == null) {
			program = programsList.get(0);
		}
		program.getScreener().run();
	}

	private static void addProgram(AbstractScreener screener) throws Exception {
		int size = programsList.size() + 1;
		String name = screener.getName();
		programsList.add(new Program(size, name, screener));
	}

}
