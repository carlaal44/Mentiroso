package com.example.arc.Mentiroso;

import java.io.Serializable;

//Hemos creado esta clase porque los errores se devolvian en String y el cliente no lo leia entonces fallaba al no tenerlo en JSON.

public class RespuestaError implements Serializable{
	//Siempre false en un error
    boolean ok;
    //Mensaje a mostrar
    String mensaje;

    public RespuestaError() {
    }

    public RespuestaError(boolean ok, String mensaje) {
        this.ok = ok;
        this.mensaje = mensaje;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}