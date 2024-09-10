package com.test.stock.chartink.main;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.stock.chartink.pojo.LinkData;
import com.test.stock.chartink.pojo.Stock;
import com.test.stock.chartink.utils.Constants;
import com.test.stock.chartink.utils.Utils;

public class ChartInkScreener implements Constants {
	protected static DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy - hh:mm a");

	public static void main(String[] args) throws Exception {
		runChartInkScreener();
	}

	private static void runChartInkScreener() throws IOException, Exception {
		LogManager.addLog("====================================================");
		LogManager.addLog("Starting ChartInkScreener: " + formatter.format(new Date()));
		List<String> chartlinkData = Utils.getDataFromFile(chartinkFile);
		List<LinkData> dataList = Utils.toLinkData(chartlinkData);
		dataList.forEach(ChartInkScreener::screenStocks);
		Utils.validateWithExistingLinkData(dataList);
		saveAndPrintStocks(dataList);
		LogManager.log();
	}

	protected static void saveAndPrintStocks(List<LinkData> dataList) {
		dataList.forEach(data -> {
			String stocks = data.getStocks().stream().filter(Stock::isSaving).map(Stock::getSymbol)
					.collect(Collectors.joining("\n"));
			Utils.writeFile(data.getDataFile(), stocks);

			String newSymbols = data.getStocks().stream().filter(Stock::isMarkedNew).map(Stock::getSymbol).collect(Collectors.joining("\n"));
			String removedSymbols = data.getStocks().stream().filter(Stock::isMarkedRemoval).map(Stock::getSymbol).collect(Collectors.joining("\n"));
			//String keptSymbols = data.getStocks().stream().filter(Stock::isMarkedKeep).map(Stock::getSymbol).collect(Collectors.joining("\n"));
			boolean hasData = (Utils.isNotEmpty(newSymbols) || Utils.isNotEmpty(removedSymbols));
			if(hasData) {
				LogManager.setDisplayLogs(true);
				LogManager.addLog("Screener Name : " + data.getLinkName());
				logSymbols(newSymbols, "==Stocks Added:==");
				logSymbols(removedSymbols, "==Stocks Removed:==");
				//logSymbols(keptSymbols, "==Stocks Kept:==");
				LogManager.addLog("====================================================");
			}
		});
	}

	public static void screenStocks(LinkData linkData) {
		HttpClient httpClient = getHttpClient();
		try {
			String postUrl = "https://chartink.com/screener/process";
			List<String> details = getAuthDetails();
			String csrfToken = details.get(0);
			String ciSession = details.get(1);

			HttpPost httpPost = new HttpPost(postUrl);

			// Set the JSON body
			StringEntity entity = new StringEntity(linkData.getLinkClause());
			httpPost.setEntity(entity);

			// Set headers
			httpPost.addHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
			httpPost.addHeader("x-csrf-token", csrfToken);
			httpPost.addHeader("cookie", "ci_session=" + ciSession);

			try {
				HttpResponse response = httpClient.execute(httpPost);
				String responseBody = EntityUtils.toString(response.getEntity());

				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonResultObject = objectMapper.readTree(responseBody);
				JsonNode jsonArrayNode = jsonResultObject.get("data");

				if (jsonArrayNode.isArray()) {
					for (JsonNode jsonNode : jsonArrayNode) {
						String name = jsonNode.get("name").asText();
						String nsecode = jsonNode.get("nsecode").asText();
						String bsecode = jsonNode.get("bsecode").asText();
						Stock stock = new Stock(name, nsecode, bsecode);
						linkData.addStock(stock);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> getAuthDetails() throws Exception {
		List<String> details = new ArrayList<>();
		HttpGet request = new HttpGet("https://chartink.com/screener/process");
		HttpResponse response = getHttpClient().execute(request);
		String responseBody = EntityUtils.toString(response.getEntity());
		Document doc = Jsoup.parse(responseBody);
		Elements metaTags = doc.getElementsByTag("meta");

		for (Element metaTag : metaTags) {
			String name = metaTag.attr("name");
			String content = metaTag.attr("content");

			if ("csrf-token".equals(name)) {
				details.add(content);
				break;
			}
		}
		Header[] cookieHeaders = response.getHeaders("Set-Cookie");
		for (Header header : cookieHeaders) {
			String[] cookieValues = header.getValue().split(";")[0].split("=");
			if (cookieValues.length == 2) {
				String cookieName = cookieValues[0].trim();
				String cookieValue = cookieValues[1].trim();
				if (cookieName.equals("ci_session")) {
					details.add(cookieValue);
				}
			}
		}
		return details;
	}

	private static void logSymbols(String symbols, String info) {
		if(Utils.isNotEmpty(symbols)) {
			LogManager.addLog(info);
			LogManager.addLog(symbols);
		}
	}

	private static HttpClient getHttpClient() {
		HttpClient client = HttpClients.custom()
				.setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
		return client;
	}
}
