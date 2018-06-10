package com.test.threads.collections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapExample {

	public static void main(String[] args) {
		Map<String, String> myMap = getConcurrentHashMap();
		System.out.println("ConcurrentHashMap before iterator: " + myMap);
		Iterator<String> it = myMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (key.equals("3"))
				myMap.put(key + "new", "3new");
		}
		System.out.println("ConcurrentHashMap after iterator: " + myMap);

		myMap = getHashMap();
		System.out.println("HashMap before iterator: " + myMap);
		Iterator<String> it1 = myMap.keySet().iterator();
		while (it1.hasNext()) {
			String key = it1.next();
			if (key.equals("3")) {
					myMap.put(key + "new", "3new");
			}
		}
		System.out.println("HashMap after iterator: " + myMap);
	}

	private static Map<String, String> getHashMap() {
		return addItems(new HashMap<String, String>());
	}

	private static Map<String, String> getConcurrentHashMap() {
		return addItems(new ConcurrentHashMap<String, String>());
	}

	private static Map<String, String> addItems(Map<String, String> myMap) {
		myMap.put("1", "1");
		myMap.put("2", "1");
		myMap.put("3", "1");
		myMap.put("4", "1");
		myMap.put("5", "1");
		myMap.put("6", "1");
		return myMap;
	}

}