package com.test.stock.rsi;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RSIDataReceiver implements Runnable {
	private static String logFile = "C:\\Users\\MilindHP\\Downloads\\rsi\\log.txt";
	private static String alertFile = "C:\\Users\\MilindHP\\Downloads\\rsi\\alert.txt";
	private static String dataFile = "C:\\Users\\MilindHP\\Downloads\\rsi\\data.txt";
	private static String htmlFile = "C:\\Users\\MilindHP\\Downloads\\rsi\\rsi.html";
	private static Set<String> newSymbolsSet = new TreeSet<>();
	private static Set<String> oldSymbolsSet = new TreeSet<>();
	private static ExecutorService executorService = Executors.newFixedThreadPool(5);
	private static int counter = 0;

	public static void main(String[] args) throws Exception {
		int minutes = 5;
		if (args.length > 0) {
			minutes = Integer.parseInt(args[0]);
		}
		while (true) {
			counter++;
			executorService.submit(new RSIDataReceiver());
			System.out.println("Waiting for next run: " + minutes + " mins");
			Thread.sleep(minutes * 60 * 1000);
			System.out.println("Next run started");
		}
	}

	@Override
	public void run() {
		try {
			appendFile(logFile, "---------------------------------------");
			readOldSymbols();
			readNewSymbols();
			compareAndGet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void compareAndGet() throws Exception {
		List<String> newSymbols = new ArrayList<>();
		newSymbolsSet.stream().filter(p -> !oldSymbolsSet.contains(p)).forEach(s->newSymbols.add(s));
		oldSymbolsSet.stream().filter(p -> !newSymbolsSet.contains(p)).forEach(s->newSymbols.add(s));
		
		appendFile(logFile, new Date() + ": Found Diff: " + getString(newSymbols));
		if (!newSymbols.isEmpty()) {
			writeFile(dataFile, String.join(",", newSymbolsSet));

			StringBuffer buff = new StringBuffer("Existing below RSI 42 Symbols: ");
			buff.append(String.join(",", oldSymbolsSet)).append("\n");
			buff.append("Found below RSI 42 Symbols: ");
			buff.append(String.join(",", newSymbols));
			writeFile(alertFile, buff.toString());
			System.out.println("alert: " + buff);
			new ProcessBuilder("Notepad.exe", alertFile).start();
		} else {
			writeFile(dataFile, String.join(",", newSymbolsSet));
		}
	}

	private static void writeFile(String fileName, String data) throws Exception {
		Files.write(Paths.get(fileName), data.getBytes());
	}

	private static void appendFile(String fileName, String data) throws Exception {
		data = data + "\n";
		StandardOpenOption openOption = (counter%50==0)?StandardOpenOption.TRUNCATE_EXISTING:StandardOpenOption.APPEND;
		Files.write(Paths.get(fileName), data.getBytes(), openOption);
	}

	private static void readOldSymbols() throws Exception {
		oldSymbolsSet.clear();
		Stream<String> stream = Files.lines(Paths.get(dataFile));
		stream.forEach(line -> {
			if (line != null) {
				List<String> collect = Stream.of(line.split(",")).collect(Collectors.toList());
				if (collect != null)
					oldSymbolsSet.addAll(collect);
			}
		});
		stream.close();
		System.out.println("Read Old Symbols: " + oldSymbolsSet);
		appendFile(logFile, new Date() + ": Read Old Symbols: " + getString(oldSymbolsSet));

	}

	private static void readNewSymbols() throws Exception {
		newSymbolsSet.clear();
		String delim = "href=\"/stocks/";
		Stream<String> stream = Files.lines(Paths.get(htmlFile));

		stream.forEach(line -> {
			if (line != null) {
				int len = delim.length();
				int cnt = line.indexOf(delim);
				while (cnt > 1) {
					int cnt2 = line.indexOf(".html", cnt);
					String symbol = line.substring(cnt + len, cnt2);
					newSymbolsSet.add(symbol);
					cnt = line.indexOf(delim, cnt + len);
				}
			}
		});
		stream.close();
		System.out.println("Read New Symbols: " + newSymbolsSet);
		appendFile(logFile, new Date() + ": Read New Symbols: " + getString(newSymbolsSet));
	}

	private static String getString(Collection<?> data) throws Exception {
		data = Optional.ofNullable(data).orElse(new ArrayList<>());
		return data.size() + ": " + data;
	}

}
