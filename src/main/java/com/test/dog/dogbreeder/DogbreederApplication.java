package com.test.dog.dogbreeder;

import com.test.dog.dogbreeder.repository.DogRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableJpaRepositories(basePackageClasses = {DogRepository.class})
public class DogbreederApplication {

	public static void main(String[] args) {
		SpringApplication.run(DogbreederApplication.class, args);
	}

}

