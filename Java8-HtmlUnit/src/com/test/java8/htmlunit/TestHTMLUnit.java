package com.test.java8.htmlunit;

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;

public class TestHTMLUnit {
	public static void main(String[] args) throws Exception {
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		//webClient.getOptions().setJavaScriptEnabled(false);
	    //webClient.getOptions().setCssEnabled(false);

		HtmlPage page = webClient.getPage("https://chartink.com/screener/daily-rsi-below-30-stocks");
		System.out.println(page.getTitleText());
		System.out.println(page.getVisibleText());
		
		webClient.close();
	}
}