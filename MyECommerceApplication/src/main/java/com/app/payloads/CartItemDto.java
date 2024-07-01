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
	
	private String sku;
	
	private String color;
	
	private String size;
	
	private String image;
	
	// Constructor for Item entity
	public CartItemDto(CartItem cartItem) {
		this.id = cartItem.getCartItemId();
		this.name = cartItem.getProductVariant().getProduct().getName();
		this.price = cartItem.getProductVariant().getProduct().getPrice();
		this.quantity = cartItem.getQuantity();
		this.sku = cartItem.getProductVariant().getSku();
		this.color = cartItem.getProductVariant().getColor();
		this.size = cartItem.getProductVariant().getSize();
		this.image = cartItem.getProductVariant().getImage();
	}
	
}
