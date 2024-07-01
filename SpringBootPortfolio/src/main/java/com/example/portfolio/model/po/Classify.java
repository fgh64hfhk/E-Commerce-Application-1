package com.example.portfolio.model.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;


@Entity
@Table
@Getter
@Setter
public class Classify implements Serializable {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column
    private String name;
    
    @Column
    private Boolean tx; // transaction
    
    @OneToMany(cascade=CascadeType.PERSIST, mappedBy="classify", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("classify")
    private Set<TStock> tStocks;

    public Classify() {
    }

    public Classify(String name, Boolean tx) {
        this.name = name;
        this.tx = tx;
    }
    
    
    
    
}
