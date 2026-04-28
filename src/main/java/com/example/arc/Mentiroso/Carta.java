package com.example.arc.Mentiroso;

//Representa una carta de la baraja francesa

public class Carta {
	// Valor de la carta: "A","2","3",...,"10","J","Q","K"
	String valor;
	// Palo de la carta: "C" (corazones), "D" (diamantes), "T" (treboles), "P"
	// (picas).
	// Se necesita el palo para que las 52 cartas sean únicas y no se repitan al
	// repartir.
	String palo;

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getPalo() {
		return palo;
	}

	public void setPalo(String palo) {
		this.palo = palo;
	}

}
