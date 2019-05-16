package com.test.threads.forkjoin.user;

public class User {
	private String name;
	private String status;

	public User(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [name=").append(name).append(", status=").append(status).append("]");
		return builder.toString();
	}

}
