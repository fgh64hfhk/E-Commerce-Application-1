package com.app.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long couponId;

	@Column(unique = true, nullable = false)
	private String couponCode;

	@Enumerated(EnumType.STRING)
	private DiscountType discountType;

	private Double discountValue;

	private LocalDate startDate;

	private LocalDate endDate;

	private String conditions;

	@Enumerated(EnumType.STRING)
	private CouponStatus status;

	// 優惠券種類
	@ManyToOne
	@JoinColumn(name = "category_id")
	private CouponCategory category;

	// 使用者
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	// 應用商品
	@ManyToMany
	@JoinTable(name = "coupon_product", joinColumns = @JoinColumn(name = "coupon_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
	private Set<Product> applicableProducts = new HashSet<>();
}
