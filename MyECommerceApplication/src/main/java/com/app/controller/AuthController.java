package com.app.controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.entities.Role;
import com.app.entities.User;
import com.app.payloads.LoginCredentials;
import com.app.payloads.UserDto;
import com.app.repositories.RoleRepository;
import com.app.security.JWTUtil;
import com.app.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
@CrossOrigin
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping("/register")
	public ResponseEntity<Map<String, Object>> registerHandler(@Valid @RequestBody UserDto userDto) {
		try {
			// 編碼用戶密碼
			String encodePassword = passwordEncoder.encode(userDto.getPassword());
			userDto.setPassword(encodePassword);

			// 獲取角色並檢查其有效性
			Set<Role> roles = new HashSet<>();
			Role role = roleRepository.findByRoleName(userDto.getRole());
			if (role == null) {
				return new ResponseEntity<>(Collections.singletonMap("Error-Message", "Invalid role specified"),
						HttpStatus.BAD_REQUEST);
			}
			if (role.getRoleName().equalsIgnoreCase("admin")) {
				Role user = roleRepository.findByRoleName("USER");
				roles.add(user);
			}
			roles.add(role);

			// 創建用戶實體
			User user = new User(userDto.getName(), userDto.getMobileNumber(), userDto.getEmail(),
					userDto.getPassword(), roles);

			// 添加用戶並檢查是否成功
			boolean isRegister = userService.addUser(user);
			if (isRegister) {
				String token = userDto.getRole().equalsIgnoreCase("ADMIN")
						? jwtUtil.generateToken_admin(userDto.getEmail())
						: jwtUtil.generateToken_user(userDto.getEmail());

				return new ResponseEntity<>(Collections.singletonMap("jwt-token", token), HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(Collections.singletonMap("Error-Message", "Failed to Register..."),
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(
					Collections.singletonMap("Error-Message", "An error occurred: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> loginHandler(@Valid @RequestBody LoginCredentials credentials) {
		try {
			UsernamePasswordAuthenticationToken authCredentials = new UsernamePasswordAuthenticationToken(
					credentials.getEmail(), credentials.getPassword());
			Authentication authentication = authenticationManager.authenticate(authCredentials);
			String status = authentication.getAuthorities().stream()
					.anyMatch(t -> t.getAuthority().equalsIgnoreCase("admin")) ? "ADMIN" : "USER";

			String token = jwtUtil.generateToken(credentials.getEmail(), status);

			return ResponseEntity.ok().header("Authorization", "Bearer " + token)
					.body(Collections.singletonMap("jwt-token", token));

		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Collections.singletonMap("error", "Invalid credentials"));
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> performLogout(Authentication authentication, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
			logoutHandler.logout(request, response, authentication); // 呼叫登出處理器
			
			return ResponseEntity.ok(Collections.singletonMap("message", "Logout successful"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("error", "An error occurred during logout: " + e.getMessage()));
		}
	}

}
