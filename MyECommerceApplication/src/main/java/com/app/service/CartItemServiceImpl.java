package com.app.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entities.Cart;
import com.app.entities.CartItem;
import com.app.entities.Product;
import com.app.entities.ProductVariant;
import com.app.entities.User;
import com.app.payloads.CartItemDto;
import com.app.repositories.CartItemRepository;
import com.app.repositories.CartRepository;
import com.app.repositories.ProductRepository;
import com.app.repositories.ProductVariantRepository;
import com.app.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartItemServiceImpl implements CartItemService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	CartItemRepository cartItemRepository;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	ProductVariantRepository productVariantRepository;

	@Autowired
	ProductRepository productRepository;

	@Override
	public List<CartItemDto> addCartItemByUserEmail(String userEmail, String sku, Integer quantity) {
		// 使用使用者的電子信箱查詢購物車主表
		User user = userRepository.findByEmail(userEmail).get();
		// 取得購物車主表
		Cart cart = user.getCart();
		// 取得購物車明細列表
		List<CartItem> cartItems = user.getCart().getCartItems();

		// 篩選是否有重複的商品變體識別碼，如果有則累加數量；否則新增一筆新的購物車明細。並減少商品變體的庫存
		Optional<CartItem> optional = cartItems.stream()
				.filter(cartItem -> cartItem.getProductVariant().getSku().equalsIgnoreCase(sku)).findFirst();

		// 重複變體，累加數量
		if (optional.isPresent()) {
			CartItem cartItem = optional.get();
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
			// 取得商品變體並減少庫存
			cartItem.getProductVariant().setInventory(cartItem.getProductVariant().getInventory() - quantity);
			// 計算累加數量的總價
			Double price = cartItem.getProductVariant().getProduct().getPrice() * quantity;
			cart.setTotalPrice(cart.getTotalPrice() + price);
			// 儲存
			cartRepository.save(cart);
			cartItemRepository.save(cartItem);
		} else {
			CartItem cartItem = new CartItem();
			cartItems.add(cartItem);
			cartItem.setCart(cart);
			cart.setCartItems(cartItems);
			// 取得商品變體
			ProductVariant variant = productVariantRepository.findBySku(sku);
			cartItem.setProductVariant(variant);
			variant.setInventory(variant.getInventory() - quantity);
			// 設定購物車明細的數量
			cartItem.setQuantity(quantity);
			// 計算累加數量的總價
			Double price = cartItem.getProductVariant().getProduct().getPrice() * quantity;
			cart.setTotalPrice(cart.getTotalPrice() + price);
			// 儲存
			cartRepository.save(cart);
			cartItemRepository.save(cartItem);
			productVariantRepository.save(variant);
		}

		return cartItemRepository.getAllCartItemsByUserEmail(userEmail);
	}

	@Override
	public List<CartItemDto> getAllCartItemsByUserEmail(String userEmail) {
		List<CartItemDto> cartItemDtos = cartItemRepository.getAllCartItemsByUserEmail(userEmail);
		return cartItemDtos;
	}

	@Override
	@Transactional
	public CartItem updateCartItemQuantityBySkuAndUserEmail(Integer quantity, String sku, String userEmail) {

		Optional<CartItem> optional = cartItemRepository.findCartItemForUpdate(sku, userEmail);

		if (optional.isPresent()) {
			CartItem cartItem = optional.get();
			// 取得原來的選擇數量
			Integer original1 = cartItem.getQuantity();

			// 設定更新的選擇數量
			cartItem.setQuantity(quantity);

			// 取得原來的購物車主表
			Cart cart = cartItem.getCart();

			ProductVariant pv = productVariantRepository.findBySku(sku);

			// 取得原來的選擇總價
			Double original_price = pv.getProduct().getPrice() * original1;
			cart.setTotalPrice(cart.getTotalPrice() - original_price + (pv.getProduct().getPrice() * quantity));
			cartRepository.save(cart);

			Integer original2 = pv.getInventory();
			pv.setInventory(pv.getInventory() + original1 - quantity);

			Optional<Product> p = productRepository.findById(pv.getProduct().getProductId());

			p.ifPresent(product -> {
				product.setTotalQuantity(product.getTotalQuantity() - original2 + pv.getInventory());
				productRepository.save(product);
			});

			productVariantRepository.save(pv);

			try {
				Thread.sleep(8000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return cartItemRepository.save(cartItem);
		} else {
			return new CartItem();
		}
	}

	@Override
	public List<CartItemDto> deleteCartItemBySkuAndUserEmail(String sku, String userEmail) {
		
		// 检查user对象是否为null
		User user = userRepository.findByEmail(userEmail).get();
		if (user == null) {
			throw new RuntimeException("User not found with email: " + userEmail);
		}

		// 检查cart对象是否为null
		Cart cart = user.getCart();
		if (cart == null) {
			throw new RuntimeException("Cart not found for user with email: " + userEmail);
		}

		List<CartItem> cartItems = cart.getCartItems();

		// 检查cartItems是否为空
		if (cartItems == null || cartItems.isEmpty()) {
			throw new RuntimeException("No cart items found for user with email: " + userEmail);
		}

		// 找到指定SKU的商品变体
		ProductVariant productVariant = cartItems.stream()
				.filter(cartItem -> cartItem.getProductVariant().getSku().equalsIgnoreCase(sku)).findAny()
				.orElseThrow(() -> new RuntimeException("Product variant not found with SKU: " + sku))
				.getProductVariant();

		// 找到指定SKU的商品数量
		Integer quantity = cartItems.stream()
				.filter(cartItem -> cartItem.getProductVariant().getSku().equalsIgnoreCase(sku)).findAny()
				.orElseThrow(() -> new RuntimeException("Product variant not found with SKU: " + sku)).getQuantity();

		// 移除指定SKU的购物车项
		CartItem ci = cartItems.stream().filter(cartItem -> cartItem.getProductVariant().equals(productVariant)).findFirst().get();
		cartItems.removeIf(cartItem -> cartItem.getProductVariant().equals(productVariant));

		// 更新用户购物车
		cart.setCartItems(cartItems);
		cart.setTotalPrice(cart.getTotalPrice() - (productVariant.getProduct().getPrice() * quantity));

		// 更新商品库存
		productVariant.setInventory(productVariant.getInventory() + quantity);

		// 更新使用者的購物車主表
		user.setCart(cart);

		// 保存更新后的数据
		cartItemRepository.delete(ci);
		productVariantRepository.save(productVariant);
		userRepository.save(user);

		// 返回用户的所有购物车项
		return cartItems.stream().map(cartItem -> {
			CartItemDto dto = new CartItemDto(cartItem);
			return dto;
		}).collect(Collectors.toList());
	}

}
