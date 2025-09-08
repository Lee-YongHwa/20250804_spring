package com.example.ex7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Ex7Application {

	public static void main(String[] args) {
		SpringApplication.run(Ex7Application.class, args);
		System.out.println("http://localhost:8080/ex7");
	}

}
