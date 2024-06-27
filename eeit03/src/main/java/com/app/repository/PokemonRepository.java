package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.Pokemon;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {

	Pokemon findByType(String type);
}
