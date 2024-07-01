package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.payloads.OrderDto;
import com.app.service.OrderService;

@RestController
@RequestMapping("/api")
public class OrderController {

	@Autowired
	public OrderService orderService;

	@PostMapping("/user/{email}/order/{payment}")
	public ResponseEntity<OrderDto> createOrderByUserEmail(@PathVariable String email, @PathVariable String payment) {
		OrderDto order = orderService.placeOrder(email, payment);

		return new ResponseEntity<OrderDto>(order, HttpStatus.CREATED);
	}

	@GetMapping("/orders/{email}")
	public ResponseEntity<List<OrderDto>> getAllOrdersByUserEmail(@PathVariable String email) {
		
		List<OrderDto> orderDtos = orderService.getOrdersByEmail(email);

		return new ResponseEntity<List<OrderDto>>(orderDtos, HttpStatus.FOUND);
	}

	@GetMapping("/orders")
	public ResponseEntity<List<OrderDto>> getAllOrders(
			@RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = "orderDate", required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = "asc", required = false) String sortOrder) {
		List<OrderDto> orderDtos = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);
		
		return new ResponseEntity<List<OrderDto>>(orderDtos, HttpStatus.FOUND);
	}

}
