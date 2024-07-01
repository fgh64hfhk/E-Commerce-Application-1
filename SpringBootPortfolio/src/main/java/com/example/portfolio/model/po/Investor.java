package com.example.portfolio.model.po;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter
@Setter
public class Investor implements Serializable {

    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column
    private String username;
    
    @Column
    private String password;
    
    @Column
    private String email;
    
    @Column
    private Integer balance;
    
    @Column
    private String code;
    
    @Column
    private Boolean pass;
    

    @OneToMany(cascade=CascadeType.PERSIST, mappedBy="investor", fetch = FetchType.EAGER)
    //@JsonIgnoreProperties("investor")
    private Set<Portfolio> portfolios;
    
    @OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy="investor", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("investor")
    private Set<Watch> watchs;
    
    public Investor() {
    }

    public Investor(String username, String password, String email, Integer balance) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.balance = balance;
        pass = true;
    }
    
    
    
}
