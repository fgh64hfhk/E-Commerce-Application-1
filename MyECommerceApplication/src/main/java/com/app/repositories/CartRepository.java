package com.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.Cart;
import com.app.payloads.CartDto;

public interface CartRepository extends JpaRepository<Cart, Long>{

	// Find Cart by user email
	@Query("SELECT new com.app.payloads.CartDto(c) FROM Cart c WHERE c.user.email = :email")
	CartDto findCartByUserEmail(@Param("email") String email);
	
	// Find total price by user email
	@Query("SELECT SUM(p.price * i.quantity) FROM Cart c JOIN c.cartItems i JOIN i.productVariant v JOIN v.product p WHERE c.user.email = :email")
	Integer findTotalPriceByUserEmail(@Param("email") String email);
	
	// Find delivery price by user email
//	@Query("SELECT c.delivery.deliveryPrice FROM Cart c WHERE c.user.email = :email")
//	Integer findDeliveryPriceByUserEmail(@Param("email") String email);
}
