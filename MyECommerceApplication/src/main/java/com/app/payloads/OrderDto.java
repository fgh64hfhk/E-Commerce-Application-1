package com.app.payloads;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.app.entities.Address;
import com.app.entities.Coupon;
import com.app.entities.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

	private String email;
	
	private LocalDate date;
	
	private Double amount;
	
	private String status;
	
	private Coupon applicableCoupon;
	
	private List<OrderItemDto> items;
	
	private Address address; 
	
	public OrderDto(Order order) {
		this.email = order.getEmail();
		this.date = order.getOrderDate();
		this.amount = order.getTotalAmount();
		this.status = order.getOrderStatus();
		this.applicableCoupon = order.getCoupon();
		this.items = order.getOrderItems().stream()
				.map(oi -> new OrderItemDto(oi))
				.collect(Collectors.toList());
	}
}
