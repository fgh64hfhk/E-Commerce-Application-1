package com.app.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.app.security.JWTFilter;
import com.app.service.UserDetailsServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	private JWTFilter jwtFilter;

	@Bean
	Customizer<CorsConfigurer<HttpSecurity>> corsCustomizer() {
		return (corsConfigurer) -> {
			corsConfigurer.configurationSource(configurationSource());
			// Add more customizations as needed
		};
	}

	@Bean
	CorsConfigurationSource configurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable()) // 禁用CSRF（跨站請求偽造）保護
			.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
					.requestMatchers(AppConstants.PUBLIC_URLS).permitAll() // 允許對首頁，登入頁面和靜態資源（如CSS、JS、圖片）的訪問，無需認證
					.requestMatchers(AppConstants.USER_URLS).hasAnyAuthority("USER", "ADMIN") // 使用者和管理員角色可以訪問的資源
					.requestMatchers(AppConstants.ADMIN_URLS).hasAuthority("ADMIN") // 只有管理員角色可以訪問
					.anyRequest().authenticated()) // 要求所有其他請求都必須經過認證
			.formLogin(form -> form
					// TODO
					.loginPage("/")
					.permitAll()) // 允許所有用戶訪問表單登錄頁面
			.logout(logout -> logout
					// TODO
					.logoutSuccessUrl("/") // 登出成功後跳轉到首頁
					.invalidateHttpSession(true) // 登出時使 HttpSession 失效
					.deleteCookies("JSESSIONID")) // 删除 JSESSIONID cookie
			.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // 根據需要創建會話（Session）
			.exceptionHandling(exception -> exception
					.authenticationEntryPoint(authenticationEntryPoint())) // 設置自定義的身份驗證入口點
			.cors(corsCustomizer()); // 啟用CORS（跨域資源共享），使用預設配置

		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		http.authenticationProvider(daoAuthenticationProvider());

		DefaultSecurityFilterChain defaultSecurityFilterChain = http.build();

		return defaultSecurityFilterChain;
	}

	AuthenticationEntryPoint authenticationEntryPoint() {
		return new AuthenticationEntryPoint() {

			@Override
			public void commence(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException authException) throws IOException, ServletException {
				// TODO
				if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
				} else {
					response.sendRedirect("/");
				}
			}
		};
	}

	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsServiceImpl);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

}