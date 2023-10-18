package com.test.stock.chartink;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChartInkScreener {
	public static void main(String[] args) throws Exception {

		List<String> scrips = screenStocks();
		List<String> scripsFiltered = filterStocksBasedOnFundamentals(scrips);

		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);
		String formattedDate = currentDate.format(formatter);

		String message = "*********" + formattedDate + "*********\n";
		String chartInkScrips = "";
		if (scrips.size() > 0) {
			for (int i = 0; i < scrips.size(); i++) {
				chartInkScrips = chartInkScrips + scrips.get(i) + "\n";
			}
		} else {
			chartInkScrips = "No stocks Found!!";
		}

		String screenerScrips = "";
		if (scripsFiltered.size() > 0) {
			for (int i = 0; i < scripsFiltered.size(); i++) {
				screenerScrips = screenerScrips + scripsFiltered.get(i) + "\n";
			}
		} else {
			screenerScrips = "No stocks Found!!";
		}

		message = message + "----Chartink---- \n" + chartInkScrips + "\n";
		message = message + "----Screener---- \n" + screenerScrips + "\n";
		message = message + "**********************************";

		System.out.println(message);
	}

	public static List<String> screenStocks() throws Exception {
		String postUrl = "https://chartink.com/screener/process";
		List<String> details = getAuthDetails();
		String csrfToken = details.get(0);
		String ciSession = details.get(1);
		List<String> scrips = new ArrayList<>();

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost httpPost = new HttpPost(postUrl);

			// Set the JSON body
			//String requestBody = "scan_clause=(+%7Bcash%7D+(+(+%7Bcash%7D+(+(+(+1+month+ago+close+%2B+1+month+ago+high+%2B+1+month+ago+low+)+%2F+3+-+(+(+1+month+ago+high+%2B+1+month+ago+low+)+%2F+2+)+%2B+(+1+month+ago+close+%2B+1+month+ago+high+%2B+1+month+ago+low+)+%2F+3+)+%3C+latest+close+and(+(+1+month+ago+close+%2B+1+month+ago+high+%2B+1+month+ago+low+)+%2F+3+-+(+(+1+month+ago+high+%2B+1+month+ago+low+)+%2F+2+)+%2B+(+1+month+ago+close+%2B+1+month+ago+high+%2B+1+month+ago+low+)+%2F+3+)+%3C+latest+open+and+latest+ema(+latest+close+%2C+21+)+%3C+latest+close+and+latest+ema(+latest+close+%2C+21+)+%3C+latest+open+and+latest+ema(+latest+close+%2C+30+)+%3C+latest+close+and+latest+ema(+latest+close+%2C+30+)+%3C+latest+open+and+latest+ema(+latest+close+%2C+50+)+%3C+latest+close+and+latest+ema(+latest+close+%2C+50+)+%3C+latest+open+and+latest+rsi(+14+)+%3E+60+and+quarterly+net+profit%2Freported+profit+after+tax+%3E+1+quarter+ago+net+profit%2Freported+profit+after+tax+and+1+quarter+ago+net+profit%2Freported+profit+after+tax+%3E+2+quarter+ago+net+profit%2Freported+profit+after+tax+and(+%7Bcash%7D+(+(+latest+close+-+(+(+1+month+ago+high+%2B+1+month+ago+low+)+%2F+2+)+%2F+latest+close+)+*+100+%3C+5+and(+latest+close+-+latest+ema(+latest+close+%2C+21+)+)+%2F+latest+close+*+100+%3C+5+and(+latest+close+-+latest+ema(+latest+close+%2C+30+)+)+%2F+latest+close+*+100+%3C+5+and(+latest+close+-+latest+ema(+latest+close+%2C+50+)+)+%2F+latest+close+*+100+%3C+5+)+)+and+yearly+debt+equity+ratio+%3C+1.5+and+quarterly+eps+after+extraordinary+items+basic+%3E+1+quarter+ago+eps+after+extraordinary+items+basic+and+1+quarter+ago+eps+after+extraordinary+items+basic+%3E+2+quarter+ago+eps+after+extraordinary+items+basic+and+yearly+net+sales+%3E+1+year+ago+net+sales+)+)+and(+(+1+month+ago+close+%2B+1+month+ago+high+%2B+1+month+ago+open+)+%2F+3+)+%3E+(+(+2+months+ago+close+%2B+2+months+ago+high+%2B+2+months+ago+low+)+%2F+3+)+)+)+";
			String requestBody = "scan_clause=(+%7Bcash%7D+(+(+%7Bcash%7D+(+latest+ema(+latest+close+%2C+20+)+%3E+latest+ema(+latest+close+%2C+50+)+and+1+day+ago++ema(+latest+close+%2C+20+)%3C%3D+1+day+ago++ema(+latest+close+%2C+50+)+or+latest+ema(+latest+close+%2C+50+)+%3E+latest+ema(+latest+close+%2C+100+)+and+1+day+ago++ema(+latest+close+%2C+50+)%3C%3D+1+day+ago++ema(+latest+close+%2C+100+)+)+)+and+market+cap+%3E%3D+1000+and+latest+close+%3E%3D+100+and+latest+close+%3C%3D+2000+)+)+";
			StringEntity entity = new StringEntity(requestBody);
			httpPost.setEntity(entity);

			// Set headers
			httpPost.addHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
			httpPost.addHeader("x-csrf-token", csrfToken);
			httpPost.addHeader("cookie", "ci_session=" + ciSession);

			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				String responseBody = EntityUtils.toString(response.getEntity());

				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonResultObject = objectMapper.readTree(responseBody);
				JsonNode jsonArrayNode = jsonResultObject.get("data");

				if (jsonArrayNode.isArray()) {
					for (JsonNode jsonNode : jsonArrayNode) {
						scrips.add(jsonNode.get("nsecode").asText());
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return scrips;
	}

	public static List<String> getAuthDetails() throws Exception {
		List<String> details = new ArrayList<>();
		org.apache.http.client.HttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet("https://chartink.com/screener/process");
		HttpResponse response = client.execute(request);
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

	public static List<String> filterStocksBasedOnFundamentals(List<String> scrips) {

		List<String> scripsFiltered = new ArrayList<>();
		List<Integer> ignoreRows = new ArrayList<>();
		ignoreRows.add(1);
		ignoreRows.add(2);
		ignoreRows.add(4);
		ignoreRows.add(5);
		ignoreRows.add(6);
		ignoreRows.add(7);
		ignoreRows.add(9);
		ignoreRows.add(11);
		ignoreRows.add(12);
		boolean status = true;

		for (int i = 0; i < scrips.size(); i++) {

			status = true;

			try {
				// Replace this with the URL of the HTML page you want to parse
				String url = "https://www.screener.in/company/" + scrips.get(i);

				// Fetch the HTML content from the URL
				Document document = Jsoup.connect(url).get();

				// Locate the table element based on its class or ID
				Element table = document.select("table.data-table").first();

				if (table != null) {
					// Select all rows within the table
					Elements rows = table.select("tr");

					// Loop through rows (skip header row if necessary)
					for (int j = 1; j < rows.size(); j++) {
						if (!ignoreRows.contains(j)) {
							Element row = rows.get(j);
							Elements columns = row.select("td"); // Select cells within the row

							// Fetch details from the columns
							String lastQuarterValue = columns.get(columns.size() - 1).text().replaceAll(",", "")
									.replaceAll("%", "");
							String secondLastQuarterValue = columns.get(columns.size() - 2).text().replaceAll(",", "")
									.replaceAll("%", "");

							lastQuarterValue = lastQuarterValue.equals("") ? "0" : lastQuarterValue;
							secondLastQuarterValue = secondLastQuarterValue.equals("") ? "0" : secondLastQuarterValue;

							double lastQuarterIntValue = Double.parseDouble(lastQuarterValue);
							double secondLastQuarterIntValue = Double.parseDouble(secondLastQuarterValue);

							System.out.println(scrips.get(i) + " : " + columns.get(0).text() + " - "
									+ secondLastQuarterValue + " " + lastQuarterIntValue);
							if (lastQuarterIntValue < secondLastQuarterIntValue || lastQuarterIntValue < 0) {
								status = false;
								break;
							}
						}
					}

					if (status) {
						scripsFiltered.add(scrips.get(i));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return scripsFiltered;
	}

}
