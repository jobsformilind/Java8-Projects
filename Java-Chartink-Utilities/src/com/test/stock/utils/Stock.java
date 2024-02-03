package com.test.stock.utils;

public class Stock {

	private String name;
	private String nseCode;
	private String bseCode;
	private Enums.Status status;

	public Stock(String name, String nseCode, String bseCode) {
		this.name = name;
		this.nseCode = nseCode;
		this.bseCode = bseCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNseCode() {
		return nseCode;
	}

	public void setNseCode(String nseCode) {
		this.nseCode = nseCode;
	}

	public String getBseCode() {
		return bseCode;
	}

	public void setBseCode(String bseCode) {
		this.bseCode = bseCode;
	}

	public Enums.Status getStatus() {
		return status;
	}

	public void setStatus(Enums.Status status) {
		this.status = status;
	}

	public boolean isMatchingStock(String symbol) {
		return getNseCode().equalsIgnoreCase(symbol) || getBseCode().equalsIgnoreCase(symbol);
	}

	public String getSymbol() {
		return nseCode != null ? nseCode : bseCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Stock [name=");
		builder.append(name);
		builder.append(", nseCode=");
		builder.append(nseCode);
		builder.append(", bseCode=");
		builder.append(bseCode);
		builder.append(", Status=");
		builder.append(status.name());
		builder.append("]");
		return builder.toString();
	}

	public boolean isSaving() {
		return isStatus(Enums.Status.NEW) || isStatus(Enums.Status.KEEP);
	}

	public boolean isStatus(Enums.Status checkStatus) {
		return status == checkStatus;
	}

	public boolean isMarkedKeep() {
		return isStatus(Enums.Status.KEEP);
	}

	public boolean isMarkedRemoval() {
		return isStatus(Enums.Status.REMOVE);
	}

	public boolean isMarkedNew() {
		return isStatus(Enums.Status.NEW);
	}
}
