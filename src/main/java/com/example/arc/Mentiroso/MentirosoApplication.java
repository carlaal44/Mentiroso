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

	// Permite que hayan varias partidas a lavez
	List<Partida> partidas = new ArrayList<>();

	// DTO
	public static record RespuestaInicio(long idPartida, Carta[] cartas, String mensaje) {
	}

	public static void main(String[] args) {
		SpringApplication.run(MentirosoApplication.class, args);
		System.out.println("Servidor levantado y listo");
	}

	@GetMapping("/crear")
	public RespuestaInicio crear(@RequestParam(value = "nombre", defaultValue = "Host") String nombre) {
		Partida p = new Partida();

		// De momento el ID es la posición en la lista
		p.setIdPartida(partidas.size());

		List<Carta> mazoRecienGenerado = generarMazo();
		p.setMazo(mazoRecienGenerado);

		// Setup del Host
		Jugador host = new Jugador();
		host.setNombre(nombre);
		host.setIdJugador(0);
		host.setEliminado(false);

		// Repartimos mano inicial
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

		// Validación básica
		if (idPartida < 0 || idPartida >= partidas.size()) {
			return "La partida " + idPartida + " no existe";
		}

		Partida p = partidas.get(idPartida);

		// Prohibido unirse
		if (p.getTurnoActual() > 1) {
			return "La primera ronda ya ha terminado, no puedes unirte ahora.";
		}

		// 10 jugadores máximo
		if (p.getNumJugadores() >= 10) {
			return "La partida está llena (máximo 10 jugadores).";
		}

		// Check de nombres duplicados
		for (int i = 0; i < p.getNumJugadores(); i++) {
			if (p.getJugadores()[i].getNombre().equals(nombre)) {
				return "El jugador '" + nombre + "' ya está en la partida";
			}
		}

		int pos = p.getNumJugadores();

		Jugador nuevo = new Jugador();
		nuevo.setNombre(nombre);
		nuevo.setIdJugador(pos);
		nuevo.setEliminado(false);

		// Asignamos las 5 cartas correspondientes del mazo
		Carta[] cartasNuevo = new Carta[5];
		// Repartimos las cartas del mazo según el orden de llegada (pos)
		// Si entra el segundo (pos=1), se lleva del índice 5 al 9 para no pisarse con
		// el resto
		for (int i = 0; i < 5; i++) {
			cartasNuevo[i] = p.getMazo().get(pos * 5 + i);
		}
		nuevo.setCartas(cartasNuevo);

		p.agregarJugador(nuevo);

		// Sacamos el listado actualizado para el frontend
		String[] nombresActuales = new String[p.getNumJugadores()];
		for (int i = 0; i < p.getNumJugadores(); i++) {
			nombresActuales[i] = p.getJugadores()[i].getNombre();
		}

		boolean esTuTurno = (p.getTurnoActual() == pos);
		Jugada ultimaJugResp;

		if (esTuTurno) {
			ultimaJugResp = p.getUltJugada();
		} else {
			ultimaJugResp = null;
		}

		String mensaje;
		if (esTuTurno) {
			mensaje = "Te has unido. ES TU TURNO.";
		} else {
			mensaje = "Te has unido. Turno de: " + p.getJugadores()[p.getTurnoActual()].getNombre();
		}

		return new RespuestaUnirse(p.getIdPartida(), cartasNuevo, nombresActuales, esTuTurno, ultimaJugResp, mensaje);
	}

	// Generación y barajado de la baraja francesa
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