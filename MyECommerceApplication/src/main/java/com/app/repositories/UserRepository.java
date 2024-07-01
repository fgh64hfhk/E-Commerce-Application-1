package com.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@EntityGraph(attributePaths = { "roles", "address", "coupons", "payments" })
	Optional<User> findById(Long id);
	
	@EntityGraph(attributePaths = { "roles", "address", "coupons", "payments" })
	List<User> findAll();
	
	@EntityGraph(attributePaths = { "roles", "address", "coupons", "payments" })
	User findByEmail(String email);
}
