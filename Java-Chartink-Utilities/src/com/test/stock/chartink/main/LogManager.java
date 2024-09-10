package com.test.stock.chartink.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.test.stock.chartink.utils.Utils;

public class LogManager {
	private static List<String> logs = new ArrayList<>();
	private static boolean displayLogs;
	private static boolean saveLogs;

	public static void log() {
		if (displayLogs) {
			Utils.log(logs.stream().collect(Collectors.joining("\n")), saveLogs);
		}
	}

	public static void setDisplayLogs(boolean showLogs) {
		displayLogs = showLogs;
	}

	public static void setSaveLogs(boolean saveLogs) {
		LogManager.saveLogs = saveLogs;
	}

	public static void addLog(String logString) {
		logs.add(logString);
	}
}
