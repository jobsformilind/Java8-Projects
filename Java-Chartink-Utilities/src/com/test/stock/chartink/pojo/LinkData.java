package com.test.stock.chartink.pojo;

import java.util.ArrayList;
import java.util.List;

import com.test.stock.chartink.utils.Constants;

public class LinkData {
	private String linkName;
	private String linkClause;
	private List<Stock> stocks = new ArrayList<>();

	public LinkData(String linkName, String linkClause) {
		this.linkName = linkName;
		this.linkClause = linkClause;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getLinkClause() {
		return linkClause;
	}

	public void setLinkClause(String linkClause) {
		this.linkClause = linkClause;
	}

	public List<Stock> getStocks() {
		return stocks;
	}

	public void addStocks(List<Stock> stocks) {
		if (stocks != null) {
			this.stocks.addAll(stocks);
		}
	}

	public void addStock(Stock stock) {
		if (stock != null) {
			this.stocks.add(stock);
		}
	}

	public Stock getStock(String symbol) {
		return stocks.stream().filter(s -> s.isMatchingStock(symbol)).findFirst().orElse(null);
	}

	public String getDataFile() {
		return Constants.DIR_DATA_DIR + linkName + ".dat";
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LinkData [linkName=");
		builder.append(linkName);
		builder.append(", stocks=");
		builder.append(stocks);
		builder.append("]");
		return builder.toString();
	}
}
