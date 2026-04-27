package com.example.arc.Mentiroso;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ClienteConsola {

	static final String BASE = "http://localhost:8080";

	static Scanner sc = new Scanner(System.in);
	static HttpClient client = HttpClient.newHttpClient();
	static ObjectMapper om = new ObjectMapper();

	static int idPartida;
	static String nombre;

	public static void main(String[] args) {

		System.out.println("=== EL MENTIROSO ===");

		System.out.print("Tu nombre: ");
		nombre = sc.nextLine();

		System.out.println("1. Crear partida");
		System.out.println("2. Unirse a partida");
		System.out.print("Opcion: ");
		int opcion = Integer.parseInt(sc.nextLine());

		if (opcion == 1) {
			crearPartida();
		} else {
			System.out.print("ID partida: ");
			idPartida = Integer.parseInt(sc.nextLine());
			unirsePartida();
		}

		jugarBucle();
	}

	static void crearPartida() {
		try {
			String json = get("/crear?nombre=" + enc(nombre));
			RespuestaInicio r = om.readValue(json, RespuestaInicio.class);

			idPartida = (int) r.idPartida;

			System.out.println(r.mensaje);
			System.out.println("ID de la partida: " + idPartida);
			mostrarCartas(r.cartas);

		} catch (Exception e) {
			System.out.println("Error creando partida");
			System.out.println(e.getMessage());
		}
	}

	static void unirsePartida() {
		try {
			String json = get("/unirse?idPartida=" + idPartida + "&nombre=" + enc(nombre));
			RespuestaUnirse r = om.readValue(json, RespuestaUnirse.class);

			System.out.println(r.mensaje);
			mostrarCartas(r.cartas);
			mostrarJugadores(r.jugadoresActuales);

		} catch (Exception e) {
			System.out.println("Error al unirse");
			System.out.println(e.getMessage());
		}
	}

	static void jugarBucle() {

		boolean terminar = false;

		while (!terminar) {

			try {
				RespuestaUnirse estado = consultarEstado();

				System.out.println();
				System.out.println("----- ESTADO -----");
				mostrarJugadores(estado.jugadoresActuales);

				if (estado.ultJugada == null) {
					System.out.println("Ultima jugada: ninguna");
				} else {
					System.out.println("Ultima jugada: " + estado.ultJugada.jugador.nombre + " dijo "
							+ estado.ultJugada.tipo + " de " + textoValor(estado.ultJugada.valor));
				}

				if (estado.finPartida) {
					System.out.println("FIN DE PARTIDA");
					System.out.println("Ganador: " + estado.ganador);
					terminar = true;
				} else if (estado.esTuTurno && estado.jugadoresActuales.length >= 2) {
					System.out.println("ES TU TURNO");
					menuTurno();
				} else if (estado.esTuTurno && estado.jugadoresActuales.length < 2) {
					System.out.println("Esperando a que se una otro jugador...");
					System.out.println("Pulsa ENTER para actualizar...");
					sc.nextLine();
				} else {
					System.out.println(estado.mensaje);
					System.out.println("Pulsa ENTER para actualizar...");
					sc.nextLine();
				}

			} catch (Exception e) {
				System.out.println("Error consultando estado");
				System.out.println(e.getMessage());
			}
		}
	}

	static void menuTurno() {

		System.out.println("1. Jugar");
		System.out.println("2. Levantar");
		System.out.print("Opcion: ");
		int opcion = Integer.parseInt(sc.nextLine());

		if (opcion == 1) {
			jugar();

		} else {
			levantar();
		}
	}

	static void jugar() {

		String tipo = "";

		while (!tipoValido(tipo)) {
			System.out.println("Tipos: carta, pareja, doblepareja, trio, full, poker");
			System.out.print("Tipo: ");
			tipo = sc.nextLine().toLowerCase();

			if (!tipoValido(tipo)) {
				System.out.println("Tipo no válido. Intenta otra vez.");
			}
		}
		int valor = 0;

		while (valor == 0) {
			System.out.print("Valor (2-10, J, Q, K, A): ");
			String valorTexto = sc.nextLine().toUpperCase();

			valor = numeroValorSeguro(valorTexto);

			if (valor == 0) {
				System.out.println("Valor no válido. Prueba con 2-10, J, Q, K o A.");
			}
		}

		try {
			String json = get("/jugar?idPartida=" + idPartida + "&nombre=" + enc(nombre) + "&tipo=" + enc(tipo)
					+ "&valor=" + valor);

			RespuestaJugada r = om.readValue(json, RespuestaJugada.class);

			System.out.println(r.mensaje);

			if (r.siguienteTurno != null) {
				System.out.println("Siguiente turno: " + r.siguienteTurno);
			}

			if (r.finPartida) {
				System.out.println("Ganador: " + r.ganador);
			}

		} catch (Exception e) {
			System.out.println("Error al jugar");
			System.out.println(e.getMessage());
		}
	}

	static boolean tipoValido(String tipo) {
		return tipo.equals("carta") || tipo.equals("pareja") || tipo.equals("doblepareja") || tipo.equals("trio")
				|| tipo.equals("full") || tipo.equals("poker");
	}

	static int numeroValorSeguro(String valor) {

		if (valor.equals("A"))
			return 14;
		if (valor.equals("K"))
			return 13;
		if (valor.equals("Q"))
			return 12;
		if (valor.equals("J"))
			return 11;

		try {
			int n = Integer.parseInt(valor);
			if (n >= 2 && n <= 10)
				return n;
			return 0;
		} catch (Exception e) {
			return 0;
		}
	}

	static void levantar() {

		try {
			String json = get("/jugar?idPartida=" + idPartida + "&nombre=" + enc(nombre) + "&tipo=levantar&valor=0");

			RespuestaJugada r = om.readValue(json, RespuestaJugada.class);

			System.out.println(r.mensaje);

			if (r.eliminado != null) {
				System.out.println("Eliminado: " + r.eliminado);
			}

			if (r.finPartida) {
				System.out.println("Ganador: " + r.ganador);
			}

		} catch (Exception e) {
			System.out.println("Error al levantar");
			System.out.println(e.getMessage());
		}
	}

	static RespuestaUnirse consultarEstado() throws Exception {
		String json = get("/estado?idPartida=" + idPartida + "&nombre=" + enc(nombre));
		return om.readValue(json, RespuestaUnirse.class);
	}

	static String get(String endpoint) throws Exception {

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + endpoint)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body();
	}

	static String enc(String texto) {
		return URLEncoder.encode(texto, StandardCharsets.UTF_8);
	}

	static int numeroValor(String valor) {

		if (valor.equals("A")) {
			return 14;
		} else if (valor.equals("K")) {
			return 13;
		} else if (valor.equals("Q")) {
			return 12;
		} else if (valor.equals("J")) {
			return 11;
		} else {
			return Integer.parseInt(valor);
		}
	}

	static String textoValor(int valor) {

		if (valor == 14) {
			return "A";
		} else if (valor == 13) {
			return "K";
		} else if (valor == 12) {
			return "Q";
		} else if (valor == 11) {
			return "J";
		} else {
			return String.valueOf(valor);
		}
	}

	static void mostrarCartas(Carta[] cartas) {

		System.out.println("Tus cartas:");

		for (int i = 0; i < cartas.length; i++) {
			System.out.println("- " + cartas[i].valor + " de " + cartas[i].palo);
		}
	}

	static void mostrarJugadores(String[] jugadores) {

		System.out.println("Jugadores:");

		for (int i = 0; i < jugadores.length; i++) {
			System.out.println("- " + jugadores[i]);
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Carta {
		public String valor;
		public String palo;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Jugador {
		public String nombre;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Jugada {
		public String tipo;
		public int valor;
		public Jugador jugador;
		public boolean verdad;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RespuestaInicio {
		public long idPartida;
		public Carta[] cartas;
		public String mensaje;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RespuestaJugada {
		public boolean ok;
		public String mensaje;
		public String siguienteTurno;
		public String eliminado;
		public boolean finPartida;
		public String ganador;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RespuestaUnirse {
		public long idPartida;
		public Carta[] cartas;
		public String[] jugadoresActuales;
		public boolean esTuTurno;
		public Jugada ultJugada;
		public String mensaje;
		public boolean finPartida;
		public String ganador;
	}
}