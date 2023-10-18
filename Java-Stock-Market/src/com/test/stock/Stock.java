package com.test.stock;

import java.util.Comparator;

import com.test.stock.utils.Utils;

public class Stock implements Comparator<Stock>, Comparable<Stock> {
	boolean consolidated;
	boolean forceUpdate;
	int daysToUpdate = 10;
	int companyId;
	String symbol = "";
	String bseSymbol = "";
	String nseSymbol = "";
	String faceValue = "";
	String cagr1 = "";
	String cagr3 = "";
	String cagr5 = "";
	String cagr10 = "";
	String sale1 = "";
	String sale3 = "";
	String sale5 = "";
	String sale10 = "";
	int cagrAvg;
	int saleAvg;
	String marketCap = "";
	String PE = "";
	String medianPE = "";
	String EPS = "";
	String sector = "";
	String error = "";

	public Stock(String symbol) {
		this.symbol = trim(symbol);
	}

	public String getCSV() {
		StringBuffer buff = new StringBuffer();
		buff.append(bseSymbol).append(",");
		buff.append(nseSymbol).append(",");
		buff.append(symbol).append(",");
		buff.append(faceValue).append(",");
		buff.append("XXXX").append(",");
		buff.append("000").append(",");

		buff.append(EPS).append(",");
		buff.append(medianPE).append(",");
		buff.append(Utils.defaultIfEmpty(PE, medianPE)).append(",");
		buff.append(saleAvg).append(",");

		buff.append(cagr1).append(",");
		buff.append(cagr3).append(",");
		buff.append(cagr5).append(",");
		buff.append(cagr10).append(",");
		buff.append(cagrAvg).append(",");

		buff.append(marketCap).append(",");
		buff.append(sector).append(",");

		buff.append(sale1).append(",");
		buff.append(sale3).append(",");
		buff.append(sale5).append(",");
		buff.append(sale10).append(",");

		return buff.toString();
	}

	public static String getCSVHeader() {
		StringBuffer buff = new StringBuffer();
		buff.append("BSE").append(",");
		buff.append("NSE").append(",");
		buff.append("Symbol").append(",");
		buff.append("FV").append(",");
		buff.append("Name").append(",");
		buff.append("Hi3Y").append(",");

		buff.append("EPS").append(",");
		buff.append("M_PE").append(",");
		buff.append("PE").append(",");
		buff.append("S_Avg").append(",");

		buff.append("C_1").append(",");
		buff.append("C_3").append(",");
		buff.append("C_5").append(",");
		buff.append("C_10").append(",");
		buff.append("C_Avg").append(",");
		buff.append("MCap").append(",");
		buff.append("sector").append(",");

		buff.append("S_1").append(",");
		buff.append("S_3").append(",");
		buff.append("S_5").append(",");
		buff.append("S_10").append(",");

		buff.append("\n");
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
		if(Utils.isEmpty(this.bseSymbol)) {
			if(Utils.isNotEmpty(bseSymbol)) {
				this.bseSymbol = bseSymbol;
			}
		}
	}

	public void setNseSymbol(String nseSymbol) {
		if(Utils.isEmpty(this.nseSymbol)) {
			if(Utils.isNotEmpty(nseSymbol)) {
				this.nseSymbol = nseSymbol;
			}
		}
	}

	public String getBseSymbol() {
		return bseSymbol;
	}

	public String getNseSymbol() {
		return nseSymbol;
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

	public String getFaceValue() {
		return faceValue;
	}

	public String getCagr1() {
		return cagr1;
	}

	public int getCagrAvg() {
		return cagrAvg;
	}

	public void setCagrAvg(int cagrAvg) {
		this.cagrAvg = cagrAvg;
	}

	@Override
	public int compare(Stock o1, Stock o2) {
		return 0;
		//return o1.getSymbol().compareTo(o2.getSymbol());
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

	public String getSale1() {
		return sale1;
	}

	public void setSale1(String sale1) {
		this.sale1 = sale1;
	}

	public String getSale3() {
		return sale3;
	}

	public void setSale3(String sale3) {
		this.sale3 = sale3;
	}

	public String getSale5() {
		return sale5;
	}

	public void setSale5(String sale5) {
		this.sale5 = sale5;
	}

	public String getSale10() {
		return sale10;
	}

	public void setSale10(String sale10) {
		this.sale10 = sale10;
	}

	public int getSaleAvg() {
		return saleAvg;
	}

	public void setSaleAvg(int saleAvg) {
		this.saleAvg = saleAvg;
	}

	public String getSector() {
		return sector;
	}

	public String getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(String marketCap) {
		this.marketCap = marketCap;
	}

	public String getPE() {
		return PE;
	}

	public void setPE(String pE) {
		PE = pE;
	}

	public String getEPS() {
		return EPS;
	}

	public void setEPS(String ePS) {
		EPS = ePS;
	}

	public boolean isConsolidated() {
		return consolidated;
	}

	public void setConsolidated(boolean consolidated) {
		this.consolidated = consolidated;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getMedianPE() {
		return medianPE;
	}

	public void setMedianPE(String medianPE) {
		this.medianPE = medianPE;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isForceUpdate() {
		return forceUpdate;
	}

	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}

	public int getDaysToUpdate() {
		return daysToUpdate;
	}

	public void setDaysToUpdate(int daysToUpdate) {
		this.daysToUpdate = daysToUpdate;
	}
}
