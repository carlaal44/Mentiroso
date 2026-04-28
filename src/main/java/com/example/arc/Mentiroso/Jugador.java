package com.example.arc.Mentiroso;

import java.io.Serializable;

//Jugador hereda de Persona, por lo que ya tiene nombre y apellido.
//Serializable permite convertir el objeto a JSON para mandarselo al cliente.

public class Jugador extends Persona implements Serializable {
	// Identificador propio de cada jugador, empezando en [0] que sera el host.
	long idJugador;
	// Verdadero si fue eliminado por levantar, saltandose el avanzarTurno().
	boolean eliminado;
	// Las 5 cartas que le tocan al jugador del mazo
	// Tamaño fijo de 5 porque siempre se reparten exactamente 5
	Carta[] cartas = new Carta[5];

	public long getIdJugador() {
		return idJugador;
	}

	public void setIdJugador(long idJugador) {
		this.idJugador = idJugador;
	}

	public boolean isEliminado() {
		return eliminado;
	}

	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;
	}

	public Carta[] getCartas() {
		return cartas;
	}

	public void setCartas(Carta[] cartas) {
		this.cartas = cartas;
	}

}
