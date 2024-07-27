package com.flashcards_8.Entidades;

import java.util.HashMap;
import java.util.Map;

// Clase para cualquier tipo de sesion (ModoPractica -> Flashcards | ModoPrueba -> Simulacion de examen)
public class Sesion {
    private int idSesion;
    private int idAlumno;
    private int idMaestro;
    private String fechaSesion;
    private int aciertosSesion;
    private int fallosSesion;
    private String calificacionSesion;
    private int duracionSesion;
    private int nivelSesion;
    private String tipoSesion;
    private int intervaloSesion;
    private Map<Integer, PalabraSesion> respuestasSesion = new HashMap<>();

    public Sesion() {}

    public Sesion(int idSesion, int idMaestro, String fechaSesion, int nivelSesion, String tipoSesion, int intervaloSesion) {
        this.idSesion = idSesion;
        this.idMaestro = idMaestro;
        this.fechaSesion = fechaSesion;
        this.nivelSesion = nivelSesion;
        this.tipoSesion = tipoSesion;
        this.intervaloSesion = intervaloSesion;
        this.aciertosSesion = 0;
        this.fallosSesion = 0;
        this.duracionSesion = 0;
        this.calificacionSesion = "";
    }

    public Sesion(int idSesion, String fechaSesion, int nivelSesion, String tipoSesion) {
        this.idSesion = idSesion;
        this.fechaSesion = fechaSesion;
        this.nivelSesion = nivelSesion;
        this.tipoSesion = tipoSesion;
        this.aciertosSesion = 0;
        this.fallosSesion = 0;
        this.duracionSesion = 0;
        this.calificacionSesion = "";
        this.intervaloSesion = 0;
    }

    // Constructor para prueba
    public Sesion(int idSesion, int idMaestro, String fechaSesion, int nivelSesion, String tipoSesion, int intervaloSesion, int aciertosSesion, int fallosSesion, String calificacionSesion) {
        this.idSesion = idSesion;
        this.idMaestro = idMaestro;
        this.fechaSesion = fechaSesion;
        this.nivelSesion = nivelSesion;
        this.tipoSesion = tipoSesion;
        this.intervaloSesion = intervaloSesion;
        this.aciertosSesion = aciertosSesion;
        this.fallosSesion = fallosSesion;
        this.calificacionSesion = calificacionSesion;
    }

    public int getIdSesion() { return idSesion; }
    public void setIdSesion(int idSesion) { this.idSesion = idSesion; }
    public int getIdAlumno() { return idAlumno; }
    public void setIdAlumno(int idAlumno) { this.idAlumno = idAlumno; }
    public int getIdMaestro() { return idMaestro; }
    public String getFechaSesion() { return fechaSesion; }
    public void setFechaSesion(String fechaSesion) { this.fechaSesion = fechaSesion; }
    public int getAciertosSesion() { return aciertosSesion; }
    public void setAciertosSesion(int aciertosSesion) { this.aciertosSesion = aciertosSesion; }
    public int getFallosSesion() { return fallosSesion; }
    public void setFallosSesion(int fallosSesion) { this.fallosSesion = fallosSesion; }
    public String getCalificacionSesion() { return calificacionSesion; }
    public void setCalificacionSesion(String calificacionSesion) { this.calificacionSesion = calificacionSesion; }
    public int getDuracionSesion() { return duracionSesion; }
    public void setDuracionSesion(int duracionSesion) { this.duracionSesion = duracionSesion; }
    public int getNivelSesion() { return nivelSesion; }
    public void setNivelSesion(int nivelSesion) { this.nivelSesion = nivelSesion; }
    public String getTipoSesion() { return tipoSesion; }
    public void setTipoSesion(String tipoSesion) { this.tipoSesion = tipoSesion; }
    public int getIntervaloSesion() { return intervaloSesion; }
    public void setIntervaloSesion(int intervaloSesion) { this.intervaloSesion = intervaloSesion; }  // Añadir este método
    public Map<Integer, PalabraSesion> getRespuestasSesion() { return respuestasSesion; }
}
