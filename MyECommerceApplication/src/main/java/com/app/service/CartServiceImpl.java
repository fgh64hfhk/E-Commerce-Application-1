package com.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.payloads.CartDto;
import com.app.repositories.CartRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

	@Autowired
	CartRepository cartRepository;
	
	@Override
	public CartDto findCartByUserEmail(String email) {
		CartDto cartDto = cartRepository.findCartByUserEmail(email);
		return cartDto;
	}

	@Override
	public Integer findTotalPriceByUserEmail(String email) {
		Integer price = cartRepository.findTotalPriceByUserEmail(email);
		System.out.println("Service: " + price);
		return price;
	}

	@Override
	public Integer findDeliveryPriceByUserEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

}
