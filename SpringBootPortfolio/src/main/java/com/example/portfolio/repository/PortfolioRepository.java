package com.example.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.portfolio.model.po.Portfolio;

@Repository(value = "portfolioRepository")
public interface PortfolioRepository extends JpaRepository<Portfolio, Integer>{

}