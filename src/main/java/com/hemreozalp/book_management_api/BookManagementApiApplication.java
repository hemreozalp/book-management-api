package com.hemreozalp.book_management_api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(title = "Book Management API", version = "0.0.1", description = "Book CRUD operations")
)
@SpringBootApplication
public class BookManagementApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookManagementApiApplication.class, args);
	}

}
