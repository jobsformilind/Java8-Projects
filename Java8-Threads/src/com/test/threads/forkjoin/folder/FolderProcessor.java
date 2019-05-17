package com.test.threads.forkjoin.folder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("serial")
public class FolderProcessor extends RecursiveTask<List<String>> {
	private final String path;
	private final String extension;

	public FolderProcessor(String path, String extension) {
		this.path = path;
		this.extension = extension;
	}

	@Override
	protected List<String> compute() {
		//System.out.println(FolderProcessor.getCurrentThreadName() + "Processing path: " + path);
		List<String> list = new ArrayList<>();
		File file = new File(path);
		Optional<File[]> content = Optional.ofNullable(file.listFiles());
		Stream<File> dirStream = content.map(Arrays::stream).orElse(Stream.empty());
		List<FolderProcessor> dirs = dirStream.filter(f -> f.isDirectory()).map(d -> new FolderProcessor(d.getAbsolutePath(), extension)).collect(Collectors.toList());
		ForkJoinTask.invokeAll(dirs).stream().map(ForkJoinTask::join).flatMap(List::stream).collect(Collectors.toCollection(() -> list));

		Stream<File> fileStream = content.map(Arrays::stream).orElse(Stream.empty());
		fileStream.filter(f -> f.isFile() && checkFile(f)).map(File::getAbsolutePath).collect(Collectors.toCollection(() -> list));
		return list;
	}

	private boolean checkFile(File file) {
		FolderForkJoinTestClass.filesScanned.incrementAndGet();
		return file.getName().endsWith(extension);
	}

	private List<FolderProcessor> createDividedSubtasks() {
		File file = new File(path);
		Optional<File[]> content = Optional.ofNullable(file.listFiles());
		Stream<File> fileStream = content.map(Arrays::stream).orElse(Stream.empty());
		return fileStream.filter(f -> f.isDirectory()).map(d -> new FolderProcessor(d.getAbsolutePath(), extension)).collect(Collectors.toList());
	}

	public static String getCurrentThreadName() {
		return Thread.currentThread().getName() + ": ";
	}
}
