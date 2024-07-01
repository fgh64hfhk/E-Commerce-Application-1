package com.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.CartItem;
import com.app.payloads.CartItemDto;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{

	// 根據使用者電子信箱添加購物車清單，回傳該購物車清單列表

	// 根據使用者電子信箱查詢所有的購物車清單
	@Query("SELECT new com.app.payloads.CartItemDto(ci) FROM CartItem ci WHERE ci.cart.user.email = :userEmail")
	List<CartItemDto> getAllCartItemsByUserEmail(@Param(value = "userEmail") String userEmail);
	
	// 根據商品變體的識別碼查找並鎖定要更新的購物車項目
	@Transactional
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT ci FROM CartItem ci WHERE ci.productVariant.sku = :sku AND ci.cart.user.email = :userEmail")
	Optional<CartItem> findCartItemForUpdate(@Param("sku") String sku, @Param("userEmail") String userEmail);
	
	// 根據購物車清單裡面的商品變體識別碼去修改購物車明細的數量，回傳更新後的選擇數量
	
	// 根據購物車清單裡面的商品變體識別碼去刪除整筆購物車明細
	@Transactional
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT ci FROM CartItem ci WHERE ci.productVariant.sku = :sku AND ci.cart.user.email = :userEmail")
	Optional<CartItem> findCartItemForDeletion(@Param("sku") String sku, @Param("userEmail") String userEmail);
}
