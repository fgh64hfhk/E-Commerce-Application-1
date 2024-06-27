package com.app.service;

import java.io.IOException;
import java.util.ArrayList;
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

	@Override
	public boolean addProductByCategory(String categoryName, Product product, List<MultipartFile> files) {

		// 1. 根據種類名稱查找是否存在該商品種類
		ProductCategory category = categoryRepository.findByCategoryName(categoryName);

		if (category != null) {
			product.setCategory(category);
		} else {
			System.out.println("Product is not found...");
			return false;
		}

		// 2. 新增商品時最少需要一筆變體資料
		List<ProductVariant> pv = product.getProductVariants();

		if (pv.isEmpty()) {
			System.out.println("Product variants is empty...");
			return false;
		}

		// 3. 遊歷所有的商品變體
		for (int i = 0; i < pv.size(); i++) {

			// 處理商品變體
			ProductVariant productVariant = pv.get(i);

			MultipartFile multipartFile = null;
			if (files != null && i < files.size()) {
				// 取得對應的上傳圖片
				multipartFile = files.get(i);
			}

			if (multipartFile != null) {
				// 該商品變體有對應的上傳圖片 --> 進行 Base64 的編碼
				byte[] imageBytes;
				try {
					imageBytes = multipartFile.getBytes();
					String base64Image = Base64.getEncoder().encodeToString(imageBytes);
					String mimeType = determineMimeType(imageBytes);
					String dateUrl = "data:" + mimeType + ";base64," + base64Image;
					productVariant.setImage(dateUrl);
				} catch (IOException e) {
					System.out.println("Failed to process uploaded image: " + e.getMessage());
					// 可以根據實際情況進行錯誤處理
				}
			} else {
				// 該商品變體沒有對應的上傳圖片 --> 先從網站鏈結下載圖片串流並進行 Base64 的編碼
				String imageUrl = productVariant.getImage();
				if (imageUrl == null) {
					// 如果商品變體內沒有提供練節則使用預設圖片
					productVariant.setImage("image_" + product.getName());
				} else {
					try {
						RestTemplate restTemplate = new RestTemplate();
						HttpHeaders headers = new HttpHeaders();
						headers.add("User-Agent",
								"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
						headers.add("Referer", "https://shoplineimg.com");

						HttpEntity<String> entity = new HttpEntity<>(headers);

						ResponseEntity<byte[]> response = restTemplate.exchange(imageUrl, HttpMethod.GET, entity,
								byte[].class);

						if (response.getStatusCode().is2xxSuccessful()) {
							byte[] imageBytes = response.getBody();
							String base64Image = Base64.getEncoder().encodeToString(imageBytes);
							String mimeType = determineMimeType(imageBytes);
							String dateUrl = "data:" + mimeType + ";base64," + base64Image;
							productVariant.setImage(dateUrl);
						} else {
							System.out.println("Failed to download image, HTTP status: " + response.getStatusCode());
						}
					} catch (Exception e) {
						System.out.println("Failed to download image: " + e.getMessage());
						// 可以根據實際情況進行錯誤處理
					}
				}
			}
		}

		// 創建一個新列表以避免 ConcurrentModifycationException
		List<ProductVariant> temp = new ArrayList<>(pv);

		// 修改總庫存數量
		Integer totalQuantity = 0;
		for (ProductVariant productVariant : temp) {
			totalQuantity += productVariant.getInventory();
			product.addProductVariant(productVariant);
		}
		product.setTotalQuantity(totalQuantity);

		try {
			Product savedProduct = productRepository.save(product);
			return savedProduct != null;
		} catch (Exception e) {
			System.out.println("Failed to save product: " + e.getMessage());
			return false;
		}
	}

	private String determineMimeType(byte[] imageBytes) {
		/*
		 * determineMimeType 方法用於從字節數組中確定圖像的 MIME 類型。 這是通過檢查文件表頭 magic number
		 * 來實現，每種文件類型都有自己特定的文件表頭 雖然這種方法在處理常見的圖像類型時比較有效，但在實際生產環境中，建議使用更強大的套件庫 Apache Tika
		 * 來處理更多類型的文件
		 */
		if (imageBytes.length > 4 &&
				imageBytes[0] == (byte) 0xFF &&
				imageBytes[1] == (byte) 0xD8 &&
				imageBytes[2] == (byte) 0xFF) {
			return "image/jpeg";
		} else if (imageBytes.length > 8 &&
				imageBytes[0] == (byte) 0x89 &&
				imageBytes[1] == (byte) 0x50 &&
				imageBytes[2] == (byte) 0x4E &&
				imageBytes[3] == (byte) 0x47) {
			return "image/png";
		} else if (imageBytes.length > 4 &&
				imageBytes[0] == (byte) 0x47 &&
				imageBytes[1] == (byte) 0x49 &&
				imageBytes[2] == (byte) 0x46) {
			return "image/gif";
		} else if (imageBytes.length > 4 && 
				imageBytes[0] == (byte) 0x52 && 
				imageBytes[1] == (byte) 0x49 && 
				imageBytes[2] == (byte) 0x46 &&
				imageBytes[3] == (byte) 0x46) {
			return "image/webp";
		} else {
			return "application/octet-stream"; // Default fallback
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

		Product getProduct = getProductById(productId);

		if (getProduct != null) {
			ProductCategory getCategory = getProduct.getCategory();
			List<ProductVariant> getVariants = getProduct.getProductVariants();

			product.setProductId(getProduct.getProductId());
			product.setCategory(getCategory);
			product.setProductVariants(getVariants);
			product.setTotalQuantity(getProduct.getTotalQuantity());
			productRepository.save(product);
			return true;
		} else {
			return false;
		}

	}

	@Override
	public Product getProductById(Long productId) {
		Optional<Product> getProduct = productRepository.findById(productId);
		if (getProduct.isPresent()) {
			return getProduct.get();
		} else {
			return null;
		}
	}

	@Override
	public List<Product> getAllProducts() {
		List<Product> products = productRepository.findAll();
		if (products.size() > 0) {
			return products;
		} else {
			return null;
		}
	}

	@Override
	public List<Product> getAllProductsByCategory(String category) {
		String sql = "SELECT * FROM products p JOIN product_categories pc ON p.category_id = pc.category_id WHERE pc.category_name = :category";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("category", category);
		List<Product> products = jdbc.query(sql, params, new ProductRowMapper());
		return products;
	}

	@Override
	public List<Product> searchProductsByKeyword(String keyword) {
		String sql = "SELECT * FROM products p WHERE p.product_name LIKE :keyword";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("keyword", "%" + keyword + "%"); // 添加百分號來進行模糊匹配
		List<Product> products = jdbc.query(sql, params, new ProductRowMapper());
		return products;
	}

}
