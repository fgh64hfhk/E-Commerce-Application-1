package com.app.payloads;

import java.util.HashMap;
import java.util.Map;

import com.app.entities.ProductVariant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDto {

	private String color;

	private String size;

	private String sku;

	private Integer inventory;

	private Map<String, String> image;

	public ProductVariantDto(ProductVariant productVariant) {
		this.color = productVariant.getColor();
		this.size = productVariant.getSize();
		this.sku = productVariant.getSku();
		this.inventory = productVariant.getInventory();
		this.image = translateBase642ImageMap(productVariant.getImage());
	}

	private Map<String, String> translateBase642ImageMap(String image) {
		Map<String, String> map = new HashMap<>();
		if (image == null || image.isEmpty()) {
			// Handle empty or null image string gracefully
			return map; // Return an empty map
		}
		if (!image.startsWith("data:")) {
			map.put("normal type", "image");
			return map;
		} else {
			try {
				// "data:{mimeType};base64,{base64Image}"
				String[] str = image.split(",");
				String base64Image = str[1];

				int ci = str[0].indexOf(":");
				int si = str[0].indexOf(";");

				// 提取 mimeType
				String mimeType = str[0].substring(ci + 1, si);

				map.put(mimeType, base64Image);
			} catch (Exception e) {
				// Handle any exceptions gracefully
				map.put("error", "invalid image data");
				// Log the exception or handle it as appropriate
			}
			return map;
		}
	}

}
