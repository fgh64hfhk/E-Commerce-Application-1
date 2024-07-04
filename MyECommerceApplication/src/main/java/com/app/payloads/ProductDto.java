package com.app.payloads;

import java.util.List;
import java.util.stream.Collectors;

import com.app.entities.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

	private String name;
	
	private String subcategory;

	private String brand;

	private String description;

	private Double price;

	private Integer totalQuantity;
	
	private List<ProductVariantDto> productList;
	
	public ProductDto(Product product) {
		this.name = product.getName();
		this.subcategory = product.getSubcategory();
		this.brand = product.getBrand();
		this.description = product.getDescription();
		this.price = product.getPrice();
		this.totalQuantity = product.getTotalQuantity();
		this.productList = product.getProductVariants().stream()
				.map(t -> new ProductVariantDto(t))
				.collect(Collectors.toList());
	}
	
}
