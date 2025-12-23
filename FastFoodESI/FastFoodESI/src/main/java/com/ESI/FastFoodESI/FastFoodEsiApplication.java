package com.ESI.FastFoodESI;

import com.ESI.FastFoodESI.model.User;
import com.ESI.FastFoodESI.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FastFoodEsiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FastFoodEsiApplication.class, args);
	}

	@Bean
	CommandLineRunner testDatabase(UserRepository userRepository) {
		return args -> {
			userRepository.save(new User("Pepe"));
			userRepository.save(new User("Paco"));
			System.out.println("Users in DB:");
			userRepository.findAll().forEach(u -> System.out.println(u.getId() + ": " + u.getUsername()));
		};
	}

}
