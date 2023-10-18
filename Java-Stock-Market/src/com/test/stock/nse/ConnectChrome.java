package com.test.stock.nse;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ConnectChrome {
	private static String dataFile = "C:\\Users\\MilindHP\\Downloads\\rsi\\data.txt";
	
	public static void main(String[] args) throws Exception {
		WebDriver driver = getWebDriver();
		Set<String> symbolsSet = readSymbols();
		symbolsSet.stream().forEach(s->getPrice(driver, s));
		driver.quit();
	}

	
	
	public static Set<String> readSymbols() throws Exception {
		Set<String> symbolsSet = new TreeSet<>();
		Stream<String> stream = Files.lines(Paths.get(dataFile));
		stream.filter(Objects::nonNull).forEach(line -> {
			List<String> collect = Stream.of(line.split(",")).collect(Collectors.toList());
			symbolsSet.addAll(collect);
		});
		stream.close();
		System.out.println("Read Old Symbols: " + symbolsSet);
		return symbolsSet;

	}

	public static String getPrice(WebDriver driver, String symbol) {
		String price = "-1";
		try {
			driver.get("https://www.nseindia.com/get-quotes/equity?symbol=" + symbol);
			Thread.sleep(2000);
			WebElement quote = driver.findElement(By.id("quoteLtp"));
			price = quote.getText().replaceAll(",", "");
			System.out.println(symbol + ": " + price);
		} catch (Exception e) {
		}
		return price;
	}

	public static WebDriver getWebDriver() {
		System.setProperty("webdriver.chrome.driver", "D:/Installed/Utils/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("excludeSwitches", Arrays.asList("disable-popup-blocking"));
		options.addArguments("--disable-blink-features=AutomationControlled");
		WebDriver driver = new ChromeDriver(options);
		driver.manage().window().setPosition(new Point(1000, 1000));
		return driver;
	}
}
