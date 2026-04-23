package com.example.arc.Mentiroso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Jugador hereda de Persona, por lo que ya tiene nombre y apellido
//Serializable permite convertir el objeto a JSON automáticamente (Spring lo necesita para devolver la respuesta)

public class Jugador extends Persona implements Serializable {
	long idJugador;
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
