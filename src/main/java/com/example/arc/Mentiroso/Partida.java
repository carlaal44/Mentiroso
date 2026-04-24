package com.example.arc.Mentiroso;

import java.util.ArrayList;
import java.util.List;

// Representa una partida en curso
// Varias partidas pueden existir en paralelo en el servidor

public class Partida {

	// Identificador único de la partida
	// Coincide con el índice en el ArrayList<Partida> del controlador
	long idPartida;

	// Array fijo de 10 jugadores porque el máximo posible es 52/5 = 10
	// Se usa array en vez de ArrayList porque el tamaño máximo es conocido y fijo
	Jugador[] jugadores = new Jugador[10];

	// El mazo completo de 52 cartas mezcladas aleatoriamente al crear la partida
	// Se usa ArrayList porque Collections.shuffle() necesita una List para mezclar
	// Cada jugador n recibe las cartas desde mazo[n*5] hasta mazo[n*5+4]
	List<Carta> mazo = new ArrayList<Carta>();

	// Cuántos jugadores han entrado realmente a la partida
	// Sirve para saber qué posición del array está libre y qué bloque del mazo
	// repartir
	int numJugadores;

	
	//Para la logica de unirse y el resto
	int turnoActual;

	// La última jugada declarada, que el siguiente jugador debe superar o levantar
	// Es null al inicio de cada ronda (después de un levantar o al empezar)
	Jugada ultJugada;

	// true cuando solo queda un jugador no eliminado
	boolean finPartida;

	// Nombre del jugador que ha ganado, null mientras la partida sigue
	String ganador;

	public int getTurnoActual() {
		return turnoActual;
	}

	public void setTurnoActual(int turnoActual) {
		this.turnoActual = turnoActual;
	}

	public long getIdPartida() {
		return idPartida;
	}

	public void setIdPartida(long idPartida) {
		this.idPartida = idPartida;
	}

	public Jugador[] getJugadores() {
		return jugadores;
	}

	public void setJugadores(Jugador[] jugadores) {
		this.jugadores = jugadores;
	}

	public List<Carta> getMazo() {
		return mazo;
	}

	public void setMazo(List<Carta> mazo) {
		this.mazo = mazo;
	}

	public int getNumJugadores() {
		return numJugadores;
	}

	public void setNumJugadores(int numJugadores) {
		this.numJugadores = numJugadores;
	}

	public Jugada getUltJugada() {
		return ultJugada;
	}

	public void setUltJugada(Jugada ultJugada) {
		this.ultJugada = ultJugada;
	}

	public boolean isFinPartida() {
		return finPartida;
	}

	public void setFinPartida(boolean finPartida) {
		this.finPartida = finPartida;
	}

	public String getGanador() {
		return ganador;
	}

	public void setGanador(String ganador) {
		this.ganador = ganador;
	}

	// ----------------------------- Metodos importantes -------------------------------
	public void setHost(Jugador host) {
		this.jugadores[0] = host;
	}

	public void agregarJugador(Jugador nuevo) {
		if (this.numJugadores < this.jugadores.length) {
			this.jugadores[this.numJugadores] = nuevo;
			this.numJugadores++;
		}
	}

	public String getNombreJugadorEnTurno() {
		return this.jugadores[this.turnoActual].getNombre();
	}

}