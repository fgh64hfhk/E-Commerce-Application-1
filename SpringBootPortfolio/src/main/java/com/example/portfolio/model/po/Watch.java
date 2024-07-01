package com.example.portfolio.model.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Watch implements Serializable {

    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column
    private String name;
    
    @ManyToOne()
    @JoinColumn(name = "investor_id", referencedColumnName = "id")
    @JsonIgnoreProperties("watchs")
    private Investor investor;
    
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "watch_tstock", 
        joinColumns = {
            @JoinColumn(name = "watch_id", nullable = false)
        },
        inverseJoinColumns = {
            @JoinColumn(name = "tStock_id", nullable = false)
        }
    )
    private Set<TStock> tStocks = new LinkedHashSet<>();


    public Watch() {
    }

    public Watch(String name, Investor investor) {
        this.name = name;
        this.investor = investor;
    }
    
    public Set<TStock> addTStock(TStock tStock) {
        tStocks.add(tStock);
        return tStocks;
    }
    
    public Set<TStock> removeTStock(TStock tStock) {
        tStocks.remove(tStock);
        return tStocks;
    }
    
    
}