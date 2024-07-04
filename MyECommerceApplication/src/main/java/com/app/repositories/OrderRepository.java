package com.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.Order;
import com.app.payloads.OrderDto;

public interface OrderRepository extends JpaRepository<Order, Long> {

	// 根據使用者電子信箱查詢訂單
	@Query("SELECT new com.app.payloads.OrderDto(o) FROM Order o WHERE o.email = :email")
	List<OrderDto> findOrdersByUserEmail(@Param("email") String email);
	
	// 查詢所有的訂單
	
}
