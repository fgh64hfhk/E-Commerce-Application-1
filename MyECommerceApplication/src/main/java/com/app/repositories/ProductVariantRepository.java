package com.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

	ProductVariant findBySku(String sku);
}
