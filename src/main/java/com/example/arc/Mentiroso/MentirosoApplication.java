package com.example.arc.Mentiroso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MentirosoApplication {
	List<Partida> partidas = new ArrayList<>();

	public record RespuestaInicio(long idPartida, Carta[] cartas, String mensaje) {
	}

	public static void main(String[] args) {
		SpringApplication.run(MentirosoApplication.class, args);
		System.out.println("aksldgfladjsfhalsd");
	}

	@GetMapping("/crear")
	public RespuestaInicio crear(@RequestParam(value = "nombre", defaultValue = "Host") String nombre) {
		Partida p = new Partida();
		p.setIdPartida(partidas.size());

		List<Carta> mazoRecienGenerado = generarMazo();
		p.setMazo(mazoRecienGenerado);

		Jugador host = new Jugador();
		host.setNombre(nombre);
		host.setIdJugador(0);
		host.setEliminado(false);

		Carta[] hostCartas = new Carta[5];
		for (int i = 0; i < 5; i++) {
			hostCartas[i] = p.getMazo().get(i);
		}

		host.setCartas(hostCartas);

		p.setHost(host);
		p.setNumJugadores(1);
		p.setTurnoActual(0);
		p.setFinPartida(false);

		partidas.add(p);

		return new RespuestaInicio(p.getIdPartida(), host.getCartas(), "Partida creada con éxito. Eres el Host.");
	}

	@GetMapping("/unirse")
	public Object unirse(@RequestParam(value = "idPartida") int idPartida,
			@RequestParam(value = "nombre") String nombre) {

		return null;
	}

	private List<Carta> generarMazo() {
		List<Carta> nuevoMazo = new ArrayList<>();
		String[] palos = { "Corazones", "Diamantes", "Tréboles", "Picas" };
		String[] valoresTexto = { "As", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
		for (String palo : palos) {
			for (String val : valoresTexto) {
				Carta c = new Carta();
				c.setPalo(palo);
				c.setValor(val);
				nuevoMazo.add(c);
			}
		}
		Collections.shuffle(nuevoMazo);
		return nuevoMazo;
	}
}
