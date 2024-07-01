package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	public ResponseEntity<OrderDto> orderProducts(@PathVariable String email, @PathVariable String payment) {
		OrderDto order = orderService.placeOrder(email, payment);
		
		return new ResponseEntity<OrderDto>(order, HttpStatus.CREATED);
	}

//	@GetMapping("/admin/orders")
//	public ResponseEntity<OrderResponse> getAllOrders(
//			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
//			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
//			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
//			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
//		
//		OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);
//
//		return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.FOUND);
//	}
//	
//	@GetMapping("public/users/{emailId}/orders")
//	public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable String emailId) {
//		List<OrderDTO> orders = orderService.getOrdersByUser(emailId);
//		
//		return new ResponseEntity<List<OrderDTO>>(orders, HttpStatus.FOUND);
//	}
//	
//	@GetMapping("public/users/{emailId}/orders/{orderId}")
//	public ResponseEntity<OrderDTO> getOrderByUser(@PathVariable String emailId, @PathVariable Long orderId) {
//		OrderDTO order = orderService.getOrder(emailId, orderId);
//		
//		return new ResponseEntity<OrderDTO>(order, HttpStatus.FOUND);
//	}
//	
//	@PutMapping("admin/users/{emailId}/orders/{orderId}/orderStatus/{orderStatus}")
//	public ResponseEntity<OrderDTO> updateOrderByUser(@PathVariable String emailId, @PathVariable Long orderId, @PathVariable String orderStatus) {
//		OrderDTO order = orderService.updateOrder(emailId, orderId, orderStatus);
//		
//		return new ResponseEntity<OrderDTO>(order, HttpStatus.OK);
//	}

}
