package com.test.stock.screener.meta;

public class JsonStock {
	public int id;
	public String symbol;
	public String name;
	public String url;
	public String priceUrl;
	public String medianPEURL;
	public String comments;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getMedianPEURL() {
		return medianPEURL;
	}

	public void setMedianPEURL(String medianPEURL) {
		this.medianPEURL = medianPEURL;
	}

	@Override
	public String toString() {
		return "JsonStock [id=" + id + ", symbol=" + symbol + ", name=" + name + ", url=" + url + "]";
	}

	public String getPriceUrl() {
		return priceUrl;
	}

	public void setPriceUrl(String priceUrl) {
		this.priceUrl = priceUrl;
	}
}
