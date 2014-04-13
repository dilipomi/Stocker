package com.example.stocker;

public class StockData {

	public String symbol;
	public String price;
	public String change;
	public String changePercent;
	public String low;
	public String high;

	public StockData(String symbol, String price, String change,
			String changePercent, String low, String high) {
		super();
		this.symbol = symbol;
		this.price = price;
		this.change = change;
		this.changePercent = changePercent + "%";
		this.low = low;
		this.high = high;
	}

}
