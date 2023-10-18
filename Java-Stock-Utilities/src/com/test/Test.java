package com.test;

public class Test {
	public static void main(String[] args) {
		log(null, null);
		log("ThIs is string", null);
		log("ThIs is string {}", "mystring");
		log("ThIs is {} string {}", "mystring", "mystring");
		log("ThIs is string {} {}", "mystring", "mystring");
	}

	public static void log(String logString, Object... args) {
		if (logString != null) {
			logString = logString.replace("{}", "%s");
			String str = String.format(logString, args);
			System.out.println(str);
		}
	}

}
