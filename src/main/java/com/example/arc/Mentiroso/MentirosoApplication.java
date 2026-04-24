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

	public static record RespuestaJugada(boolean ok, String mensaje, String siguienteTurno, String eliminado,
			boolean finPartida, String ganador) {
	}
	
	public static record RespuestaUnirse(long idPartida, Carta[] cartas, String[] jugadoresActuales, boolean esTuTurno,
			Jugada ultJugada, String mensaje) {
		
	}

	public static void main(String[] args) {
		SpringApplication.run(MentirosoApplication.class, args);
		System.out.println("Servidor levantado y listo");
	}

	// PRIMER ENDPOINT
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

	// SEGUNDO ENDPOINT
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

	// TERCER ENDPOINT
	@GetMapping("/jugar")
	public RespuestaJugada jugar(@RequestParam int idPartida, @RequestParam String nombre, @RequestParam String tipo,
			@RequestParam(required = false, defaultValue = "0") int valor) {

		// Comprobamos que exista la partida
		if (idPartida < 0 || idPartida >= partidas.size()) {
			return new RespuestaJugada(false, "La partida no existe", null, null, false, null);
		}

		Partida p = partidas.get(idPartida);

		// Si ya acabó no se puede jugar
		if (p.isFinPartida()) {
			return new RespuestaJugada(false, "La partida ya ha terminado", null, null, true, p.getGanador());
		}

		// Buscamos al jugador por nombre
		Jugador jugador = buscarJugador(p, nombre);

		if (jugador == null) {
			return new RespuestaJugada(false, "Ese jugador no está en la partida", null, null, false, null);
		}

		if (jugador.isEliminado()) {
			return new RespuestaJugada(false, "Estás eliminado", null, null, false, null);
		}

		// Comprobamos que sea su turno
		Jugador jugadorTurno = p.getJugadores()[p.getTurnoActual()];

		if (!jugadorTurno.getNombre().equalsIgnoreCase(nombre)) {
			return new RespuestaJugada(false, "No es tu turno", jugadorTurno.getNombre(), null, false, null);
		}

		// Si quiere levantar la jugada anterior
		if (tipo.equalsIgnoreCase("levantar")) {
			return levantar(p, jugador);
		}

		// Comprobamos que el tipo de jugada exista
		int fuerzaNueva = fuerzaTipo(tipo);

		if (fuerzaNueva == 0) {
			return new RespuestaJugada(false, "Tipo de jugada no válido", nombre, null, false, null);
		}

		Jugada anterior = p.getUltJugada();

		// Si hay jugada anterior esta debe superarla
		if (anterior != null) {
			int fuerzaAnterior = fuerzaTipo(anterior.getTipo());

			if (fuerzaNueva < fuerzaAnterior) {
				return new RespuestaJugada(false, "La jugada no supera a la anterior", nombre, null, false, null);
			}

			if (fuerzaNueva == fuerzaAnterior && valor <= anterior.getValor()) {
				return new RespuestaJugada(false, "La jugada no supera a la anterior", nombre, null, false, null);
			}
		}

		// Guardamos la jugada
		Jugada nueva = new Jugada();
		nueva.setTipo(tipo);
		nueva.setValor(valor);
		nueva.setJugador(jugador);

		// Aquí guardamos si decía verdad o mentía
		nueva.setVerdad(tieneJugada(jugador, tipo, valor));

		p.setUltJugada(nueva);

		// Pasamos turno
		avanzarTurno(p);

		String siguiente = p.getJugadores()[p.getTurnoActual()].getNombre();

		return new RespuestaJugada(true, "Jugada aceptada", siguiente, null, false, null);
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

	// Busca un jugador por nombre dentro de una partida
	private Jugador buscarJugador(Partida p, String nombre) {
		for (int i = 0; i < p.getNumJugadores(); i++) {
			if (p.getJugadores()[i].getNombre().equalsIgnoreCase(nombre)) {
				return p.getJugadores()[i];
			}
		}
		return null;
	}

	// Da una fuerza a cada jugada para compararlas
	private int fuerzaTipo(String tipo) {
		switch (tipo.toLowerCase()) {
		case "carta":
			return 1;
		case "pareja":
			return 2;
		case "doblepareja":
			return 3;
		case "trio":
			return 4;
		case "full":
			return 5;
		case "poker":
			return 6;
		default:
			return 0;
		}
	}

	// Pasa al siguiente jugador que no este eliminado
	private void avanzarTurno(Partida p) {
		int siguiente = p.getTurnoActual();

		do {
			siguiente = (siguiente + 1) % p.getNumJugadores();
		} while (p.getJugadores()[siguiente].isEliminado());

		p.setTurnoActual(siguiente);
	}

	// Convierte las cartas a número para comparar
	private int valorNumerico(String v) {
		switch (v) {
		case "As":
			return 14;
		case "A":
			return 14;
		case "K":
			return 13;
		case "Q":
			return 12;
		case "J":
			return 11;
		default:
			return Integer.parseInt(v);
		}
	}

	// Comprueba si el jugador tiene realmente la jugada que ha declarado
	private boolean tieneJugada(Jugador j, String tipo, int valor) {

		int[] contador = new int[15];

		for (Carta c : j.getCartas()) {
			int valCarta = valorNumerico(c.getValor());
			contador[valCarta]++;
		}

		switch (tipo.toLowerCase()) {

		case "carta":
			return contador[valor] >= 1;

		case "pareja":
			return contador[valor] >= 2;

		case "trio":
			return contador[valor] >= 3;

		case "poker":
			return contador[valor] >= 4;

		case "doblepareja":
			int parejas = 0;
			for (int i = 2; i < contador.length; i++) {
				if (contador[i] >= 2) {
					parejas++;
				}
			}
			return parejas >= 2;

		case "full":
			boolean hayTrio = false;
			boolean hayPareja = false;

			for (int i = 2; i < contador.length; i++) {
				if (contador[i] >= 3) {
					hayTrio = true;
				} else if (contador[i] >= 2) {
					hayPareja = true;
				}
			}

			return hayTrio && hayPareja;

		default:
			return false;
		}
	}

	// Resuelve cuando un jugador levanta la jugada anterior
	private RespuestaJugada levantar(Partida p, Jugador jugador) {

		Jugada anterior = p.getUltJugada();

		if (anterior == null) {
			return new RespuestaJugada(false, "No hay jugada que levantar", null, null, false, null);
		}

		Jugador jugadorAnterior = anterior.getJugador();
		String eliminado;

		// Si el anterior decía la verdad pierde el que levanta
		if (anterior.isVerdad()) {
			jugador.setEliminado(true);
			eliminado = jugador.getNombre();
		} else {
			// Si el anterior mentía pierde el anterior
			jugadorAnterior.setEliminado(true);
			eliminado = jugadorAnterior.getNombre();
		}

		// Después de levantar ya no hay jugada anterior
		p.setUltJugada(null);

		// Miramos si queda solo un jugador vivo
		int vivos = 0;
		String ganador = null;

		for (int i = 0; i < p.getNumJugadores(); i++) {
			if (!p.getJugadores()[i].isEliminado()) {
				vivos++;
				ganador = p.getJugadores()[i].getNombre();
			}
		}

		if (vivos == 1) {
			p.setFinPartida(true);
			p.setGanador(ganador);
			return new RespuestaJugada(true, "Fin de partida", null, eliminado, true, ganador);
		}

		avanzarTurno(p);

		String siguiente = p.getJugadores()[p.getTurnoActual()].getNombre();

		return new RespuestaJugada(true, "Se ha levantado la jugada", siguiente, eliminado, false, null);
	}
}
