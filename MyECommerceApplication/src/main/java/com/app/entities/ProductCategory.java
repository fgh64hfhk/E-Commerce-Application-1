package com.app.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Table(name = "product_categories")
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {

	@Id
	private Long categoryId;

	private String categoryName;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	@JsonManagedReference
	@ToString.Exclude
	private List<Product> products;

	public ProductCategory(Long categoryId, String categoryName) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}

}
