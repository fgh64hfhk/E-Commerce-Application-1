package com.example.portfolio.model.po;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Immutable
@Subselect("SELECT ROW_NUMBER() OVER() AS id, p.investor_id as invid, c.name as name, SUM(p.amount * s.price) as subtotal " +
           "FROM Classify c, Portfolio p, TStock s " +
           "WHERE p.t_stock_id = s.id AND s.classify_id = c.id " +
           "GROUP BY p.investor_id, c.name") // p.investor.id=:id AND 
@Getter
@Setter
public class Asset {
    @Id
    private Integer id;
    
    @Column
    private Integer invid;
    
    @Column
    private String name;
    
    @Column
    private Double subtotal;

    
    
}
