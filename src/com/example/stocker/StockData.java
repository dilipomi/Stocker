package com.example.stocker;

public class StockData {

	public String symbol;
	public String price;
	public String change;
	public String changePercent;
	public String stopLose;

	public StockData(String symbol, String price, String change,
			String changePercent, String stopLose) {
		super();
		this.symbol = symbol;
		this.price = price;
		this.change = change;
		this.changePercent = changePercent + "%";
		this.stopLose = stopLose;
	}

}
