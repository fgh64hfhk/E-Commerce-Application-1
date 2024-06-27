package com.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

	@EntityGraph(attributePaths = { "productVariants", "applicableCoupons" })
	Optional<Product> findById(Long id);
	
	@EntityGraph(attributePaths = { "productVariants", "applicableCoupons" })
	List<Product> findAll();
}
