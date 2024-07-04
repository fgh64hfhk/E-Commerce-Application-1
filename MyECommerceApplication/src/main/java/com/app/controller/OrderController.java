package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.config.AppConstants;
import com.app.entities.Address;
import com.app.entities.AddressDto;
import com.app.payloads.OrderDto;
import com.app.service.OrderService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
@CrossOrigin
public class OrderController {

	@Autowired
	public OrderService orderService;

	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful operation", content = {
					@Content(schema = @Schema(implementation = OrderDto.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "204", description = "No users found", content = {
					@Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = {
					@Content(schema = @Schema()) }) })
	@PostMapping("/public/user/{email}/order/{payment}")
	public ResponseEntity<OrderDto> createOrderByUserEmail(@PathVariable String email, @PathVariable String payment, @RequestBody AddressDto addressDto) {
		Address address = new Address(addressDto);
		
		OrderDto order = orderService.placeOrder(email, payment, address);

		return new ResponseEntity<OrderDto>(order, HttpStatus.CREATED);
	}

	@GetMapping("/public/user/{email}/orders")
	public ResponseEntity<List<OrderDto>> getAllOrdersByUserEmail(@PathVariable String email) {
		
		List<OrderDto> orderDtos = orderService.getOrdersByEmail(email);

		return new ResponseEntity<List<OrderDto>>(orderDtos, HttpStatus.FOUND);
	}

	@GetMapping("/admin/orders")
	public ResponseEntity<List<OrderDto>> getAllOrders(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
		List<OrderDto> orderDtos = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);
		
		return new ResponseEntity<List<OrderDto>>(orderDtos, HttpStatus.FOUND);
	}

}
