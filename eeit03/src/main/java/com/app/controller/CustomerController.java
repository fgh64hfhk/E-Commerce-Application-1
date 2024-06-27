package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.entities.Customer;
import com.app.repository.CustomerRepository;

@RestController
public class CustomerController {

	@Autowired
	private CustomerRepository customerRepository;

	@PostMapping("/customer")
	public Boolean addCustomer(@RequestBody Customer customer) {
		customerRepository.save(customer);
		return true;
	}

	@PutMapping("/customer/{id}")
	public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
		Customer c = getCustomerById(id);
		if (c != null) {
			customer.setId(c.getId());
			Customer updatedCustomer = customerRepository.save(customer);
			return updatedCustomer;
		} else {
			return null;
		}
	}

	@GetMapping("/customer/{id}")
	public Customer getCustomerById(@PathVariable Long id) {
		Customer customer = customerRepository.findById(id).orElse(null);
		return customer;
	}

	@DeleteMapping("/customer/{id}")
	public Boolean deleteCustomer(@PathVariable Long id) {
		customerRepository.deleteById(id);
		return true;
	}
}
