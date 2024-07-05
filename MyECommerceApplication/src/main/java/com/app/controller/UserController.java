package com.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.entities.User;
import com.app.payloads.UserDto;
import com.app.security.UserInfoConfig;
import com.app.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
@CrossOrigin
@Tag(name = "User", description = "User management APIs")
public class UserController {

	@Autowired
	private UserService userService;

	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successful operation", content = {
			@Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json") }) })
	@GetMapping("/public/user/{userId}")
	public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {

		// Extract user ID from JWT
		String authenticatedUserEmail = ((UserInfoConfig) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal()).getEmail();

		User user = userService.getUserByEmail(authenticatedUserEmail);

		if (!user.getUserId().equals(userId)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		} else {
			User returnUser = userService.getUserById(userId);

			if (returnUser != null) {
				UserDto userDto = new UserDto(returnUser);
				return ResponseEntity.ok(userDto);
			} else {
				return ResponseEntity.badRequest().body(null);
			}

		}
	}

	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successful operation", content = {
			@Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json") }) })
	@GetMapping("/public/user/email/{email}")
	public ResponseEntity<UserDto> getUserByUserEmail(@PathVariable String email) {

		// Extract user ID from JWT
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		System.out.println(principal);

		if (!principal.equals(email)) {
			System.out.println("!authenticatedUserEmail.equals(email)");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		} else {
			User returnUser = userService.getUserByEmail((String)principal);

			if (returnUser != null) {
				UserDto userDto = new UserDto(returnUser);
				return ResponseEntity.ok(userDto);
			} else {
				return ResponseEntity.badRequest().body(null);
			}
		}
		
//		if (principal instanceof UserInfoConfig) {
//			String authenticatedUserEmail = ((UserInfoConfig) principal).getEmail();
//			if (!authenticatedUserEmail.equals(email)) {
//				System.out.println("!authenticatedUserEmail.equals(email)");
//				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//			} else {
//				User returnUser = userService.getUserByEmail(authenticatedUserEmail);
//
//				if (returnUser != null) {
//					UserDto userDto = new UserDto(returnUser);
//					return ResponseEntity.ok(userDto);
//				} else {
//					return ResponseEntity.badRequest().body(null);
//				}
//			}
//		} else {
//			System.out.println("!principal instanceof UserInfoConfig");
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//		}
	}

	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successful operation", content = {
			@Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json") }) })
	@PutMapping("/public/user/{userId}")
	public ResponseEntity<UserDto> updateUserById(@PathVariable Long userId, @RequestBody User user) {

		// Extract user ID from JWT
		String authenticatedUserEmail = ((UserInfoConfig) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal()).getEmail();

		User authenticated = userService.getUserByEmail(authenticatedUserEmail);

		if (!authenticated.getUserId().equals(userId)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		} else {
			User returnUser = userService.getUserById(userId);

			if (returnUser != null) {
				user.setUserId(returnUser.getUserId());
				Boolean isUpdate = userService.updateUserById(user.getUserId(), user);

				if (isUpdate) {
					UserDto userDto = new UserDto(user);
					return ResponseEntity.ok(userDto);
				} else {
					return ResponseEntity.internalServerError().body(null);
				}

			} else {
				return ResponseEntity.badRequest().body(null);
			}

		}
	}

	@Operation(summary = "Retrieve all users", description = "Get a list of all users. The response is a list of UserDto objects.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful operation", content = {
					@Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "204", description = "No users found", content = {
					@Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = {
					@Content(schema = @Schema()) }) })
	@GetMapping("/admin/users")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		List<User> users = userService.getAllUser();
		List<UserDto> userDtos = users.stream().map(UserDto::new).collect(Collectors.toList());

		if (users.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
			return ResponseEntity.ok().body(userDtos);
		}
	}

	@DeleteMapping("/admin/user/{userId}")
	public ResponseEntity<User> deleteUserById(@PathVariable Long userId) {
		ResponseEntity<User> entity;
		User user = userService.getUserById(userId);
		if (user != null) {
			userService.deleteUserById(userId);
			entity = new ResponseEntity<User>(user, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		}
		return entity;
	}
}
