package com.example.arc.Mentiroso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class MentirosoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MentirosoApplication.class, args);
		System.out.println("aksldgfladjsfhalsd");
	}
	// hola

	@GetMapping("/crear")
	public Jugador hello(@RequestParam(value = "nombre", defaultValue = "World") String name) {
		Jugador x = new Jugador();
		return x;
	}
}
