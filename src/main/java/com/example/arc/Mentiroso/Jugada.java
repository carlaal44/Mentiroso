package com.example.arc.Mentiroso;

//Representa una jugada declarada por un jugador

public class Jugada {
	// Tipo de jugada declarada
	String tipo;
	// Valor más alto declarado ya convertido a int para poder comparar con >
	int valor, valor2;
	// El jugador que ha hecho esta jugada
	// Se guarda el objeto entero para poder acceder a sus cartas reales al levantar
	Jugador jugador;
	// Calculado con tieneJugada() al declarar y guardado aqui para no recalcularlo
	// al levantar
	// True tenia la combinacion correcta y con False es que mentia.
	boolean verdad;

	public boolean isVerdad() {
		return verdad;
	}

	public void setVerdad(boolean verdad) {
		this.verdad = verdad;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}
	
	public int getValor2() {
	    return valor2;
	}

	public void setValor2(int valor2) {
	    this.valor2 = valor2;
	}

	public Jugador getJugador() {
		return jugador;
	}

	public void setJugador(Jugador jugador) {
		this.jugador = jugador;
	}

}
