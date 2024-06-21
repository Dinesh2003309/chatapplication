package com.dinesh.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableJpaAuditing
@EnableAutoConfiguration
public class UserServiceApplication {

	/**
	 * Returns a WebMvcConfigurer instance with Cross-Origin Resource Sharing (CORS) configuration applied.
	 * CORS is a mechanism that allows resources on a web page to be requested from another domain outside the domain from which the resource originated.
	 *
	 * @return a WebMvcConfigurer instance with CORS configuration applied
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept","Authorization")
						.allowedOrigins("https://helpido-user.blockchainfirm.io" , "https://helpido-web.blockchainfirm.io","https://www.helpido-web.blockchainfirm.io/","http://localhost:8001", "http://localhost:3000")
						.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
						.exposedHeaders("X-Get-Header")
						.allowCredentials(true).maxAge(3600);
			}
		};
	}


	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
