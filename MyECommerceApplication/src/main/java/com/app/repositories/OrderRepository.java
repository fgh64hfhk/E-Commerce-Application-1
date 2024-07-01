package com.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	// 查詢所有的訂單
	
}
