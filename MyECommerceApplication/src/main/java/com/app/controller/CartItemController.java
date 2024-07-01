package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.entities.CartItem;
import com.app.payloads.CartItemDto;
import com.app.service.CartItemService;

@RestController
@RequestMapping("/api")
public class CartItemController {

	@Autowired
	CartItemService cartItemService;
	
	@GetMapping("/cart/items/{userEmail}")
	public ResponseEntity<List<CartItemDto>> getAllCartItemsByUserEmail(@PathVariable("userEmail") String userEmail) {
		ResponseEntity<List<CartItemDto>> entity = null;
		List<CartItemDto> cartItemDtos = cartItemService.getAllCartItemsByUserEmail(userEmail);
		if (!cartItemDtos.isEmpty()) {
			entity = new ResponseEntity<List<CartItemDto>>(cartItemDtos, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<List<CartItemDto>>(cartItemDtos, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	@PutMapping("/cart/item/{sku}/{userEmail}/quantity/{quantity}")
	public ResponseEntity<CartItemDto> updateCartItemQuantityBySkuAndUserEmail(@PathVariable("sku") String sku, @PathVariable("userEmail") String userEmail, @PathVariable("quantity") Integer quantity) {
		ResponseEntity<CartItemDto> entity;
		CartItem cartItem = cartItemService.updateCartItemQuantityBySkuAndUserEmail(quantity, sku, userEmail);
		CartItemDto cartItemDto = new CartItemDto(cartItem);
		entity = new ResponseEntity<CartItemDto>(cartItemDto, HttpStatus.OK);

		return entity;
	}
	
	@PostMapping("/cart/item/{sku}/{userEmail}/quantity/{quantity}")
	public ResponseEntity<List<CartItemDto>> addCartItemBySkuAndUserEmail(@PathVariable("sku") String sku, @PathVariable("userEmail") String userEmail, @PathVariable("quantity") Integer quantity) {
		ResponseEntity<List<CartItemDto>> entity;
		List<CartItemDto> cartItems = cartItemService.addCartItemByUserEmail(userEmail, sku, quantity);

		entity = new ResponseEntity<List<CartItemDto>>(cartItems, HttpStatus.OK);

		return entity;
	}
	
	@DeleteMapping("/cart/item/{sku}/{userEmail}")
	public ResponseEntity<List<CartItemDto>> deleteCartItemBySkuAndUserEmail(@PathVariable("sku") String sku, @PathVariable("userEmail") String userEmail) {
		ResponseEntity<List<CartItemDto>> entity;
		List<CartItemDto> cartItems = cartItemService.deleteCartItemBySkuAndUserEmail(sku, userEmail);

		entity = new ResponseEntity<List<CartItemDto>>(cartItems, HttpStatus.OK);

		return entity;
	}
}
