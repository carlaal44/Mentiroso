package com.example.arc.Mentiroso;

public class RespuestaError {

    boolean ok;
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