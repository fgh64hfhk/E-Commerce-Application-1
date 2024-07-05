package com.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.entities.Product;
import com.app.entities.ProductVariant;
import com.app.entities.Role;
import com.app.entities.User;
import com.app.repositories.RoleRepository;
import com.app.service.ProductService;
import com.app.service.UserService;
import com.app.utilits.JsonParserUtils;
import com.app.utilits.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@SecurityScheme(name = "E-Commerce Application", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class MyECommerceApplication implements CommandLineRunner {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ProductService productService;

	public static void main(String[] args) {
		SpringApplication.run(MyECommerceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// 初始化使用者
		initialize_users();

		// 初始化產品
		initialize_HELMETs();

		initialize_PROTECTIVE_GEARs();
		
		initialize_PROTECTIVE_GLOVESs();
	}

	void initialize_users() {

		// ----- 初始化使用者 -----
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
		String fileName = "users.json";

		// 獲取當前的JSON檔案的陣列
		JSONArray root = JsonParserUtils.readFile(fileName);

		// 獲得資料庫的角色資料
		for (int i = 0; i < root.length(); i++) {

			// 獲取當前的JSON對象
			JSONObject object = root.getJSONObject(i);
			System.out.println("user: " + object);

			// 轉換JSON對象為User對象
			User user = gson.fromJson(object.toString(), User.class);
			System.out.println("user: " + user);

			// 編碼用戶密碼
			String encodePassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(encodePassword);

			// 獲取角色並檢查其有效性
			Set<Role> getRoles = user.getRoles();
			boolean isAdmin = getRoles.stream().anyMatch(t -> t.getRoleName().equalsIgnoreCase("admin"));

			Role roleAdmin = roleRepository.findByRoleName("ADMIN")
					.orElseThrow(() -> new RuntimeException("Role ADMIN not found in database."));

			Role roleUser = roleRepository.findByRoleName("USER")
					.orElseThrow(() -> new RuntimeException("Role USER not found in database."));

			// 清空當前用戶的角色列表
			getRoles.clear();

			// 根據用戶是否是管理員，設置不同的角色
			if (isAdmin) {
				getRoles.addAll(Arrays.asList(roleAdmin, roleUser));
			} else {
				getRoles.add(roleUser);
			}

			user.setRoles(getRoles);

			// 保存使用者對象
			userService.addUser(user);
		}
	}

	void initialize_HELMETs() {

		// ----- 初始化產品 -----
		Gson gson2 = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
		String fileName2 = "安全帽產品資料.json";

		// 獲取當前的JSON檔案的陣列
		JSONArray all_products_json = JsonParserUtils.readFile(fileName2);

		// 遊歷陣列內的所有產品物件
		for (int i = 0; i < all_products_json.length(); i++) {

			// 獲取當前的JSON對象
			JSONObject products_json = all_products_json.getJSONObject(i);

			// 轉換JSON物件為產品物件
			Product product = gson2.fromJson(products_json.toString(), Product.class);

			// 開始解析產品物件內的產品變體
			JSONArray all_variants_json = products_json.getJSONArray("productVariants");

			// 存放產品變體的陣列
			List<ProductVariant> productVariants = new ArrayList<ProductVariant>();

			for (int j = 0; j < all_variants_json.length(); j++) {

				// 獲取當前的JSON對象
				JSONObject variant_json = all_variants_json.getJSONObject(j);

				// 轉換JSON物件為產品變體物件
				ProductVariant productVariant = gson2.fromJson(variant_json.toString(), ProductVariant.class);

				// 新增產品變體到陣列
				productVariants.add(productVariant);
			}

			// 設置產品變體陣列到產品物件
			product.setProductVariants(productVariants);
			
			productService.addProductByCategory("安全帽", product, null);
		}
	}
	
	void initialize_PROTECTIVE_GEARs() {

		// ----- 初始化產品 -----
		Gson gson2 = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
		String fileName2 = "防摔衣產品資料.json";

		// 獲取當前的JSON檔案的陣列
		JSONArray all_products_json = JsonParserUtils.readFile(fileName2);

		// 遊歷陣列內的所有產品物件
		for (int i = 0; i < all_products_json.length(); i++) {

			// 獲取當前的JSON對象
			JSONObject products_json = all_products_json.getJSONObject(i);

			// 轉換JSON物件為產品物件
			Product product = gson2.fromJson(products_json.toString(), Product.class);

			// 開始解析產品物件內的產品變體
			JSONArray all_variants_json = products_json.getJSONArray("productVariants");

			// 存放產品變體的陣列
			List<ProductVariant> productVariants = new ArrayList<ProductVariant>();

			for (int j = 0; j < all_variants_json.length(); j++) {

				// 獲取當前的JSON對象
				JSONObject variant_json = all_variants_json.getJSONObject(j);

				// 轉換JSON物件為產品變體物件
				ProductVariant productVariant = gson2.fromJson(variant_json.toString(), ProductVariant.class);

				// 新增產品變體到陣列
				productVariants.add(productVariant);
			}

			// 設置產品變體陣列到產品物件
			product.setProductVariants(productVariants);
			
			productService.addProductByCategory("防摔衣", product, null);
		}
	}
	
	void initialize_PROTECTIVE_GLOVESs() {

		// ----- 初始化產品 -----
		Gson gson2 = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
		String fileName2 = "手套產品資料.json";

		// 獲取當前的JSON檔案的陣列
		JSONArray all_products_json = JsonParserUtils.readFile(fileName2);

		// 遊歷陣列內的所有產品物件
		for (int i = 0; i < all_products_json.length(); i++) {

			// 獲取當前的JSON對象
			JSONObject products_json = all_products_json.getJSONObject(i);

			// 轉換JSON物件為產品物件
			Product product = gson2.fromJson(products_json.toString(), Product.class);

			// 開始解析產品物件內的產品變體
			JSONArray all_variants_json = products_json.getJSONArray("productVariants");

			// 存放產品變體的陣列
			List<ProductVariant> productVariants = new ArrayList<ProductVariant>();

			for (int j = 0; j < all_variants_json.length(); j++) {

				// 獲取當前的JSON對象
				JSONObject variant_json = all_variants_json.getJSONObject(j);

				// 轉換JSON物件為產品變體物件
				ProductVariant productVariant = gson2.fromJson(variant_json.toString(), ProductVariant.class);

				// 新增產品變體到陣列
				productVariants.add(productVariant);
			}

			// 設置產品變體陣列到產品物件
			product.setProductVariants(productVariants);
			
			productService.addProductByCategory("防摔手套", product, null);
		}
	}
}
