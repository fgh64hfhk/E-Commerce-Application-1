package com.app.component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.app.entities.Pokemon;
import com.app.entities.User;
import com.app.entities.UserPokemonSelection;
import com.app.payloads.PokemonDto;
import com.app.repository.PokemonRepository;
import com.app.repository.UserPokemonSelectionRepository;
import com.app.repository.UserRepository;

@Component
public class AppCommandLineRunner implements CommandLineRunner {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PokemonRepository pokemonRepository;
	
	@Autowired
	private UserPokemonSelectionRepository userPokemonSelectionRepository;

	@Override
	public void run(String... args) throws Exception {
		// 創建一些用戶
		List<User> users = new ArrayList<>();
		User user = new User();
		user.setUsername("user1");
		User user2 = new User();
		user2.setUsername("user2");
		users.add(user);
		users.add(user2);
		userRepository.saveAll(users);
		// 創建一些精靈
		List<Pokemon> pokemons = new ArrayList<>();
		Pokemon pikachu = new Pokemon();
		pikachu.setName("Pikachu");
		pikachu.setType("Electric");
		Pokemon charmander = new Pokemon();
		charmander.setName("Charmander");
		charmander.setType("Fire");
		Pokemon bulbasaur = new Pokemon();
		bulbasaur.setName("Bulbasaur");
		bulbasaur.setType("Grasss");
		pokemons.add(pikachu);
		pokemons.add(charmander);
		pokemons.add(bulbasaur);
		pokemonRepository.saveAll(pokemons);
		// 記錄使用者選擇精靈
		List<UserPokemonSelection> selections = new ArrayList<>();
		UserPokemonSelection selection = new UserPokemonSelection();
		selection.setUser(user);
		selection.setPokemon(pikachu);
		selection.setSelectionDate("2024-06-26");
		UserPokemonSelection selection2 = new UserPokemonSelection();
		selection2.setUser(user2);
		selection2.setPokemon(bulbasaur);
		selection2.setSelectionDate("2024-06-25");
		UserPokemonSelection selection3 = new UserPokemonSelection();
		selection3.setUser(user);
		selection3.setPokemon(charmander);
		selection3.setSelectionDate("2024-05-20");
		selections.add(selection);
		selections.add(selection2);
		selections.add(selection3);
		userPokemonSelectionRepository.saveAll(selections);
		// 查詢
		List<PokemonDto> userPokemons = userPokemonSelectionRepository.findPokemonNamesAndTypesByUserIdOrderBySelectionDate(user.getId());
		userPokemons.forEach(pokemon -> {
			System.out.println("Pokemon Name: " + pokemon.getName() + ", Type: " + pokemon.getType());
		});
	}

}
