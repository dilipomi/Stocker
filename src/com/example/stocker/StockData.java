package com.example.stocker;

public class StockData {

	public String symbol;
	public String price;
	public String change;
	public String changePercent;

	public StockData(String symbol, String price, String change,
			String changePercent) {
		super();
		this.symbol = symbol;
		this.price = price;
		this.change = change;
		this.changePercent = changePercent + "%";
	}

}
