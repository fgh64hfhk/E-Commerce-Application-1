package com.app.payloads;

import com.app.entities.CartItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

	private Long id;

	private String name;

	private Double price;

	private Integer quantity;
	
	// Constructor for Item entity
	public CartItemDto(CartItem cartItem) {
		this.id = cartItem.getCartItemId();
		this.name = cartItem.getProductVariant().getProduct().getName();
		this.price = cartItem.getProductVariant().getProduct().getPrice();
		this.quantity = cartItem.getQuantity();
	}
}
