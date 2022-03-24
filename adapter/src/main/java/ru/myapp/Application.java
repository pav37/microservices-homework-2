package ru.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Locale;

@SpringBootApplication
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		SpringApplication.run(Application.class, args);
	}

}
