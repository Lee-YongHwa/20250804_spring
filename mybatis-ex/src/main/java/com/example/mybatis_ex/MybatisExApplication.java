package com.example.mybatis_ex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MybatisExApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisExApplication.class, args);
		System.out.println("http://localhost:8080/mybatis-ex");
		System.out.println("http://localhost:8080/mybatis-ex/swagger-ui/index.html");
	}

}
