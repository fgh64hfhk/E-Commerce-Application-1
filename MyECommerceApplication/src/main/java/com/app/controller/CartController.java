package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payloads.CartDto;
import com.app.service.CartService;

@RestController
@RequestMapping("/api")
public class CartController {

	@Autowired
	private CartService cartService;
	
	// 查詢
	@GetMapping("/cart/{userEmail}")
	public ResponseEntity<CartDto> getCartByUserEmail(@PathVariable String userEmail) {
		ResponseEntity<CartDto> entity = null;
		CartDto cartDto = cartService.findCartByUserEmail(userEmail);
		if (cartDto != null) {
			entity = new ResponseEntity<CartDto>(cartDto, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<CartDto>(cartDto, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	// 查詢
	@GetMapping("/cart/price/{userEmail}")
	public ResponseEntity<Integer> getCartTotalPriceByUserEmail(@PathVariable String userEmail) {
		ResponseEntity<Integer> entity = null;
		Integer price = cartService.findTotalPriceByUserEmail(userEmail);
		if (price > -1) {
			entity = new ResponseEntity<Integer>(price, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<Integer>(price, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
}
