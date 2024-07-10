package com.app.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.entities.ProductVariant;
import com.app.payloads.ProductVariantDto;
import com.app.service.FileService;
import com.app.service.ProductVariantService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
@CrossOrigin
public class ProductVariantController {

	@Autowired
	private ProductVariantService productVariantService;

	@Autowired
	private FileService fileService;

	@Value("${project.image}")
	private String path;

	// 增 --> 根據商品編號新增商品變體
	@PostMapping(path = "/admin/product/{productId}/variant", consumes = { "multipart/form-data" })
	public ResponseEntity<ProductVariantDto> createProductVariantByProductId(@PathVariable Long productId,
			@Valid @RequestParam("variant") ProductVariantDto productVariantDto,
			@RequestParam(value = "image", required = false) MultipartFile file) {

		ResponseEntity<ProductVariantDto> entity;

		ProductVariant productVariant = new ProductVariant(productVariantDto);

		Boolean isCreate = productVariantService.addProductVariantByProductId(productId, productVariant, file);

		if (isCreate) {
			entity = new ResponseEntity<>(productVariantDto, HttpStatus.CREATED);
		} else {
			entity = new ResponseEntity<>(productVariantDto, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 增 --> 根據商品編號新增商品變體列表
	@PostMapping(path = "/admin/product/{productId}/variants", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<List<ProductVariantDto>> createProductVariantsByProductId(@PathVariable Long productId,
			@Valid @RequestParam("variants") List<ProductVariantDto> productVariantDtos,
			@RequestParam(value = "images", required = false) List<MultipartFile> files) {

		ResponseEntity<List<ProductVariantDto>> entity;

		List<ProductVariant> productVariants = productVariantDtos.stream().map(t -> new ProductVariant(t))
				.collect(Collectors.toList());

		Boolean isCreate = productVariantService.addProductVariantsByProductId(productId, productVariants, files);

		if (isCreate) {
			entity = new ResponseEntity<>(productVariantDtos, HttpStatus.CREATED);
		} else {
			entity = new ResponseEntity<>(productVariantDtos, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 刪除 --> 根據商品變體的識別碼刪除商品變體
	@DeleteMapping("/admin/product/variant/{sku}")
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

	@PutMapping(value = "/admin/product/variant/{sku}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<ProductVariantDto> updateProductVariantBySku(@PathVariable String sku,
			@RequestPart(name = "variant", required = true) ProductVariantDto productVariantDto,
			@RequestPart(name = "image", required = false) MultipartFile file) {

		ResponseEntity<ProductVariantDto> entity;

		try {
			if (file != null && !file.isEmpty()) {
	            String fileName = fileService.uploadImage(path, file);
	            InputStream in = fileService.getResource(path, fileName);
	            System.out.println(in);
	        }
			
			System.out.println("controller: " + productVariantDto);

			ProductVariant productVariant = new ProductVariant(productVariantDto);
			
			System.out.println("controller: " + productVariant);

			ProductVariant updatedProductVariant = productVariantService.updateProductVariantBySku(sku, productVariant,
					file);

			ProductVariantDto updatedProductVariantDto = new ProductVariantDto(updatedProductVariant);

			if (updatedProductVariant != null) {
				entity = new ResponseEntity<>(updatedProductVariantDto, HttpStatus.CREATED);
			} else {
				entity = new ResponseEntity<>(updatedProductVariantDto, HttpStatus.BAD_REQUEST);
			}
			return entity;
		} catch (IOException e) {

			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// 查詢所有變體
	@GetMapping("/public/product/{productId}/variants")
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
	@GetMapping("/public/product/{productId}/variant/colors")
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
	@GetMapping("/public/product/{productId}/variant/sizes")
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
	@GetMapping("/public/product/{productId}/variant/images")
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
	@GetMapping("/public/product/variant/{sku}/image")
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
	@GetMapping("/public/product/variant/{sku}/inventory")
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
