package com.example.portfolio.model.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TStock implements Serializable {

    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String symbol;
    
    @Column
    private String name;
    
    // 以下是報價資料
    @Column
    private BigDecimal preClosed;
    
    @Column
    private BigDecimal price;
    
    @Column
    private BigDecimal changePrice;
    
    @Column
    private BigDecimal changeInPercent;
    
    @Column
    private Long volumn;
    
    @Column
    @Temporal(TemporalType.DATE)
    private Date transactionDate;
    
    @Column(name = "open_price") // "open" is a reserved word in MySQL	"
    private BigDecimal open;
    
    @Column(name = "high_price") 
    private BigDecimal high;
    
    @Column(name = "low_price")
    private BigDecimal low;
    
    @Column(name = "close_price")
    private BigDecimal close;
    
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "classify_id", referencedColumnName = "id")
    @JsonIgnoreProperties("tStocks")
    private Classify classify;
    
    public TStock() {
    }

    public TStock(String symbol, String name, Classify classify) {
        this.symbol = symbol;
        this.name = name;
        this.classify = classify;
    }
    
}