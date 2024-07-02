package com.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/public/login")
	public String login() {
		return "login";
	}

	@GetMapping("/public/")
	public String home() {
		return "Welcome to the home page!";
	}

	@GetMapping("/public/hello")
	public String hello() {
		return "Hello everyone!";
	}

	@GetMapping("/admin")
	public String manager() {
		return "Hello Manager !";
	}
}
