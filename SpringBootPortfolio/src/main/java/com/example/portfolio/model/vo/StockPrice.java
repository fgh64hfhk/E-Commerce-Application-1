package com.example.portfolio.model.vo;

public class StockPrice {
	public String High;
	public String Low;
	public String Volume;
	public String AdjClose;
	public String Close;
	public String Date;
	public String Open;
	
	@Override
	public String toString() {
		return "StockPrice [High=" + High + ", Low=" + Low + ", Volume=" + Volume + ", AdjClose=" + AdjClose
				+ ", Close=" + Close + ", Date=" + Date + ", Open=" + Open + "]";
	}
	
	
}
