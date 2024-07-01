package com.example.portfolio.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio.model.po.Classify;
import com.example.portfolio.model.po.TStock;
import com.example.portfolio.service.PortfolioService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/tstock")
public class TStockController {

    @Autowired
    private PortfolioService service;

    @PostMapping(value = {"/", "/add"})
    @Transactional
    public TStock add(@RequestBody Map<String, String> map) {
        Classify classify = service.getClassifyRepository().findById(Integer.parseInt(map.get("classify_id"))).get();
        TStock ts = new TStock();
        ts.setName(map.get("name"));
        ts.setSymbol(map.get("symbol"));
        ts.setClassify(classify);
        service.gettStockRepository().save(ts);
        return ts;
    }
    
    @PutMapping(value = {"/", "/update"})
    @Transactional
    public TStock update(@RequestBody Map<String, String> map) {
        Classify classify = service.getClassifyRepository().findById(Integer.parseInt(map.get("classify_id"))).get();
        TStock ts = service.gettStockRepository().findById(Integer.parseInt(map.get("id"))).get();
        ts.setName(map.get("name"));
        ts.setSymbol(map.get("symbol"));
        ts.setClassify(classify);
        //service.gettStockRepository().update(Long.parseLong(map.get("id")), map.get("name"), map.get("symbol"));
        return ts;
    }
    
    @DeleteMapping(value = {"/{id}", "/delete/{id}"})
    @Transactional
    public Boolean delete(@PathVariable("id") Integer id) {
        service.gettStockRepository().deleteById(id);
        return true;
    }
    
    @GetMapping(value = {"/{id}", "/get/{id}"})
    @Transactional
    public TStock get(@PathVariable("id") Integer id) {
        TStock tStock = service.gettStockRepository().findById(id).get();
        return tStock;
    }

    @GetMapping(value = {"/", "/query"})
    public Iterable<TStock> query() {
        return service.gettStockRepository().findAll();
    }

}