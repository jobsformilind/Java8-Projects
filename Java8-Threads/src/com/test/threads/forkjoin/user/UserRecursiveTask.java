package com.test.threads.forkjoin.user;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class UserRecursiveTask extends RecursiveTask<List<User>> {
	private List<User> users;

	public UserRecursiveTask(List<User> users) {
		this.users = users;
	}

	@Override
	protected List<User> compute() {
		if (users.size() > UserForkJoinTestClass.THRESHOLD) {
			return ForkJoinTask.invokeAll(createDividedSubtasks()).stream().map(ForkJoinTask::join).flatMap(List::stream).collect(Collectors.toList());
		} else {
			return processing(users);
		}
	}

	private List<UserRecursiveTask> createDividedSubtasks() {
		int size = users.size();
		int middle = size / 2;
		List<UserRecursiveTask> dividedTasks = new ArrayList<>();
		dividedTasks.add(new UserRecursiveTask(users.subList(0, middle)));
		dividedTasks.add(new UserRecursiveTask(users.subList(middle, size)));
		return dividedTasks;
	}

	private List<User> processing(List<User> users) {
		//System.out.println(UserForkJoinTestClass.getCurrentThreadName() + "Processing chunk of: " + users.size());
		users.parallelStream().forEach(u -> u.setStatus("PROCESSED"));
		return users;
	}
}
