package com.app.payloads;

import com.app.entities.OrderItem;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
	
	private String color;

	private String size;

	private String sku;

	private Integer quantity;

	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String image;

	public OrderItemDto(OrderItem orderItem) {
		this.color = orderItem.getColor();
		this.size = orderItem.getSize();
		this.sku = orderItem.getSku();
		this.quantity = orderItem.getQuantity();
		this.image = orderItem.getImage();
	}
}
