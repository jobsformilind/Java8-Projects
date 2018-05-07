package com.test.java8.features;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TestJSONObject {
	public static void main(String[] args) {		
		JSONObject json = new JSONObject();
		json.put("firstName", "John");
		json.put("lastName", "Smith");
		json.put("age", 25);

		Map m = new LinkedHashMap(4);
		m.put("streetAddress", "21 2nd Street");
		m.put("city", "New York");
		m.put("state", "NY");
		m.put("postalCode", 10021);
		json.put("address", m);

		JSONArray ja = new JSONArray();
		m = new LinkedHashMap(2);
		m.put("type", "home");
		m.put("number", "212 555-1234");
		ja.add(m);

		m = new LinkedHashMap(2);
		m.put("type", "fax");
		m.put("number", "212 555-1234");

		ja.add(m);
		json.put("phoneNumbers", ja);

		System.out.println(json.toString());
		
		
	}
}
