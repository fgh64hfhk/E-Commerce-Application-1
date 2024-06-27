package com.app.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.ProductCategory;

public interface CategoryRepository extends JpaRepository<ProductCategory, Long> {

	@EntityGraph(attributePaths = { "products" })
	ProductCategory findByCategoryName(String categoryName);

}
