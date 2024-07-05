package com.app.service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.app.entities.Product;
import com.app.entities.ProductCategory;
import com.app.entities.ProductVariant;
import com.app.mapper.ProductRowMapper;
import com.app.repositories.CategoryRepository;
import com.app.repositories.ProductRepository;
import com.app.repositories.ProductVariantRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

	@Autowired
	private NamedParameterJdbcTemplate jdbc;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductVariantRepository productVariantRepository;

	@Override
	public boolean addProductByCategory(String categoryName, Product product, List<MultipartFile> files) {

		// 根據種類名稱查找是否存在該商品種類
		ProductCategory category = categoryRepository.findByCategoryName(categoryName);
		if (category == null) {
			System.out.println("Product category not found...");
			return false;
		}
		product.setCategory(category);

		// 新增商品時最少需要一筆變體資料
		List<ProductVariant> productVariants = product.getProductVariants();
		if (productVariants.isEmpty()) {
			System.out.println("Product variants is empty...");
			return false;
		}

		int totalQuantity = 0;
		// 處理商品變體圖片和庫存
		for (int i = 0; i < productVariants.size(); i++) {
			ProductVariant productVariant = productVariants.get(i);
			handleProductVariantImage(productVariant, files, i);
			totalQuantity += productVariant.getInventory(); // 更新總庫存
			product.addProductVariant(productVariant);
		}
		product.setTotalQuantity(totalQuantity); // 設置商品總庫存

		try {
			Product savedProduct = productRepository.save(product);
			for (ProductVariant productVariant : savedProduct.getProductVariants()) {
				productVariant.setProduct(savedProduct);
				productVariantRepository.save(productVariant);
			}
			return savedProduct != null;
		} catch (Exception e) {
			System.out.println("Failed to save product: " + e.getMessage());
			return false;
		}
	}

	private void handleProductVariantImage(ProductVariant productVariant, List<MultipartFile> files, int index) {
		MultipartFile multipartFile = (files != null && index < files.size()) ? files.get(index) : null;
		if (multipartFile != null) {
			try {
				// 該商品變體有對應的上傳圖片 --> 進行 Base64 的編碼
				byte[] imageBytes = multipartFile.getBytes();
				productVariant.setImage(encodeImageToBase64(imageBytes));
			} catch (IOException e) {
				System.out.println("Failed to process uploaded image: " + e.getMessage());
			}
		} else {
			// 該商品變體沒有對應的上傳圖片 --> 先從網站鏈結下載圖片串流並進行 Base64 的編碼
			String imageUrl = productVariant.getImage();
			if (imageUrl == null) {
				// 如果商品變體內沒有提供練節則使用預設圖片
				productVariant.setImage("image_" + productVariant.getProduct().getName());
			} else {
				downloadAndSetImage(productVariant, imageUrl);
			}
		}
	}

	private void downloadAndSetImage(ProductVariant productVariant, String imageUrl) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
			headers.add("Referer", "https://shoplineimg.com");

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<byte[]> response = restTemplate.exchange(imageUrl, HttpMethod.GET, entity, byte[].class);

			if (response.getStatusCode().is2xxSuccessful()) {
				byte[] imageBytes = response.getBody();
				productVariant.setImage(encodeImageToBase64(imageBytes));
			} else {
				System.out.println("Failed to download image, HTTP status: " + response.getStatusCode());
				productVariant
						.setImage("image_" + productVariant.getProduct().getName() + "_" + response.getStatusCode());
			}
		} catch (Exception e) {
			System.out.println("Failed to download image: " + e.getMessage());
			productVariant.setImage("image_" + productVariant.getProduct().getName() + "_" + e.getMessage());
		}
	}

	private String encodeImageToBase64(byte[] imageBytes) {
		String base64Image = Base64.getEncoder().encodeToString(imageBytes);
		String mimeType = determineMimeType(imageBytes);
		String dateUrl = "data:" + mimeType + ";base64," + base64Image;
		return dateUrl;
	}

	private String determineMimeType(byte[] imageBytes) {
		if (imageBytes.length > 4 && imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8
				&& imageBytes[2] == (byte) 0xFF) {
			return "image/jpeg";
		} else if (imageBytes.length > 8 && imageBytes[0] == (byte) 0x89 && imageBytes[1] == (byte) 0x50
				&& imageBytes[2] == (byte) 0x4E && imageBytes[3] == (byte) 0x47) {
			return "image/png";
		} else if (imageBytes.length > 4 && imageBytes[0] == (byte) 0x47 && imageBytes[1] == (byte) 0x49
				&& imageBytes[2] == (byte) 0x46) {
			return "image/gif";
		} else if (imageBytes.length > 4 && imageBytes[0] == (byte) 0x52 && imageBytes[1] == (byte) 0x49
				&& imageBytes[2] == (byte) 0x46 && imageBytes[3] == (byte) 0x46) {
			return "image/webp";
		} else {
			return "application/octet-stream";
		}
	}

	@Override
	public boolean deleteProductById(Long productId) {
		Product product = getProductById(productId);
		if (product != null) {
			productRepository.delete(product);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean updateProductById(Long productId, Product product) {
		Product existingProduct = getProductById(productId);
		if (existingProduct != null) {
			product.setProductId(existingProduct.getProductId());
			product.setCategory(existingProduct.getCategory());
			product.setProductVariants(existingProduct.getProductVariants());
			product.setTotalQuantity(existingProduct.getTotalQuantity());
			productRepository.save(product);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Product getProductById(Long productId) {
		Optional<Product> product = productRepository.findById(productId);
		return product.orElse(null);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public List<Product> getAllProductsByCategory(String category) {
		String sql = "SELECT * FROM products p JOIN product_categories pc ON p.category_id = pc.category_id WHERE pc.category_name = :category";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("category", category);
		return jdbc.query(sql, params, new ProductRowMapper());
	}

	@Override
	public List<Product> searchProductsByKeyword(String keyword) {
		String sql = "SELECT * FROM products p WHERE p.product_name LIKE :keyword";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("keyword", "%" + keyword + "%");
		return jdbc.query(sql, params, new ProductRowMapper());
	}

}
