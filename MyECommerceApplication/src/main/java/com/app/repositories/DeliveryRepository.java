package com.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long>{

}
