package com.example.arc.Mentiroso;

import java.io.Serializable;

public class RespuestaInicio implements Serializable {
	private long idPartida;
	private Carta[] cartas;
	private String mensaje;

	public RespuestaInicio(long idPartida, Carta[] cartas, String mensaje) {
		this.idPartida = idPartida;
		this.cartas = cartas;
		this.mensaje = mensaje;
	}

	public long getIdPartida() {
		return idPartida;
	}

	public Carta[] getCartas() {
		return cartas;
	}

	public String getMensaje() {
		return mensaje;
	}
}