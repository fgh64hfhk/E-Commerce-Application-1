package com.app.service;

import java.util.List;

import com.app.entities.Address;
import com.app.payloads.OrderDto;

public interface OrderService {
	
	OrderDto placeOrder(String email, String paymentMethod, Address address);

	List<OrderDto> getOrdersByEmail(String email);

	List<OrderDto> getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
