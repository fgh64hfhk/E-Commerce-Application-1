package com.example.portfolio.service;

import com.example.portfolio.repository.ClassifyRepository;
import com.example.portfolio.repository.InvestorRepository;
import com.example.portfolio.repository.PortfolioRepository;
import com.example.portfolio.repository.TStockRepository;
import com.example.portfolio.repository.WatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {
    @Autowired
    private ClassifyRepository classifyRepository;
    
    @Autowired
    private TStockRepository tStockRepository;
    
    @Autowired
    private InvestorRepository investorRepository;
    
    @Autowired
    private PortfolioRepository portfolioRepository;
    
    @Autowired
    private WatchRepository watchRepository;

    public ClassifyRepository getClassifyRepository() {
        return classifyRepository;
    }

    public TStockRepository gettStockRepository() {
        return tStockRepository;
    }

    public InvestorRepository getInvestorRepository() {
        return investorRepository;
    }

    public PortfolioRepository getPortfolioRepository() {
        return portfolioRepository;
    }

    public WatchRepository getWatchRepository() {
        return watchRepository;
    }
    
    
}
