package com.example.portfolio.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio.model.po.TStock;
import com.example.portfolio.model.vo.StockPrice;
import com.example.portfolio.service.PortfolioService;
import com.google.gson.Gson;

import jakarta.transaction.Transactional;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

@RestController
@RequestMapping("/price")
public class PriceController {
    
    @Autowired
    private PortfolioService service;
    
    private Gson gson = new Gson();
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    
    // 個股報價資訊(Watch List用)
    @GetMapping(value = {"/refresh"})
    @Transactional
    public List<TStock> refresh() {
        List<TStock> list = StreamSupport
                .stream(service.gettStockRepository().findAll().spliterator(), false)
                .collect(Collectors.toList());;
        for (TStock tStock : list) {
            // 取得報價資訊
            try {
            	String jsonstring = org.cdri.YahooFinance.getLatestStockData(tStock.getSymbol());
            	jsonstring = jsonstring.replaceAll("Adj Close", "AdjClose");
            	StockPrice stockPrice = gson.fromJson(jsonstring, StockPrice.class);
            	
            	//Stock stock = YahooFinance.get(tStock.getSymbol());
                //tStock.setChangePrice(stock.getQuote().getChange());
                //tStock.setChangePrice(stock.getQuote().getChange());
                //tStock.setChangeInPercent(stock.getQuote().getChangeInPercent());
                //tStock.setPreClosed(stock.getQuote().getPreviousClose());
                //tStock.setPrice(stock.getQuote().getPrice());
                //tStock.setTransactionDate(stock.getQuote().getLastTradeTime().getTime());
                //tStock.setVolumn(stock.getQuote().getVolume());
                
                
            	tStock.setPrice(new BigDecimal(stockPrice.AdjClose));
                tStock.setTransactionDate(sdf.parse(stockPrice.Date));
                tStock.setVolumn(Long.parseLong(stockPrice.Volume));
                tStock.setOpen(new BigDecimal(stockPrice.Open));
                tStock.setHigh(new BigDecimal(stockPrice.High));
                tStock.setLow(new BigDecimal(stockPrice.Low));
                tStock.setClose(new BigDecimal(stockPrice.Close));
                
                
                System.out.println(stockPrice);
                System.out.println(tStock);
            	/*
                Stock stock = YahooFinance.get(tStock.getSymbol());
                tStock.setChangePrice(stock.getQuote().getChange());
                tStock.setChangeInPercent(stock.getQuote().getChangeInPercent());
                tStock.setPreClosed(stock.getQuote().getPreviousClose());
                tStock.setPrice(stock.getQuote().getPrice());
                tStock.setTransactionDate(stock.getQuote().getLastTradeTime().getTime());
                tStock.setVolumn(stock.getQuote().getVolume());
                */
                // 更新報價
                service.gettStockRepository().updatePrice(
                        tStock.getId(), 
                        tStock.getChangePrice(), 
                        tStock.getChangeInPercent(), 
                        tStock.getPreClosed(), 
                        tStock.getPrice(), 
                        tStock.getTransactionDate(), 
                        tStock.getVolumn(),
                        tStock.getOpen(),
                        tStock.getHigh(),
                        tStock.getLow(),
                        tStock.getClose());
                
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return list;
    }
    
    // 歷史報價資訊(走勢圖用)
    // 範例 : /histquotes/^TWII 
    // 範例 : /histquotes/2330.TW 
    @GetMapping(value = {"/histquotes/{symbol:.+}"})
    public List<HistoricalQuote> queryHistoricalQuotes(@PathVariable("symbol") Optional<String> symbol) {
        List<HistoricalQuote> historicalQuotes = null;
        try {
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.add(Calendar.MONTH, -1); // from 1 month ago
            Stock stock = YahooFinance.get(symbol.get());
            historicalQuotes = stock.getHistory(from, to, Interval.DAILY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historicalQuotes;
    }
}
