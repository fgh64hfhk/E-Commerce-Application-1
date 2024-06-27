package com.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.app.entities.ProductCategory;
import com.app.repositories.CategoryRepository;

@Configuration
public class CategoryConfig {

	@Bean
	CommandLineRunner initDatabase(CategoryRepository categoryRepository) {
		return args -> {
			if (categoryRepository.count() == 0) {
				ProductCategory category1 = new ProductCategory(101L, "安全帽");
				ProductCategory category2 = new ProductCategory(102L, "防摔衣");
				ProductCategory category3 = new ProductCategory(103L, "防摔手套");

				categoryRepository.save(category1);
				categoryRepository.save(category2);
				categoryRepository.save(category3);
			}
		};
	}
}
