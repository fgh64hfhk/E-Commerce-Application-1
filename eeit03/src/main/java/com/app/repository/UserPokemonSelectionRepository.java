package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.UserPokemonSelection;
import com.app.payloads.PokemonDto;

public interface UserPokemonSelectionRepository extends JpaRepository<UserPokemonSelection, Long>{

	// 使用 JPQL 查詢指定使用者的選擇紀錄
	@Query(value = "SELECT ups FROM UserPokemonSelection ups WHERE ups.user.id = :userId")
	List<UserPokemonSelection> findSelectionsByUserId(@Param("userId") Long userId);
	// 使用 JPQL 查詢指定精靈的被選擇紀錄
	@Query(value = "SELECT ups FROM UserPokemonSelection ups WHERE ups.pokemon.id = :pokemonId")
	List<UserPokemonSelection> findSelectionsByPokemonId(@Param("pokemonId") Long pokemonId);
	
	// 使用 JPQL 查詢指定使用者的選擇紀錄，包括精靈的名稱與種類
	@Query(value = "SELECT new com.app.payloads.PokemonDto(ups.pokemon.name, ups.pokemon.type) FROM UserPokemonSelection ups WHERE ups.user.id = :userId ORDER BY ups.selectionDate")
	List<PokemonDto> findPokemonNamesAndTypesByUserIdOrderBySelectionDate(@Param("userId") Long userId);
}
