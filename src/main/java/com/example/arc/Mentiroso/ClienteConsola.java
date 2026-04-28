package com.example.arc.Mentiroso;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.ObjectMapper;

//Hacemos peticiones GET al servidor, usamos ObjetcMapper para convertir la respuesta.

public class ClienteConsola {

	static final String BASE = "http://192.168.1.62:8080";

	static Scanner in = new Scanner(System.in);
	static HttpClient client = HttpClient.newHttpClient();
	static ObjectMapper om = new ObjectMapper();

	static long idPartida;
	static String nombre;
	static Carta[] misCartas;

	public static void main(String[] args) {
		int opcion = 0;

		System.out.println("=== EL MENTIROSO ===");

		System.out.print("Tu nombre: ");
		nombre = in.nextLine();

		System.out.println("1. Crear partida");
		System.out.println("2. Unirse a partida");

		while (opcion != 1 && opcion != 2) {
			System.out.print("Opcion: ");
			try {
				opcion = Integer.parseInt(in.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Por favor, introduce 1 o 2.");
			}
		}

		if (opcion == 1) {
			System.out.println();
			crearPartida();
		} else {
			long idPartidaTemp = -1;

			while (idPartidaTemp < 0) {
				System.out.print("ID partida: ");

				if (in.hasNextLong()) {
					idPartidaTemp = in.nextLong();
					in.nextLine(); // limpiar salto de línea

					if (idPartidaTemp < 0) {
						System.out.println("El ID debe ser positivo.");
					}
				} else {
					System.out.println("Introduce un número válido.");
					in.nextLine(); // limpiar lo que haya escrito
				}
			}

			idPartida = idPartidaTemp;
			unirsePartida();
		}
		// Entramos.
		jugarBucle();
	}

	static void crearPartida() {// Llamamos al endpoint de /crear guarda el idPartida y muestra cartas.
		try {
			String json = get("/crear?nombre=" + enc(nombre));
			RespuestaInicio r = om.readValue(json, RespuestaInicio.class);

			idPartida = r.idPartida;

			System.out.println(r.mensaje);
			System.out.println("ID de la partida: " + idPartida);
			mostrarCartas(r.cartas);
			misCartas = r.cartas;

		} catch (Exception e) {
			System.out.println("Error creando partida");
			if (e.getMessage() == null) {
				System.out.println("Error de conexión: El servidor no responde o está inactivo.");
			} else {
				System.out.println(e.getMessage());
			}
		}
	}

	static void unirsePartida() {// Llamamos al endpoint /unirse con el idPartida introducido por el usuario.
		try {
			String json = get("/unirse?idPartida=" + idPartida + "&nombre=" + enc(nombre));

			if (json.contains("\"ok\":false")) {
				RespuestaError r = om.readValue(json, RespuestaError.class);
				System.out.println(r.mensaje);
				System.exit(0);
			}

			RespuestaUnirse r = om.readValue(json, RespuestaUnirse.class);

			System.out.println(r.mensaje);
			mostrarCartas(r.cartas);
			misCartas = r.cartas;

		} catch (Exception e) {
			System.err.println("Error al unirse");
			if (e.getMessage() == null) {
				System.err.println("Error de conexión: El servidor no responde o está inactivo.");
			} else {
				System.err.println(e.getMessage());
			}
			System.exit(0);
		}
	}

	static void jugarBucle() {// Bucle en el que se repite hasta que se ejecuta finPartida a true, consultando
								// el estado y decide que mostrar segun si es el turno del juador.

		boolean terminar = false;

		while (!terminar) {

			try {
				RespuestaUnirse estado = consultarEstado();

				System.out.println();
				System.out.println("----- ESTADO -----");
				mostrarJugadores(estado.jugadoresActuales);
				System.out.println();

				if (misCartas != null) {
		            System.out.println("------ TUS CARTAS ------");
		            mostrarCartas(misCartas);
		        }
				// Mostramos ultima jugada si existe.
				if (estado.ultJugada == null) {
					System.out.println("Ultima jugada: Ninguna");
				} else if (estado.ultJugada.valor2 > 0) {
					System.out.println("Ultima jugada: " + estado.ultJugada.jugador.nombre + " dijo "
							+ estado.ultJugada.tipo + " de " + textoValor(estado.ultJugada.valor) + " y "
							+ textoValor(estado.ultJugada.valor2));
					System.out.println();
				} else {
					System.out.println("Ultima jugada: " + estado.ultJugada.jugador.nombre + " dijo "
							+ estado.ultJugada.tipo + " de " + textoValor(estado.ultJugada.valor));
					System.out.println();
				}

				// Partida terminada por lo que mostramos el ganador y salimos del bucle.
				if (estado.finPartida) {
					System.out.println("FIN DE PARTIDA");
					System.out.println("Ganador: " + estado.ganador);
					System.out.println();
					terminar = true;
				} else if (estado.esTuTurno && estado.jugadoresActuales.length >= 2) {
					System.out.println("ES TU TURNO");
					if (estado.ultJugada == null) {
				        System.out.println("(No puedes levantar, no hay jugada anterior)");
				    }
				    menuTurno(estado.ultJugada != null);
				} else if (estado.esTuTurno && estado.jugadoresActuales.length < 2) {
					// Si es nuestro turno y estamos solos esperamos a los demas. (No tiene sentido
					// jugar 1 solo).
					System.out.println("Esperando a que se una otro jugador...");
					System.out.println("Pulsa ENTER para actualizar...");
					in.nextLine();
					System.out.println();
				} else {
					System.out.println(estado.mensaje);
					System.out.println("Pulsa ENTER para actualizar...");
					in.nextLine();
					System.out.println();
				}

			} catch (Exception e) {
				System.out.println("Error consultando estado");
				if (e.getMessage() == null) {
					System.out.println("Error de conexión: El servidor no responde o está inactivo.");
				} else {
					System.out.println(e.getMessage());
				}
			}
		}

	}

	static void menuTurno(boolean puedeLevantar) {
	    System.out.println("1. Jugar");
	    if (puedeLevantar) System.out.println("2. Levantar");

	    int opcion = 0;
	    while (opcion != 1 && (opcion != 2 || !puedeLevantar)) {
	        System.out.print("Opcion: ");
	        try {
	            opcion = Integer.parseInt(in.nextLine());
	            if (opcion == 2 && !puedeLevantar) {
	                System.out.println("No puedes levantar ahora, no hay jugada anterior.");
	            }
	        } catch (NumberFormatException e) {
	            System.out.println("Por favor, introduce una opción válida.");
	        }
	    }

	    if (opcion == 1) jugar();
	    else levantar();
	}

	static void jugar() {// Pide tipo y valor, validandolos.

		String tipo = "";
		System.out.println();
		
		while (!tipoValido(tipo)) {
			System.out.println("----Tipos----");
			System.out.println("- Carta.");
			System.out.println("- Pareja.");
			System.out.println("- Doble Pareja.");
			System.out.println("- Trio.");
			System.out.println("- Full.");
			System.out.println("- Poker.");
			System.out.print("Tipo: ");
			tipo = in.nextLine().toLowerCase().trim().replace(" ", "");

			if (!tipoValido(tipo)) {
				System.out.println("Tipo no válido. Intenta otra vez.");
			}
		}

		int valor = 0;
		int valor2 = 0;
		
		System.out.println();
		
		while (valor == 0) {// Validamos.
			if (tipo.equals("doblepareja") || tipo.equals("full")) {
				System.out.print("Valor MÁS ALTO (2-10, J, Q, K, A): ");
			} else {
				System.out.print("Valor (2-10, J, Q, K, A): ");
			}

			String valorTexto = in.nextLine().toUpperCase();
			valor = numeroValorSeguro(valorTexto);

			if (valor == 0) {
				System.out.println("Valor no válido. Prueba con 2-10, J, Q, K o A.");
			}
		}

		if (tipo.equals("doblepareja") || tipo.equals("full")) {
			while (valor2 == 0) {
				System.out.print("Segundo valor (2-10, J, Q, K, A): ");
				String valorTexto2 = in.nextLine().toUpperCase();

				valor2 = numeroValorSeguro(valorTexto2);

				if (valor2 == 0) {
					System.err.println("Valor no válido. Prueba con 2-10, J, Q, K o A.");
				} else if (valor2 == valor) {
					System.err.println("Los dos valores no pueden ser iguales.");
					valor2 = 0;
				}
			}
		}

		try {
			String json = get("/jugar?idPartida=" + idPartida + "&nombre=" + enc(nombre) + "&tipo=" + enc(tipo)
					+ "&valor=" + valor + "&valor2=" + valor2); // enc explicado mas abajo.

			RespuestaJugada r = om.readValue(json, RespuestaJugada.class);

			System.out.println(r.mensaje);

			if (r.siguienteTurno != null) {
				System.out.println("Siguiente turno: " + r.siguienteTurno);
			}

			if (r.finPartida) {
				System.out.println("Ganador: " + r.ganador);
			}

		} catch (Exception e) {
			System.err.println("Error al jugar");
			if (e.getMessage() == null) {
				System.err.println("Error de conexión: El servidor no responde o está inactivo.");
			} else {
				System.err.println(e.getMessage());
			}
		}
	}

	static boolean tipoValido(String tipo) {// Comprobamos si es valido.
		return tipo.equalsIgnoreCase("carta") || tipo.equalsIgnoreCase("pareja") || tipo.equalsIgnoreCase("doblepareja")
				|| tipo.equalsIgnoreCase("trio") || tipo.equalsIgnoreCase("full") || tipo.equalsIgnoreCase("poker");
	}

	static int numeroValorSeguro(String valor) {// Convertimos el String a int.

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

	static void levantar() {// Llamamos al endpoint /jugar con tipo = levantar para mirar la jugada
							// anterior.

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
			if (e.getMessage() == null) {
				System.out.println("Error de conexión: El servidor no responde o está inactivo.");
			} else {
				System.out.println(e.getMessage());
			}
		}
	}

	static RespuestaUnirse consultarEstado() throws Exception {// Consultamos el estado actual llamando al endpoint
																// /estado.
		String json = get("/estado?idPartida=" + idPartida + "&nombre=" + enc(nombre));
		return om.readValue(json, RespuestaUnirse.class);
	}

	static String get(String endpoint) throws Exception {// Metodo que hace la peticion GET al servidor y devuelve el
															// cuerpo como String.

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE + endpoint)).build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body();
	}

	// Codifica el texto para que sea seguro en una URL, convirtiendo espacios y
	// caracteres especiales a una URL.
	static String enc(String texto) {
		return URLEncoder.encode(texto, StandardCharsets.UTF_8);
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

	static void mostrarCartas(Carta[] cartas) {// Mostramos cartas al jugador.

		System.out.println("Tus cartas:");

		for (int i = 0; i < cartas.length; i++) {
			System.out.println("- " + cartas[i].valor + " de " + cartas[i].palo);
		}
	}

	static void mostrarJugadores(String[] jugadores) {// Mostramos lista de jugadores.

		System.out.println("Jugadores:");

		for (int i = 0; i < jugadores.length; i++) {
			System.out.println("- " + jugadores[i]);
		}
	}

	// Clases internas para mapear las respuestas JSON del servidor.
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
		public int valor2;
		public Jugador jugador;
		public boolean verdad;
	}

	// Mapea la respuesta de /crear.
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RespuestaInicio {
		public long idPartida;
		public Carta[] cartas;
		public String mensaje;
	}

	// Mapea la respuesta de /jugar y /levantar.
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RespuestaJugada {
		public boolean ok;
		public String mensaje;
		public String siguienteTurno;
		public String eliminado;
		public boolean finPartida;
		public String ganador;
	}

	// Mapea la respuesta de /estado y /unirse.
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

	// Mapea las respuestas de error del servidor.
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RespuestaError {
		public boolean ok;
		public String mensaje;
	}
}