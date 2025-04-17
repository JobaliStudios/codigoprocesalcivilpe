package com.jobalistudios.codigoprocesalcivilpe.model;

public class Codigo {
    private String nombre;
    private String estado; // "Disponible" o "Próximamente"

    public Codigo(String nombre, String estado) {
        this.nombre = nombre;
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEstado() {
        return estado;
    }
}
