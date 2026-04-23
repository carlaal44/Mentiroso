package com.example.arc.Mentiroso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Jugador implements Serializable {
	String nombre;
	Carta[] cartas = new Carta[5];
	Boolean activo;

	public Jugador() {

	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Carta[] getCartas() {
		return cartas;
	}

	public void setCartas(Carta[] cartas) {
		this.cartas = cartas;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

}
