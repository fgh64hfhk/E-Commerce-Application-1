package com.app.entities;

import java.util.Map;
import java.util.Set;

import com.app.payloads.ProductVariantDto;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long productVariantId;

	@NotBlank
	private String color;

	@NotBlank
	private String size;

	@NotBlank
	private String sku;

	private Integer inventory;

	// 將此屬性修改為Base64_dataURL的形式
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String image;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "product_id")
	@JsonBackReference
	@ToString.Exclude
	private Product product;
	
	public ProductVariant(ProductVariantDto productVariantDto) {
		this.color = productVariantDto.getColor();
		this.size = productVariantDto.getSize();
		this.sku = productVariantDto.getSku();
		this.inventory = productVariantDto.getInventory();
		this.image = analysImageString(productVariantDto.getImage());
	}
	
	private String analysImageString(Map<String, String> image) {
		Set<String> keys = image.keySet();
		if (keys.contains("normal type")) {
			return new String(image.get("normal type"));
		}
		return "default_image";
	}
}
