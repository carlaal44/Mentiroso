package com.example.arc.Mentiroso;

import java.io.Serializable;

public class RespuestaUnirse implements Serializable {

	private long idPartida;
	private Carta[] cartas;
	private String[] jugadoresActuales;
	private boolean esTuTurno;
	private Jugada ultJugada;
	private String mensaje;

	public RespuestaUnirse(long idPartida, Carta[] cartas, String[] jugadoresActuales, boolean esTuTurno,
			Jugada ultJugada, String mensaje) {
		this.idPartida = idPartida;
		this.cartas = cartas;
		this.jugadoresActuales = jugadoresActuales;
		this.esTuTurno = esTuTurno;
		this.ultJugada = ultJugada;
		this.mensaje = mensaje;
	}

	public long getIdPartida() {
		return idPartida;
	}

	public Carta[] getCartas() {
		return cartas;
	}

	public String[] getJugadoresActuales() {
		return jugadoresActuales;
	}

	public boolean isEsTuTurno() {
		return esTuTurno;
	}

	public Jugada getUltJugada() {
		return ultJugada;
	}

	public String getMensaje() {
		return mensaje;
	}
}