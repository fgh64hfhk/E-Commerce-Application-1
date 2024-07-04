package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.entities.Product;
import com.app.payloads.ProductDto;
import com.app.payloads.ProductVariantDto;
import com.app.service.ProductService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
@CrossOrigin
public class ProductController {

	@Autowired
	private ProductService service;

	// 根據商品種類的名稱新增商品，接受最少一項變體，並且使用 multipart/form-data 資料型態
	@PostMapping(path = "/admin/categories/{categoryName}/product", consumes = { "multipart/form-data" })
	public ResponseEntity<ProductDto> createProductByCategoryName(@PathVariable String categoryName,
			@RequestParam("metadata") ProductDto productDto,
			@RequestParam(value = "images", required = false) List<MultipartFile> files) {

		ResponseEntity<ProductDto> entity = null;

		try {
			// 處理商品變體資料，例如驗證和其他必要的處理
			List<ProductVariantDto> productVariantDtos = productDto.getProductList();
			if (productVariantDtos.isEmpty()) {
				return ResponseEntity.badRequest().body(null); // 如果沒有變體，返回錯誤訊息
			}

			// 處理圖片上傳，如果有上傳圖片的話
			// 您可以進行圖片上傳處理，例如存儲到本地或者其他存儲服務中

			Product product = new Product(productDto);

			Boolean isAddSuccessful = service.addProductByCategory(categoryName, product, files);

			if (isAddSuccessful) {
				entity = new ResponseEntity<>(productDto, HttpStatus.CREATED);
			} else {
				entity = new ResponseEntity<>(productDto, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			entity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return entity;
	}

	// 查 --> 根據商品編號查找商品
	@GetMapping("/admin/product/{productId}")
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
	@PutMapping("/admin/product/{productId}")
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
	@GetMapping("/admin/products")
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
	@GetMapping("/public/categories/{categoryName}/products")
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
	@GetMapping("/public/products/{keyword}")
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
	@DeleteMapping("/public/product/{productId}")
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
