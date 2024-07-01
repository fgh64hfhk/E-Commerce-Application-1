package com.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.app.entities.ProductCategory;
import com.app.entities.Role;
import com.app.repositories.CategoryRepository;
import com.app.repositories.RoleRepository;

@Configuration
public class InitializeConfig {

	@Bean
	CommandLineRunner initDatabase(CategoryRepository categoryRepository, RoleRepository roleRepository) {
		return args -> {
			if (categoryRepository.count() == 0) {
				ProductCategory category1 = new ProductCategory(201L, "安全帽");
				ProductCategory category2 = new ProductCategory(202L, "防摔衣");
				ProductCategory category3 = new ProductCategory(203L, "防摔手套");

				categoryRepository.save(category1);
				categoryRepository.save(category2);
				categoryRepository.save(category3);
			}
			if (roleRepository.count() == 0) {

				Role admin = new Role(101L, "ADMIN");
				Role user = new Role(102L, "USER");

				roleRepository.save(admin);
				roleRepository.save(user);
			}
		};
	}
}
