package com.mokhir.dev.BookShop;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class BookShopApplication {
	public static void main(String[] args) {
		SpringApplication.run(BookShopApplication.class, args);
	}

}
