package com.app.payloads;

import java.util.List;
import java.util.stream.Collectors;

import com.app.entities.Cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

	private Long id;

	private String userEmail;

	private List<CartItemDto> items;

	private Double totalPrice;

	private Integer deliveryPrice;

	// Constructor for Cart entity;
	public CartDto(Cart cart) {
		this.id = cart.getCartId();
		this.userEmail = cart.getUser().getEmail();
		this.items = cart.getCartItems().stream()
				.map(CartItemDto::new)
				.collect(Collectors.toList());
		this.totalPrice = cart.getCartItems().stream()
				.mapToDouble(cartItem -> cartItem.getProductVariant().getProduct().getPrice() * cartItem.getQuantity())
				.sum();
		this.deliveryPrice = cart.getDelivery().getDeliveryPrice();
	}
}
