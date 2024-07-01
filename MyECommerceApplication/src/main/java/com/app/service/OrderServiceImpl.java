package com.app.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entities.Cart;
import com.app.entities.CartItem;
import com.app.entities.Order;
import com.app.entities.OrderItem;
import com.app.entities.Payment;
import com.app.entities.PaymentType;
import com.app.entities.User;
import com.app.payloads.CartDto;
import com.app.payloads.CartItemDto;
import com.app.payloads.OrderDto;
import com.app.repositories.CartItemRepository;
import com.app.repositories.CartRepository;
import com.app.repositories.OrderItemRepository;
import com.app.repositories.OrderRepository;
import com.app.repositories.PaymentRepository;
import com.app.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Override
	@Transactional(rollbackOn = Exception.class)
	public OrderDto placeOrder(String email, String paymentMethod) {

		User user = userRepository.findByEmail(email);

		CartDto cartDto = cartRepository.findCartByUserEmail(email);
		if (cartDto == null) {
			throw new RuntimeException("Cart not found for user with email: " + email);
		}

		Order order = new Order();

		order.setEmail(email);
		order.setOrderDate(LocalDate.now());

		order.setTotalAmount(cartDto.getTotalPrice());
		order.setOrderStatus("Order Accepted !");

		Payment payment = new Payment();
		payment.setUser(user);
		// paymentMethod
		payment.setPaymentType(PaymentType.AfterPay);
		payment = paymentRepository.save(payment);

		user.setPayments(new HashSet<>());
		user.getPayments().add(payment);

		Order savedOrder = orderRepository.save(order);

		List<CartItemDto> cartItems = cartDto.getItems();

		if (cartItems.size() == 0) {
			throw new RuntimeException("Cart is empty.");
		}

		List<OrderItem> orderItems = new ArrayList<>();

		for (CartItemDto cartItemDto : cartItems) {
			OrderItem orderItem = new OrderItem();
			orderItem.setColor(cartItemDto.getColor());
			orderItem.setImage(cartItemDto.getImage());
			orderItem.setOrder(savedOrder);
			orderItem.setQuantity(cartItemDto.getQuantity());
			orderItem.setSize(cartItemDto.getSize());
			orderItem.setSku(cartItemDto.getSku());

			orderItems.add(orderItem);
		}

		orderItems = orderItemRepository.saveAll(orderItems);
		
		savedOrder.setOrderItems(orderItems);

		Cart clearCart = cartRepository.findById(cartDto.getId()).orElseThrow();
		// 清空購物車明細的資料
		List<CartItem> ci = clearCart.getCartItems();
		cartItemRepository.deleteAllInBatch(ci);
		// 清空購物車
		clearCart.getCartItems().clear();
		clearCart.setTotalPrice(0.0);
		cartRepository.save(clearCart);
		
		savedOrder = orderRepository.save(savedOrder);

		return new OrderDto(savedOrder);
	}

	@Override
	public OrderDto getOrderByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OrderDto> getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

}
