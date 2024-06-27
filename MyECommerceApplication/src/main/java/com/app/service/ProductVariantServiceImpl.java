package com.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import com.app.entities.ProductVariant;
import com.app.repositories.ProductRepository;
import com.app.repositories.ProductVariantRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductVariantServiceImpl implements ProductVariantService {

	@Autowired
	private NamedParameterJdbcTemplate jdbc;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	ProductVariantRepository productVariantRepository;

	@Override
	public ProductVariant deleteProductVariantBySku(String sku) {
		ProductVariant getProductVariant = productVariantRepository.findBySku(sku);

		if (getProductVariant != null) {
			Product product = getProductVariant.getProduct();
			Integer newQuantity = product.getTotalQuantity() - getProductVariant.getInventory();
			product.removeProductVariant(getProductVariant);
			product.setTotalQuantity(newQuantity);
			productVariantRepository.delete(getProductVariant);
			productRepository.save(product);
			return getProductVariant;
		} else {
			return null;
		}
	}

	@Override
	public ProductVariant updateProductVariantBySku(String sku, ProductVariant productVariant, MultipartFile file) {

		// 根據商品變體的識別碼查找該商品變體
		ProductVariant getProductVariant = productVariantRepository.findBySku(sku);

		if (getProductVariant == null) {
			return null;
		}

		Product getProduct = getProductVariant.getProduct();

		// 只允許修改庫存數量與更換圖片，因為如果修改顏色或者尺寸表示該變體的識別碼將無效，視為修改其他的變體
		// 檢查是否違反規則
		if (!getProductVariant.getSku().equals(productVariant.getSku())) {
			System.out.println("識別碼不相同，請確認是否修改此變體");
			return null;
		} else if (!getProductVariant.getColor().equals(productVariant.getColor())) {
			System.out.println("不可修改顏色，請使用正確的識別碼查找");
			return null;
		} else if (!getProductVariant.getSize().equals(productVariant.getSize())) {
			System.out.println("不可以修改尺寸，請使用正確的識別碼查找");
			return null;
		} else {
			// 確定要修改此變體
			ProductVariant returnProductVariant = setImageWithBase64(productVariant, file);

			// 更新庫存數量並調整商品總庫存數量
			Integer originalInventory = getProductVariant.getInventory();
			Integer newInventory = productVariant.getInventory();
			Integer totalQuantity = getProduct.getTotalQuantity() - originalInventory + newInventory;

			returnProductVariant.setInventory(newInventory);
			getProduct.setTotalQuantity(totalQuantity);

			// 保存更新後的商品變體
			ProductVariant savedProductVariant = productVariantRepository.save(returnProductVariant);
			// 保存更新後的商品
			getProduct.removeProductVariant(getProductVariant);
			getProduct.addProductVariant(savedProductVariant);
			productRepository.save(getProduct);

			System.out.println("商品變體更新成功，識別碼為： " + sku);
			return savedProductVariant;
		}
	}

	@Override
	public List<ProductVariant> getAllProductVariantsByProductId(Long productId) {
		List<ProductVariant> list;
		Optional<Product> optional = productRepository.findById(productId);
		if (optional.isPresent()) {
			list = new ArrayList<>(optional.get().getProductVariants());
		} else {
			list = null;
		}
		return list;
	}

	@Override
	public Set<String> getAllColorsByProductId(Long productId) {

		Set<String> colors = new HashSet<>();

		String sql = "select distinct pv.color from product_variants pv join products p on p.product_id = pv.product_id where p.product_id = :productId";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("productId", productId);

		List<String> colorList = jdbc.query(sql, params, (rs, rowNum) -> rs.getString("color"));

		colors.addAll(colorList);

		return colors;
	}

	@Override
	public Set<String> getAllSizesByProductId(Long productId) {

		Set<String> sizes = new HashSet<>();

		String sql = "select distinct pv.size from product_variants pv join products p on p.product_id = pv.product_id where p.product_id = :productId";

		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("productId", productId);

		List<String> sizeList = jdbc.query(sql, params, (rs, rowNum) -> rs.getString("size"));

		sizes.addAll(sizeList);

		return sizes;
	}

	@Override
	public List<String> getAllImagesByProductId(Long productId) {

		List<String> images;

		String sql = "select distinct pv.image from product_variants pv join products p on p.product_id = pv.product_id where p.product_id = :productId";

		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("productId", productId);

		images = jdbc.query(sql, params, (rs, rowNum) -> rs.getString("image"));

		return images;
	}

	@Override
	public String getImageBySku(String sku) {

		String sql = "select pv.image from product_variants pv where pv.sku = :sku";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("sku", sku);

		String image = jdbc.queryForObject(sql, params, (rs, rowNum) -> rs.getString("image"));

		return image;
	}

	@Override
	public boolean addProductVariantByProductId(Long productId, ProductVariant productVariant,
			MultipartFile multipartFile) {

		// 根據商品編號查找是否存在該商品
		Optional<Product> optional = productRepository.findById(productId);

		// 如果有找到該商品編號對應的商品
		if (optional.isPresent()) {

			Product getProduct = optional.get();
			
			Integer totalQuantity = getProduct.getTotalQuantity();

			// 取出已存在的商品變體
			List<ProductVariant> variants = getProduct.getProductVariants();

			// 顯示待新增的變體
			System.out.println("product variant: " + productVariant);

			// 檢查是否存在相同的識別碼
			Optional<ProductVariant> existingVariant = variants.stream()
					.filter(v -> v.getSku().equals(productVariant.getSku())).findFirst(); // 如果存在相同的識別碼

			if (existingVariant.isPresent()) {
				// 如果存在匹配的變體，則處理變體
				ProductVariant foundVariant = existingVariant.get();
				// 減去原有的數量
				totalQuantity -= foundVariant.getInventory();
				// 將舊的變體移除
				getProduct.removeProductVariant(foundVariant);
				// 累加變體的庫存
				int newInventory = foundVariant.getInventory() + productVariant.getInventory();
				// 使用修改圖片的方法
				foundVariant = setImageWithBase64(productVariant, multipartFile);

				foundVariant.setInventory(newInventory);

				totalQuantity += foundVariant.getInventory();

				// 更新已存在的變體
				productVariantRepository.save(foundVariant);
				System.out.println("Existing variant found and inventory updated: " + foundVariant);
				
				getProduct.addProductVariant(foundVariant);
			} else {
				// 如果不存在匹配的變體，則新增一筆變體
				ProductVariant updatedVariant = setImageWithBase64(productVariant, multipartFile);
				totalQuantity += updatedVariant.getInventory();
				productVariantRepository.save(updatedVariant);
				getProduct.addProductVariant(updatedVariant);
			}

			getProduct.setTotalQuantity(totalQuantity);

			Product savedProduct = productRepository.save(getProduct);
			System.out.println("saved product: " + savedProduct.getProductVariants());

			return savedProduct != null;
		} else {
			return false;
		}
	}

	@Override
	public boolean addProductVariantsByProductId(Long productId, List<ProductVariant> productVariants, List<MultipartFile> multipartFiles) {

		// 根據商品編號查找是否存在該商品
		Optional<Product> optional = productRepository.findById(productId);

		if (optional.isPresent()) {
			Product getProduct = optional.get();

			// 輸出待添加的商品變體資訊
			System.out.println("product variants: " + productVariants);

			// 宣告暫時累加的庫存數量
			Integer totalQuantity = getProduct.getTotalQuantity();

			// 遊歷新添加的商品變體列表
			for (int i = 0; i < productVariants.size(); i++) {
				
				ProductVariant newVariant = productVariants.get(i);
				
				// 查找是否已存在相同識別碼的變體
				Optional<ProductVariant> existingVariant = getProduct.getProductVariants().stream()
						.filter(v -> v.getSku().equals(newVariant.getSku())).findFirst();

				if (existingVariant.isPresent()) {
					// 獲取相同識別碼的變體物件
					ProductVariant foundVariant = existingVariant.get();
					
					// 如果已存在相同的識別碼的變體，則變更圖片
					if (multipartFiles != null) {
						foundVariant = setImageWithBase64(foundVariant, multipartFiles.get(i));
					} else {
						foundVariant = setImageWithBase64(foundVariant, null);
					}
					
					// 如果已存在相同的識別碼的變體，則累加庫存
					int newInventory = foundVariant.getInventory() + newVariant.getInventory();
					foundVariant.setInventory(newInventory);
					
					// 更新已存在的變體
					productVariantRepository.save(foundVariant);
					System.out.println("Existing variant found and inventory updated: " + foundVariant);
				} else {
					// 如果不存在相同的識別碼的變體，則創建一個新的變體物件
					ProductVariant variantToSave = newVariant;
					// 處理圖片
					if (multipartFiles != null) {
						variantToSave = setImageWithBase64(variantToSave, multipartFiles.get(i));
					} else {
						variantToSave = setImageWithBase64(variantToSave, null);
					}
					// 存儲
					productVariantRepository.save(variantToSave);
					getProduct.addProductVariant(variantToSave);
					System.out.println("New variant added: " + variantToSave);
				}
				// 累加總庫存的數量
				totalQuantity += newVariant.getInventory();
			}

			// 更新商品的總庫存數量
			getProduct.setTotalQuantity(totalQuantity);
			Product savedProduct = productRepository.save(getProduct);
			System.out.println("saved product: " + savedProduct.getProductVariants());

			return savedProduct != null;
		} else {
			return false;
		}
	}
	
	private ProductVariant setImageWithBase64(ProductVariant foundVariant, MultipartFile multipartFile) {
		
		// 處理圖片
		if (multipartFile != null) {
			// 該商品變體有對應的上傳圖片 --> 進行 Base64 的編碼
			byte[] imageBytes;
			try {
				imageBytes = multipartFile.getBytes();
				String base64Image = Base64.getEncoder().encodeToString(imageBytes);
				String mimeType = determineMimeType(imageBytes);
				String dateUrl = "data:" + mimeType + ";base64," + base64Image;
				foundVariant.setImage(dateUrl);
			} catch (IOException e) {
				System.out.println("Failed to process uploaded image: " + e.getMessage());
				// 可以根據實際情況進行錯誤處理
				foundVariant.setImage("image_file_error_" + foundVariant.getSku());
			}
		} else {
			// 該商品變體沒有對應的上傳圖片 --> 先從網站鏈結下載圖片串流並進行 Base64 的編碼
			String imageUrl = foundVariant.getImage();
			if (imageUrl == null) {
				// 如果商品變體內沒有提供練節則使用預設圖片
				foundVariant.setImage("image_not_url_" + foundVariant.getSku());
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
						foundVariant.setImage(dateUrl);
					} else {
						System.out
								.println("Failed to download image, HTTP status: " + response.getStatusCode());
						foundVariant.setImage("image_bad_request_" + foundVariant.getSku());
					}
				} catch (Exception e) {
//					e.printStackTrace();
					System.out.println("Failed to download image: " + e.getMessage());
					// 可以根據實際情況進行錯誤處理
					foundVariant.setImage("image_not_absolute_url_" + foundVariant.getSku());
				}
			}
		}
		return foundVariant;
	}
	
	private String determineMimeType(byte[] imageBytes) {
		/*
		 * determineMimeType 方法用於從字節數組中確定圖像的 MIME 類型。 這是通過檢查文件表頭 magic number
		 * 來實現，每種文件類型都有自己特定的文件表頭 雖然這種方法在處理常見的圖像類型時比較有效，但在實際生產環境中，建議使用更強大的套件庫 Apache Tika
		 * 來處理更多類型的文件
		 */
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
			return "application/octet-stream"; // Default fallback
		}
	}

	
	@Override
	public Integer getProductVariantInventoryBySku(String sku) {
		ProductVariant pv = productVariantRepository.findBySku(sku);
		if (pv != null) {
			return pv.getInventory();
		} else {
			return null;
		}
	}

}
