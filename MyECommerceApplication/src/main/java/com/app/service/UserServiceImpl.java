package com.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entities.Address;
import com.app.entities.Cart;
import com.app.entities.CartItem;
import com.app.entities.Delivery;
import com.app.entities.DeliveryType;
import com.app.entities.Product;
import com.app.entities.ProductVariant;
import com.app.entities.User;
import com.app.repositories.AddressRepository;
import com.app.repositories.CartItemRepository;
import com.app.repositories.CartRepository;
import com.app.repositories.DeliveryRepository;
import com.app.repositories.ProductRepository;
import com.app.repositories.ProductVariantRepository;
import com.app.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	DeliveryRepository deliveryRepository;

	@Autowired
	CartItemRepository cartItemRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	ProductVariantRepository productVariantRepository;

	@Override
	public boolean addUser(User user) {
		
		// 預設配送地址
		Address address = new Address();
		addressRepository.save(address);

		// 預設購物車列表，用於配送的初始
		List<Cart> carts = new ArrayList<>();

		Cart cart = new Cart();
		carts.add(cart);
		cart.setUser(user);
		cartRepository.save(cart);

		Delivery delivery = new Delivery();
		delivery.setDeliveryType(DeliveryType.Home);
		delivery.setDeliveryPrice(50);
		delivery.setCart(carts);
		deliveryRepository.save(delivery);
		cart.setDelivery(delivery);

		List<CartItem> cartItems = new ArrayList<>();

		Product product = new Product();
		product.setName("Product001");
		product.setPrice(550.0);

		List<ProductVariant> productVariants = new ArrayList<>();
		ProductVariant productVariant = new ProductVariant(1L, "白", "M", "001-白-M", 50, "image001", product);
		productVariants.add(productVariant);
		CartItem cartItem = new CartItem(cart, productVariant, 5);

		ProductVariant productVariant2 = new ProductVariant(2L, "白", "L", "001-白-L", 50, "image002", product);
		productVariants.add(productVariant2);
		CartItem cartItem2 = new CartItem(cart, productVariant2, 10);

		ProductVariant productVariant3 = new ProductVariant(3L, "白", "XL", "001-白-XL", 50, "image003", product);
		productVariants.add(productVariant3);
		CartItem cartItem3 = new CartItem(cart, productVariant3, 15);

		Integer totalQuantity = productVariants.stream().mapToInt(pv -> pv.getInventory()).sum();
		product.setTotalQuantity(totalQuantity);
		productRepository.save(product);

		productVariantRepository.saveAll(productVariants);

		cartItems.add(cartItem);
		cartItems.add(cartItem2);
		cartItems.add(cartItem3);

		cartItemRepository.saveAll(cartItems);

		Double totalPrice = cartItems.stream()
				.mapToDouble(item -> item.getProductVariant().getProduct().getPrice() * item.getQuantity()).sum();
		cart.setTotalPrice(totalPrice);
		cart.setCartItems(cartItems);

		cartRepository.save(cart);

		user.setCart(cart);
		user.setAddress(address);
		User savedUser = userRepository.save(user);

		if (savedUser != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean deleteUserById(Long userId) {
		User user = getUserById(userId);
		if (user != null) {
			userRepository.delete(user);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public User getUserById(Long userId) {
		Optional<User> getUser = userRepository.findById(userId);
		if (getUser.isPresent()) {
			return getUser.get();
		} else {
			return null;
		}
	}

	@Override
	public boolean updateUserById(Long userId, User user) {
		Optional<User> getUser = userRepository.findById(userId);
		if (getUser.isPresent()) {
			User updatedUser = getUser.get();
			updatedUser.setCoupons(user.getCoupons());
			updatedUser.setEmail(user.getEmail());
			updatedUser.setUsername(user.getUsername());
			updatedUser.setMobileNumber(user.getMobileNumber());
//			updatedUser.setPassword(null);
//			updatedUser.setRoles(null);
//			updatedUser.setUserId(userId);
			userRepository.save(updatedUser);

			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<User> getAllUser() {
		List<User> users = userRepository.findAll();
		if (users.size() > 0) {
			return users;
		} else {
			return null;
		}
	}

	@Override
	public User getUserByEmail(String email) {
		Optional<User> getUser = userRepository.findByEmail(email);
		return getUser.get();
	}

}
