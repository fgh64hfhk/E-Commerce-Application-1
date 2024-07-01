package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.entities.User;
import com.app.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

	// injection cart service
	@Autowired
	private UserService service;

	// 增 --> 註冊頁面
	@PostMapping("/user")
	public ResponseEntity<User> createUser(@RequestBody User user) {

		ResponseEntity<User> entity;
		if (service.addUser(user)) {
			entity = new ResponseEntity<User>(user, HttpStatus.CREATED);
		} else {

			entity = new ResponseEntity<User>(user, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 一般使用者
	// 查 -->
	@GetMapping("/user/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable Long userId) {
		User user = service.getUserById(userId);
		ResponseEntity<User> entity;
		if (user != null) {
			entity = new ResponseEntity<User>(user, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<User>(user, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	// 一般使用者
	// 查 -->
	@GetMapping("/user/{userEmail}")
	public ResponseEntity<User> getUserByUserEmail(@PathVariable String userEmail) {
		
		ResponseEntity<User> entity;
		User user = service.getUserByEmail(userEmail);
		if (user != null) {
			entity = new ResponseEntity<User>(user, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<User>(user, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 一般使用者
	// 修
	@PutMapping("/user/{userId}")
	ResponseEntity<User> updateUserById(@PathVariable Long userId, @RequestBody User user) {
		
		User u = service.getUserById(userId);

		ResponseEntity<User> entity;
		if (u != null) {
			user.setUserId(u.getUserId());
			if (service.updateUserById(u.getUserId(), user)) {
				entity = new ResponseEntity<User>(user, HttpStatus.OK);
			} else {
				entity = new ResponseEntity<User>(user, HttpStatus.SERVICE_UNAVAILABLE);
			}
		} else {
			entity = new ResponseEntity<User>(user, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 管理使用者
	// 查
	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = service.getAllUser();
		ResponseEntity<List<User>> entity;
		if (users.size() > 0) {
			entity = new ResponseEntity<List<User>>(users, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<List<User>>(users, HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 管理使用者
	// 刪
	@DeleteMapping("/user/{userId}")
	public ResponseEntity<User> deleteUserById(@PathVariable Long userId) {
		ResponseEntity<User> entity;
		User user = service.getUserById(userId);
		if (user != null) {
			service.deleteUserById(userId);
			entity = new ResponseEntity<User>(user, HttpStatus.OK);
		} else {
			entity = new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		}
		return entity;
	}
}
