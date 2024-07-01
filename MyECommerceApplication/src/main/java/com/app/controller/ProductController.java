package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.entities.Product;
import com.app.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ProductController {

	// injection cart service
	@Autowired
	private ProductService service;

	// 增 --> 根據商品種類的名稱新增商品
	@PostMapping(path = "/{categoryName}/product", consumes = { "multipart/form-data" })
	public ResponseEntity<Product> createProduct(@PathVariable String categoryName,
			@RequestPart("product") Product product,
			@RequestPart(value = "image", required = false) List<MultipartFile> files) {

		ResponseEntity<Product> entity = null;

		if (service.addProductByCategory(categoryName, product, files)) {
			entity = new ResponseEntity<Product>(product, HttpStatus.CREATED);
		} else {
			entity = new ResponseEntity<Product>(product, HttpStatus.BAD_REQUEST);
		}

		return entity;
	}
	
	// 增 --> 根據商品種類的名稱新增商品
	@PostMapping(path = "/{categoryName}/products", consumes = { "multipart/form-data" })
	public ResponseEntity<List<Product>> createProductsByCategoryName(@PathVariable String categoryName,
			@RequestPart("products") List<Product> products,
			@RequestPart(value = "image", required = false) List<MultipartFile> files) {

		ResponseEntity<List<Product>> entity = null;

//		if (service.addProductByCategory(categoryName, product, files)) {
//			entity = new ResponseEntity<Product>(product, HttpStatus.CREATED);
//		} else {
//			entity = new ResponseEntity<Product>(product, HttpStatus.BAD_REQUEST);
//		}

		return entity;
	}

	// 查 --> 根據商品編號查找商品
	@GetMapping("/product/{productId}")
	public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
		Product product = service.getProductById(productId);
		ResponseEntity<Product> entity;
		if (product != null) {
			entity = new ResponseEntity<Product>(product, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<Product>(product, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 修 --> 根據商品編號修改商品
	@PutMapping("/product/{productId}")
	public ResponseEntity<?> updateProductById(@PathVariable Long productId, @Valid @RequestBody Product product) {

		Product p = service.getProductById(productId);

		if (p != null) {
			product.setProductId(p.getProductId());

			if (service.updateProductById(p.getProductId(), product)) {
				return new ResponseEntity<>(product, HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Service unavailable. Could not update product.",
						HttpStatus.SERVICE_UNAVAILABLE);
			}
		} else {
			return new ResponseEntity<>("Product not found.", HttpStatus.BAD_REQUEST);
		}
	}

	// 查
	@GetMapping("/products")
	public ResponseEntity<List<Product>> getAllProducts() {
		List<Product> products = service.getAllProducts();
		ResponseEntity<List<Product>> entity;
		if (products.size() > 0) {
			entity = new ResponseEntity<List<Product>>(products, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<List<Product>>(products, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 查
	@GetMapping("/{categoryName}/products")
	public ResponseEntity<List<Product>> getAllProductsByCategoryName(@PathVariable String categoryName) {
		List<Product> products = service.getAllProductsByCategory(categoryName);
		ResponseEntity<List<Product>> entity;
		if (products.size() > 0) {
			entity = new ResponseEntity<List<Product>>(products, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<List<Product>>(products, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 模糊查詢
	@GetMapping("/products/{keyword}")
	public ResponseEntity<List<Product>> getProductsByKeyword(@PathVariable String keyword) {
		List<Product> products = service.searchProductsByKeyword(keyword);
		ResponseEntity<List<Product>> entity;
		if (products.size() > 0) {
			entity = new ResponseEntity<List<Product>>(products, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<List<Product>>(products, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 刪
	@DeleteMapping("/product/{productId}")
	public ResponseEntity<Product> deleteProductById(@PathVariable Long productId) {
		ResponseEntity<Product> entity;
		Product product = service.getProductById(productId);
		if (product != null) {
			if (service.deleteProductById(product.getProductId())) {
				entity = new ResponseEntity<Product>(product, HttpStatus.OK);
			} else {
				entity = new ResponseEntity<Product>(product, HttpStatus.SERVICE_UNAVAILABLE);
			}
		} else {
			entity = new ResponseEntity<Product>(product, HttpStatus.NOT_FOUND);
		}
		return entity;
	}
}
