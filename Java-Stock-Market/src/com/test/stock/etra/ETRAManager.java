package com.test.stock.etra;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.test.stock.utils.MultiValueMap;
import com.test.stock.utils.Utils;

public class ETRAManager {
	private static Map<String, String> unprocessedFiles = new HashMap<>();

	public static void main(String[] args) throws Exception {
		System.out.println("ETRAManager:: Starting...");
		String baseDir = getBaseDirectory(args);
		String dataFile = Utils.ensureFile(baseDir, Constants.INPUT_DATA_FILE);
		MultiValueMap<String, String> data = Utils.readDuplicateData(dataFile);
		Map<String, File> filesMap = Utils.readFilesMap(baseDir, Constants.PDF);
		processData(baseDir, data, filesMap);
		Utils.printUnprocessedMap(unprocessedFiles);
		System.out.println("ETRAManager:: Processing Done.");
	}

	private static void processData(String outDir, MultiValueMap<String, String> data, Map<String, File> filesMap) {
		data.entryList().stream().forEach(e -> {
			String oldFileName = e.getKey();
			String newFileName = e.getValue();
			File file = filesMap.get(oldFileName);
			if (file != null && file.exists() && newFileName != null) {
				File newFile = new File(outDir, newFileName + Constants.PDF);
				Utils.copyFile(file, newFile);
			} else {
				unprocessedFiles.put(oldFileName, newFileName);
			}
		});
	}

	private static String getBaseDirectory(String[] args) {
		if (args.length > 0) {
			File file = new File(args[0]);
			if (file.exists()) {
				String path = file.getAbsolutePath();
				System.out.println("Processing data from: " + path);
				return path;
			}
		}
		System.out.println("Please enter valid directory as args.");
		System.exit(0);
		return "";
	}
}
