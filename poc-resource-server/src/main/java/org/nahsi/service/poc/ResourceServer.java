package org.nahsi.service.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@SpringBootApplication
public class ResourceServer {
	public static void main(String[] args) {
		SpringApplication.run(ResourceServer.class, args);
	}
}
