package com.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entities.User;
import com.app.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository repository;

	@Override
	public boolean addUser(User user) {
		User savedUser = repository.save(user);
		if (savedUser != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean deleteUserById(Long userId) {
		User user = getUserById(userId);
		if (user != null) {
			repository.delete(user);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public User getUserById(Long userId) {
		Optional<User> getUser = repository.findById(userId);
		if (getUser.isPresent()) {
			return getUser.get();
		} else {
			return null;
		}
	}

	@Override
	public boolean updateUserById(Long userId, User user) {
		Optional<User> getUser = repository.findById(userId);
		if (getUser.isPresent()) {
			User updatedUser = getUser.get();
			updatedUser.setCoupons(user.getCoupons());
			updatedUser.setEmail(user.getEmail());
			updatedUser.setName(user.getName());
			updatedUser.setMobileNumber(user.getMobileNumber());
//			updatedUser.setPassword(null);
//			updatedUser.setRoles(null);
//			updatedUser.setUserId(userId);
			repository.save(updatedUser);

			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<User> getAllUser() {
		List<User> users = repository.findAll();
		if (users.size() > 0) {
			return users;
		} else {
			return null;
		}
	}

}
