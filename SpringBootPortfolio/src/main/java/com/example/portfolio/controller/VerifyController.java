package com.example.portfolio.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.portfolio.model.po.Investor;
import com.example.portfolio.service.PortfolioService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/verify")
public class VerifyController {
    @Autowired
    PortfolioService service;
    
    @Transactional
    @GetMapping("/{id}/{code}")
    @ResponseBody
    public String verify(@PathVariable("id") Optional<Integer> id,
                         @PathVariable("code") Optional<String> code, 
                         HttpSession session) {
        String message = "ERROR";
        Investor investor = service.getInvestorRepository().findById(id.get()).get();
        if(investor != null && investor.getCode().equals(code.get())) {
            service.getInvestorRepository().updatePass(id.get(), Boolean.TRUE);
            message = "SUCCESS";
        }
        session.setAttribute("message", message);
        //return "redirect:/portfolio/verify.jsp";
        return message;
    }
    
}