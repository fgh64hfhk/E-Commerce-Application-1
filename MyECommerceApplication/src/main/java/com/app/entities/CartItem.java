package com.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cartItemId;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "cart_id")
	@JsonBackReference
	@ToString.Exclude
	private Cart cart;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "product_variant_id")
	@JsonBackReference
	@ToString.Exclude
	private ProductVariant productVariant;
	
	private Integer quantity;

	public CartItem(Cart cart, ProductVariant productVariant, Integer quantity) {
		super();
		this.cart = cart;
		this.productVariant = productVariant;
		this.quantity = quantity;
	}

}
