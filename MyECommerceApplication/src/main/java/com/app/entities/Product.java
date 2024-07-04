package com.app.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.app.payloads.ProductDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;

	@Column(name = "product_name")
	private String name;

	private String subcategory;

	private String brand;

	private String description;

	private Double price;

	private Integer totalQuantity;

	@ManyToOne
	@JoinColumn(name = "category_id")
	@JsonBackReference
	@ToString.Exclude
	private ProductCategory category;

	@OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST, orphanRemoval = true)
	@JsonManagedReference
	@ToString.Exclude
	private List<ProductVariant> productVariants = new ArrayList<>();

	// 優惠券
	@ManyToMany(mappedBy = "applicableProducts")
	private Set<Coupon> applicableCoupons = new HashSet<>();

	public void addProductVariant(ProductVariant productVariant) {
		if (!productVariants.contains(productVariant)) {
			productVariants.add(productVariant);
			productVariant.setProduct(this);
			System.out.println("成功新增變體:" + this.getProductVariants());
		}
	}

	public void removeProductVariant(ProductVariant productVariant) {
		productVariants.remove(productVariant);
		productVariant.setProduct(null);
	}

	public Product(ProductDto productDto) {
		this.name = productDto.getName();
		this.subcategory = productDto.getSubcategory();
		this.brand = productDto.getBrand();
		this.description = productDto.getDescription();
		this.price = productDto.getPrice();
		this.totalQuantity = productDto.getTotalQuantity();

		this.productVariants = productDto.getProductList().stream().map(t -> new ProductVariant(t))
				.collect(Collectors.toList());
	}
}
