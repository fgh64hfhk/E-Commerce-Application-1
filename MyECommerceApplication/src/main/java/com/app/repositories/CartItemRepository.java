package com.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{

	// addCartItemByUserEmail
	// List<CartItemDto>
	
	// getAllCartItemsByUserEmail
	// List<CartItemDto>
	
	// updateCartItemQuantityBySku
	// 撰寫是否鎖定資料庫的邏輯
	// Integer
	
	// deleteCartItemBySku
	// CartItemDto
}
