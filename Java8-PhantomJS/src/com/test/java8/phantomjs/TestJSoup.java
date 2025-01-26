package com.test.java8.phantomjs;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TestJSoup {
	public static void main(String[] args) throws Exception {

		String url = "https://chartink.com/screener/daily-rsi-below-30-stocks";

		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				System.out.println("Inside if");
				desktop.browse(new URI(url));
				desktop.addAppEventListener(null);
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				System.out.println("Inside else");
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}