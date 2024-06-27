package com.app.service;

import java.util.List;

import com.app.entities.User;

public interface UserService {

	boolean addUser(User user);
	
	boolean deleteUserById(Long userId);
	
	boolean updateUserById(Long userId, User user);
	
	User getUserById(Long userId);
	
	List<User> getAllUser();
}
