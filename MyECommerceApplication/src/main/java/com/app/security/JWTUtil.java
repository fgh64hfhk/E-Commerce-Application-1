package com.app.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JWTUtil {

	@Value("${jwt_secret}")
	private String secret;

	public String generateToken_user(String email) throws IllegalArgumentException, JWTCreationException {
		return generateToken(email, "USER");
	}
	
	public String generateToken_admin(String email) throws IllegalArgumentException, JWTCreationException {
		return generateToken(email, "ADMIN");
	}
	
	public String generateToken(String email, String status) throws IllegalArgumentException, JWTCreationException {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + 86400000);
		return JWT.create()
				.withSubject("User Details")
				.withClaim("email", email)
				.withClaim("status", status)
				.withIssuedAt(new Date())
				.withExpiresAt(expiryDate)
				.withIssuer("Event Scheduler")
				.sign(Algorithm.HMAC256(secret));
	}
	
	public Map<String, String> validateTokenAndRetrieveSubject(String token) throws JWTVerificationException {
		Map<String, String> map = new HashMap<>();
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
				.withSubject("User Details")
				.withIssuer("Event Scheduler")
				.build();
		DecodedJWT jwt = verifier.verify(token);
		map.put("email", jwt.getClaim("email").asString());
		map.put("status", jwt.getClaim("status").asString());
		return map;
	}
}