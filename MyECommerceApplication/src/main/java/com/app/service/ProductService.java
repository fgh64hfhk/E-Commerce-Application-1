package com.app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.app.entities.Product;

public interface ProductService {

	// 新增商品時最少一項商品變體
	boolean addProductByCategory(String categoryName, Product product, List<MultipartFile> files);

	// 刪除商品時同時刪除所有的商品變體
	boolean deleteProductById(Long productId);

	boolean updateProductById(Long productId, Product product);

	Product getProductById(Long productId);

	List<Product> getAllProducts();

	List<Product> getAllProductsByCategory(String category);

	List<Product> searchProductsByKeyword(String keyword);
}
