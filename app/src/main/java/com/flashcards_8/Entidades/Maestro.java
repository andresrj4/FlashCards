package com.flashcards_8.Entidades;

public class Maestro {

    private int idMaestro;
    private String nombreMaestro;
    private String contraseñaMaestro;

    public Maestro(int idMaestro, String nombreMaestro, String contraseñaMaestro) {
        this.idMaestro = idMaestro;
        this.nombreMaestro = nombreMaestro;
        this.contraseñaMaestro = contraseñaMaestro;
    }

    public Maestro() {}
    public int getIdMaestro() {
        return idMaestro;
    }
    public void setIdMaestro(int idMaestro) {
        this.idMaestro = idMaestro;
    }
    public String getNombreMaestro() {
        return nombreMaestro;
    }
    public void setNombreMaestro(String nombreMaestro) {
        this.nombreMaestro = nombreMaestro;
    }
    public String getContraseñaMaestro() {
        return contraseñaMaestro;
    }
    public void setContraseñaMaestro(String contraseñaMaestro) {this.contraseñaMaestro = contraseñaMaestro;}
}
