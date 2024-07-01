package com.app.service;

import java.util.List;

import com.app.payloads.OrderDto;

public interface OrderService {
	
	OrderDto placeOrder(String email, String paymentMethod);

	OrderDto getOrderByEmail(String email);

	List<OrderDto> getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
