package com.app.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.entities.ProductVariant;
import com.app.service.ProductVariantService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ProductVariantController {

	@Autowired
	private ProductVariantService productVariantService;

	// 增 --> 根據商品編號新增商品變體
	@PostMapping("/product/{productId}/variant")
	public ResponseEntity<ProductVariant> createProductVariantByProductId(@PathVariable Long productId,
			@Valid @RequestPart("variant") ProductVariant productVariant,
			@RequestPart(required = false, value = "image") MultipartFile file) {

		ResponseEntity<ProductVariant> entity;

		if (productVariantService.addProductVariantByProductId(productId, productVariant, file)) {
			entity = new ResponseEntity<ProductVariant>(productVariant, HttpStatus.CREATED);
		} else {
			entity = new ResponseEntity<ProductVariant>(productVariant, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 增 --> 根據商品編號新增商品變體列表
	@PostMapping("/product/{productId}/variants")
	public ResponseEntity<List<ProductVariant>> createProductVariantsByProductId(@PathVariable Long productId,
			@Valid @RequestPart("variants") List<ProductVariant> productVariants,
			@RequestPart(required = false, value = "image") List<MultipartFile> files) {

		ResponseEntity<List<ProductVariant>> entity;

		if (productVariantService.addProductVariantsByProductId(productId, productVariants, files)) {
			entity = new ResponseEntity<List<ProductVariant>>(productVariants, HttpStatus.CREATED);
		} else {
			entity = new ResponseEntity<List<ProductVariant>>(productVariants, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 刪除 --> 根據商品變體的識別碼刪除商品變體
	@DeleteMapping("/product/variant/{sku}")
	public ResponseEntity<ProductVariant> deleteProductVariantBySku(@PathVariable String sku) {

		ResponseEntity<ProductVariant> entity;

		ProductVariant deletedProductVariant = productVariantService.deleteProductVariantBySku(sku);

		if (deletedProductVariant != null) {
			entity = new ResponseEntity<ProductVariant>(deletedProductVariant, HttpStatus.CREATED);
		} else {
			entity = new ResponseEntity<ProductVariant>(deletedProductVariant, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 更新
	@PutMapping("/product/variant/{sku}")
	public ResponseEntity<ProductVariant> updateProductVariantBySku(@PathVariable String sku,
			@Valid @RequestPart("variant") ProductVariant productVariant,
			@RequestPart(required = false, value = "image") MultipartFile file) {

		ResponseEntity<ProductVariant> entity;

		ProductVariant updatedProductVariant = productVariantService.updateProductVariantBySku(sku, productVariant, file);

		if (updatedProductVariant != null) {
			entity = new ResponseEntity<ProductVariant>(updatedProductVariant, HttpStatus.CREATED);
		} else {
			entity = new ResponseEntity<ProductVariant>(updatedProductVariant, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 查詢所有變體
	@GetMapping("/product/{productId}/variants")
	public ResponseEntity<List<ProductVariant>> getAllProductVariantsByProductId(@PathVariable Long productId) {

		ResponseEntity<List<ProductVariant>> entity;
		List<ProductVariant> productVariants = productVariantService.getAllProductVariantsByProductId(productId);

		if (productVariants.size() > 0) {
			entity = new ResponseEntity<List<ProductVariant>>(productVariants, HttpStatus.CREATED);
		} else {
			entity = new ResponseEntity<List<ProductVariant>>(productVariants, HttpStatus.BAD_REQUEST);
		}

		return entity;
	}

	// 查詢顏色表
	@GetMapping("/product/{productId}/variant/colors")
	public ResponseEntity<Set<String>> getAllColorsByProductId(@PathVariable Long productId) {

		ResponseEntity<Set<String>> entity;
		Set<String> colors = productVariantService.getAllColorsByProductId(productId);

		if (colors.size() > 0) {
			entity = new ResponseEntity<Set<String>>(colors, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<Set<String>>(colors, HttpStatus.BAD_REQUEST);
		}

		return entity;
	}

	// 查詢尺寸表
	@GetMapping("/product/{productId}/variant/sizes")
	public ResponseEntity<Set<String>> getAllSizesByProductId(@PathVariable Long productId) {

		ResponseEntity<Set<String>> entity;
		Set<String> sizes = productVariantService.getAllSizesByProductId(productId);

		if (sizes.size() > 0) {
			entity = new ResponseEntity<Set<String>>(sizes, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<Set<String>>(sizes, HttpStatus.BAD_REQUEST);
		}

		return entity;
	}

	// 查詢圖片列表
	@GetMapping("/product/{productId}/variant/images")
	public ResponseEntity<List<String>> getAllImagesByProductId(@PathVariable Long productId) {

		ResponseEntity<List<String>> entity;
		List<String> images = productVariantService.getAllImagesByProductId(productId);

		if (images.size() > 0) {
			entity = new ResponseEntity<List<String>>(images, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<List<String>>(images, HttpStatus.BAD_REQUEST);
		}

		return entity;
	}

	// 查詢圖片
	@GetMapping("/product/variant/{sku}/image")
	public ResponseEntity<String> getImageBySku(@PathVariable String sku) {

		ResponseEntity<String> entity;
		String image = productVariantService.getImageBySku(sku);

		if (image != null) {
			entity = new ResponseEntity<String>(image, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<String>(image, HttpStatus.BAD_REQUEST);
		}

		return entity;
	}

	// 查詢庫存
	@GetMapping("/product/variant/{sku}/inventory")
	public ResponseEntity<Integer> getInventoryBySku(@PathVariable String sku) {
		ResponseEntity<Integer> entity;
		Integer inventory = productVariantService.getProductVariantInventoryBySku(sku);
		if (inventory != null) {
			entity = new ResponseEntity<Integer>(inventory, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<Integer>(inventory, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

}
