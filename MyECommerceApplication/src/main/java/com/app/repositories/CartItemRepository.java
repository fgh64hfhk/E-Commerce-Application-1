package com.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.CartItem;

import jakarta.transaction.Transactional;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{

	// 根據使用者電子信箱添加購物車清單，回傳該購物車清單列表
//	@Modifying
//	@Transactional
//	@Query("INSERT INTO CartItem(")
//	void addCartItemByUserEmail(
//			@Param("userEmail") String userEmail,
//			@Param("sku") String sku,
//			@Param("quantity") int quantity);
	// 根據使用者電子信箱查詢所有的購物車清單
	
	// 根據購物車清單裡面的商品變體識別碼去修改購物車明細的數量，回傳更新後的選擇數量
	// 根據購物車清單裡面的商品變體識別碼去刪除整筆購物車明細
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
