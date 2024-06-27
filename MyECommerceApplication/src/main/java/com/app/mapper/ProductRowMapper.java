package com.app.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.app.entities.Product;

public class ProductRowMapper implements RowMapper<Product> {
	
	@Override
	public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
		Product product = new Product();
		
		product.setProductId(rs.getLong("product_id"));
		product.setName(rs.getString("product_name"));
		product.setSubcategory(rs.getString("subcategory"));
		product.setBrand(rs.getString("brand"));
		product.setDescription(rs.getString("description"));
		product.setPrice(rs.getDouble("price"));
		product.setTotalQuantity(rs.getInt("total_quantity"));

		return product;
	}

}
