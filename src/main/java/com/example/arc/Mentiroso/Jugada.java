package com.example.arc.Mentiroso;

//Representa una jugada declarada por un jugador
//El jugador declara un tipo y un valor, que puede ser mentira o verdad

public class Jugada {
	// Tipo de jugada declarada: "carta","pareja","doblePareja","trio","full","poker"
	String tipo;
	// Valor más alto declarado ya convertido a int para poder comparar con >
    // A=14, K=13, Q=12, J=11, 2-10=su número
    // Se guarda como int y no como String precisamente para poder hacer jugada1.valor > jugada2.valor
	int valor;
	// El jugador que ha hecho esta jugada
    // Se guarda el objeto entero para poder acceder a sus cartas reales al levantar
	Jugador jugador;
	
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
	
	public Jugador getJugador() {
		return jugador;
	}
	
	public void setJugador(Jugador jugador) {
		this.jugador = jugador;
	}
	
}
