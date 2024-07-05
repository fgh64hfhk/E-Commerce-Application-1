package com.app.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.service.UserDetailsServiceImpl;
import com.auth0.jwt.exceptions.JWTVerificationException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class JWTFilter extends OncePerRequestFilter {

	@Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 攔截控制器的所有路徑，並取得標頭
		String header = request.getHeader("Authorization");
		if (header != null && !header.isBlank() && header.startsWith("Bearer")) {
			// 取得令牌
			String jwt = header.substring(7);

			if (jwt == null || jwt.isBlank()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invlaid JWT token in Bearer Header");
				return;
			}

			try {
				// authenticate user login
				Map<String, String> details = jwtUtil.validateTokenAndRetrieveSubject(jwt);
				String email = details.get("email");
				UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails,
						new String(email),
						userDetails.getAuthorities());
				// 設置 details
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				if (SecurityContextHolder.getContext().getAuthentication() == null) {
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			} catch (JWTVerificationException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invlaid JWT token in Bearer Header");
			}

		}
		filterChain.doFilter(request, response);
	}
}