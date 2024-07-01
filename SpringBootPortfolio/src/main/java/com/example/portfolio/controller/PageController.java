package com.example.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class PageController {
	
	@GetMapping(value = "/investor")
    public String investor() {
        return "investor";
    }
	
	@GetMapping(value = "/classify")
    public String classify() {
        return "classify";
    }
	
	@GetMapping(value = "/tstock")
    public String tstock() {
        return "tstock";
    }
	
	@GetMapping(value = "/watch")
    public String watch() {
        return "watch";
    }
    
	@GetMapping(value = "/watchlist")
    public String watchlist() {
        return "watchlist";
    }
    
}
