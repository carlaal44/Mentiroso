package com.example.arc.Mentiroso;

import java.util.ArrayList;
import java.util.List;

//Representa una partida en curso pudiendo existir varias en paralelo.

public class Partida {

	// Coincide con el índice en el ArrayList<Partida> del controlador
	long idPartida;

	// Array fijo de 10 jugadores porque el máximo posible es 52/5 = 10
	Jugador[] jugadores = new Jugador[10];

	// El mazo completo de 52 cartas mezcladas aleatoriamente al crear la partida.
	// Cada jugador n recibe las cartas desde mazo[n*5] hasta mazo[n*5+4]
	List<Carta> mazo = new ArrayList<Carta>();

	// Cuántos jugadores han entrado realmente a la partida
	// Sirve para saber qué posición del array está libre y qué bloque del mazo hay
	// que repartir.
	int numJugadores;

	// Indice del jugador cada vez que alguien levanta.
	int turnoActual;
	// Mientras ronda sea 0 se pueden unir, si es mayor no se uniran.
	int ronda = 0;

	// La última jugada declarada, que el siguiente jugador debe superar o levantar
	// Es null al inicio de cada ronda (después de un levantar o al empezar)
	Jugada ultJugada;

	// true cuando solo queda un jugador no eliminado
	boolean finPartida;

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

	public int getRonda() {
		return ronda;
	}

	public void setRonda(int ronda) {
		this.ronda = ronda;
	}

	public void setHost(Jugador host) {//Metemos al creador de la partida como host.
		this.jugadores[0] = host;
	}

	public void agregarJugador(Jugador nuevo) {//Añadimos un jugador aumentado el numero de jugadores.
		if (this.numJugadores < this.jugadores.length) {
			this.jugadores[this.numJugadores] = nuevo;
			this.numJugadores++;
		}
	}

	public String getNombreJugadorEnTurno() {//Devuelve el nombre del que le toca jugar.
		return this.jugadores[this.turnoActual].getNombre();
	}

}