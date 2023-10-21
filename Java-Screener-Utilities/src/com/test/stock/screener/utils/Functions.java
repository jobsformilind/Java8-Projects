package com.test.stock.screener.utils;

import java.util.function.Function;

public class Functions {
	public static Function<String, String> TO_PAGED_URL = s -> s + "?page=";

}
