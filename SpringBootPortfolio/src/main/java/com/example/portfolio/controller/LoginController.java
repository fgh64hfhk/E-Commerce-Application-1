package com.example.portfolio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.portfolio.model.po.Investor;
import com.example.portfolio.service.PortfolioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
    
    @Autowired
    private PortfolioService service;
    
    @PostMapping("/")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session) {
        Investor investor = service.getInvestorRepository().getInvestor(username);
        if(investor != null && investor.getPassword().equals(password)) {
            session.setAttribute("investor", investor);
            session.setAttribute("watch_id", investor.getWatchs().iterator().next().getId());
            return "redirect:/portfolio/index.jsp";
        }
        session.invalidate();
        return "redirect:/portfolio/login.jsp";
    }
    
    @GetMapping("/out")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/portfolio/login.jsp";
    }
}
