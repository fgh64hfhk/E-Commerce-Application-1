package com.app.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Value("${eeit82.openapi.dev-url}")
	private String devUrl;

	@Value("${eeit82.openapi.prod-url}")
	private String prodUrl;

	@Bean
	OpenAPI openAPI() {

		Server devServer = new Server();
		devServer.setUrl(devUrl);
		devServer.setDescription("Server URL in Development environment");

		Server prodServer = new Server();
		prodServer.setUrl(prodUrl);
		prodServer.setDescription("Server URL in Production environment");

		Contact contact = new Contact();
		contact.setEmail("bear200806@gmail.com");
		contact.setName("Tony Lin");
		contact.setUrl("https://github.com/fgh64hfhk/E-Commerce-Application-1");

		License mitLicense = new License();
		mitLicense.name("forked from Sirajuddin135/E-Commerce-Application");
		mitLicense.url("https://github.com/Sirajuddin135/E-Commerce-Application");

		Info info = new Info();
		info.title("E-Commerce Application");
		info.version("v1.0.3");
		info.contact(contact);
		info.description("Backend APIs for E-Commerce application");
		info.license(mitLicense);

		return new OpenAPI().info(info).servers(List.of(devServer, prodServer)).externalDocs(new ExternalDocumentation()
				.description("E-Commerce App Documentation").url("http://localhost:8080/swagger-ui/index.html"));
	}
}
