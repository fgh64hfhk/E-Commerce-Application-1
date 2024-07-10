package com.app.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.app.entities.ProductVariant;

public interface ProductVariantService {

	// 根據商品編號新增商品變體
	boolean addProductVariantByProductId(Long productId, ProductVariant productVariant, MultipartFile multipartFile);

	// 根據商品編號新增商品變體列表
	boolean addProductVariantsByProductId(Long productId, List<ProductVariant> productVariants,
			List<MultipartFile> multipartFiles);

	// 根據商品變體的識別碼刪除商品變體
	ProductVariant deleteProductVariantBySku(String sku);

	// 根據商品變體的識別碼修改商品變體
	ProductVariant updateProductVariantBySku(String sku, ProductVariant productVariant, MultipartFile file) throws IOException;

	// 根據商品變體的識別碼修改商品變體的圖片

	// 根據商品變體的識別碼查詢庫存數量
	Integer getProductVariantInventoryBySku(String sku);

	// 根據商品編號查詢所有的商品變體
	List<ProductVariant> getAllProductVariantsByProductId(Long productId);

	// 根據商品編號查詢所有的變體顏色
	Set<String> getAllColorsByProductId(Long productId);

	// 根據商品編號查詢所有的變體尺寸
	Set<String> getAllSizesByProductId(Long productId);

	// 根據商品編號查詢所有的變體圖片
	List<String> getAllImagesByProductId(Long productId);

	// 根據商品變體的識別碼查詢該變體圖片
	String getImageBySku(String sku);

	// 根據商品編號查詢商品描述
//	String getDescribtionByProductId(Long productId);
}
