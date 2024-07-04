package com.app.payloads;

import com.app.entities.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

	private String name;
	private String mobileNumber;
	private String email;
	private String password;

	private String role;
	
	public UserDto(User user) {
		this.name = user.getUsername();
		this.mobileNumber = user.getMobileNumber();
		this.email = user.getEmail();
	}
}
