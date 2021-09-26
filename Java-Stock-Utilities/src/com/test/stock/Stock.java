package com.test.stock;

import java.util.Comparator;

import com.test.stock.utils.Utils;

public class Stock implements Comparator<Stock>, Comparable<Stock> {
	String symbol = "";
	String bseSymbol = "";
	String nseSymbol = "";
	String faceValue = "";
	String cagr1 = "";
	String cagr3 = "";
	String cagr5 = "";
	String cagr10 = "";
	String roe1 = "";
	String roe3 = "";
	String roe5 = "";
	String roe10 = "";
	String profit1 = "";
	String profit3 = "";
	String profit5 = "";
	String profit10 = "";
	int cagrAvg35;
	int profitAvg35;
	int roeAvg35;
	int cagrAvg;
	int profitAvg;
	int roeAvg;
	String sector = "";

	public Stock(String symbol) {
		this.symbol = trim(symbol);
	}

	public String getCSV() {
		StringBuffer buff = new StringBuffer();
		buff.append(symbol).append(",");
		buff.append(bseSymbol).append(",");
		buff.append(nseSymbol).append(",");
		buff.append(faceValue).append(",");
		buff.append(cagr1).append(",");
		buff.append(roe1).append(",");
		buff.append(profit1).append(",");
		buff.append(cagr3).append(",");
		buff.append(roe3).append(",");
		buff.append(profit3).append(",");
		buff.append(cagr5).append(",");
		buff.append(roe5).append(",");
		buff.append(profit5).append(",");
		buff.append(cagr10).append(",");
		buff.append(roe10).append(",");
		buff.append(profit10).append(",");
		buff.append(cagrAvg35).append(",");
		buff.append(roeAvg35).append(",");
		buff.append(profitAvg35).append(",");
		buff.append(cagrAvg).append(",");
		buff.append(roeAvg).append(",");
		buff.append(profitAvg).append(",");
		buff.append(sector);
		return buff.toString();
	}

	public static String getCSVHeader() {
		StringBuffer buff = new StringBuffer();
		buff.append("symbol").append(",");
		buff.append("bseSymbol").append(",");
		buff.append("nseSymbol").append(",");
		buff.append("faceValue").append(",");
		buff.append("cagr1").append(",");
		buff.append("roe1").append(",");
		buff.append("profit1").append(",");
		buff.append("cagr3").append(",");
		buff.append("roe3").append(",");
		buff.append("profit3").append(",");
		buff.append("cagr5").append(",");
		buff.append("roe5").append(",");
		buff.append("profit5").append(",");
		buff.append("cagr10").append(",");
		buff.append("roe10").append(",");
		buff.append("profit10").append(",");
		buff.append("cagrAvg35").append(",");
		buff.append("roeAvg35").append(",");
		buff.append("profitAvg35").append(",");
		buff.append("cagrAvg").append(",");
		buff.append("roeAvg").append(",");
		buff.append("profitAvg").append(",");
		buff.append("sector").append("\n");
		return buff.toString();
	}

	@Override
	public String toString() {
		return symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setFaceValue(String faceValue) {
		this.faceValue = trim(faceValue);
	}

	public void setCagr1(String cagr1) {
		this.cagr1 = trim(cagr1);
	}

	public void setCagr3(String cagr3) {
		this.cagr3 = trim(cagr3);
	}

	public void setCagr5(String cagr5) {
		this.cagr5 = trim(cagr5);
	}

	public void setCagr10(String cagr10) {
		this.cagr10 = trim(cagr10);
	}

	public void setSector(String sector) {
		this.sector = trim(sector);
	}

	public void setBseSymbol(String bseSymbol) {
		this.bseSymbol = bseSymbol;
	}

	public void setNseSymbol(String nseSymbol) {
		this.nseSymbol = nseSymbol;
	}

	public String getBseSymbol() {
		return bseSymbol;
	}

	public String getNseSymbol() {
		return nseSymbol;
	}

	public void setRoe1(String roe1) {
		this.roe1 = trim(roe1);
	}

	public void setRoe3(String roe3) {
		this.roe3 = trim(roe3);
	}

	public void setRoe5(String roe5) {
		this.roe5 = trim(roe5);
	}

	public void setRoe10(String roe10) {
		this.roe10 = trim(roe10);
	}

	public void setProfit1(String profit1) {
		this.profit1 = trim(profit1);
	}

	public void setProfit3(String profit3) {
		this.profit3 = trim(profit3);
	}

	public void setProfit5(String profit5) {
		this.profit5 = trim(profit5);
	}

	public void setProfit10(String profit10) {
		this.profit10 = trim(profit10);
	}

	public String getCagr3() {
		return cagr3;
	}

	public String getCagr5() {
		return cagr5;
	}

	public String getCagr10() {
		return cagr10;
	}

	public String getRoe3() {
		return roe3;
	}

	public String getRoe5() {
		return roe5;
	}

	public String getRoe10() {
		return roe10;
	}

	public String getProfit3() {
		return profit3;
	}

	public String getProfit5() {
		return profit5;
	}

	public String getProfit10() {
		return profit10;
	}

	public String getFaceValue() {
		return faceValue;
	}

	public String getCagr1() {
		return cagr1;
	}

	public int getCagrAvg() {
		return cagrAvg;
	}

	public String getRoe1() {
		return roe1;
	}

	public String getSector() {
		return sector;
	}

	public int getProfitAvg() {
		return profitAvg;
	}

	public void setProfitAvg(int profitAvg) {
		this.profitAvg = profitAvg;
	}

	public int getRoeAvg() {
		return roeAvg;
	}

	public void setRoeAvg(int roeAvg) {
		this.roeAvg = roeAvg;
	}

	public String getProfit1() {
		return profit1;
	}

	public void setCagrAvg(int cagrAvg) {
		this.cagrAvg = cagrAvg;
	}

	public int getCagrAvg35() {
		return cagrAvg35;
	}

	public void setCagrAvg35(int cagrAvg35) {
		this.cagrAvg35 = cagrAvg35;
	}

	public int getProfitAvg35() {
		return profitAvg35;
	}

	public void setProfitAvg35(int profitAvg35) {
		this.profitAvg35 = profitAvg35;
	}

	public int getRoeAvg35() {
		return roeAvg35;
	}

	public void setRoeAvg35(int roeAvg35) {
		this.roeAvg35 = roeAvg35;
	}

	@Override
	public int compare(Stock o1, Stock o2) {
		return o1.getSymbol().compareTo(o2.getSymbol());
	}

	@Override
	public int compareTo(Stock o) {
		return compare(this, o);
	}

	public String trim(String str) {
		return str == null ? "" : str.trim();
	}

	public String average(String str1, String str2, String str3) {
		int cnt = 0;
		int total = 0;
		if (Utils.isNotEmpty(str1)) {
			total += Integer.parseInt(str1);
			cnt++;
		}
		if (Utils.isNotEmpty(str2)) {
			total += Integer.parseInt(str2);
			cnt++;
		}
		if (Utils.isNotEmpty(str3)) {
			total += Integer.parseInt(str3);
			cnt++;
		}
		if (cnt > 0) {
			return "" + total / cnt;
		}
		return "NC";
	}

}
