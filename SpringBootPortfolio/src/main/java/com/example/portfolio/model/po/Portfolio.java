package com.example.portfolio.model.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.ForeignKey;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Portfolio implements Serializable {

    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column
    private Double cost;
    
    @Column
    private Integer amount;
    
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date date = new Date();

    @ManyToOne()
    @JoinColumn(name = "investor_id", referencedColumnName = "id")
    @JsonIgnoreProperties("portfolios")
    private Investor investor;
    
    @OneToOne()
    @JoinColumn(name = "tStock_id", 
                foreignKey = @ForeignKey(name="tStock_fk", 
                                         value = ConstraintMode.CONSTRAINT))
    private TStock tStock;
    
    public Portfolio() {
    }

    public Portfolio(Double cost, Integer amount, Investor investor, TStock tStock) {
        this.cost = cost;
        this.amount = amount;
        this.investor = investor;
        this.tStock = tStock;
    }
    
    
}
