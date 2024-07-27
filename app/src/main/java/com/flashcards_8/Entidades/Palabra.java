package com.flashcards_8.Entidades;

import java.io.Serializable;

public class Palabra implements Serializable {
    private Integer idPalabra;
    private String textoPalabra;
    private String imagenPalabra;
    private String audioPalabra;
    private Integer nivelPalabra;
    private int vecesVistaPalabra;
    private int tiempoVista;
    private int vecesEscuchada;
    private boolean resultado;

    public Palabra(Integer idPalabra, String textoPalabra, String imagenPalabra, String audioPalabra, Integer nivelPalabra) {
        this.idPalabra = idPalabra;
        this.textoPalabra = textoPalabra;
        this.imagenPalabra = imagenPalabra;
        this.audioPalabra = audioPalabra;
        this.nivelPalabra = nivelPalabra;
    }

    public Palabra(int idPalabra, String textoPalabra) {
        this.idPalabra = idPalabra;
        this.textoPalabra = textoPalabra;
    }

    public Palabra() {}

    public Integer getIdPalabra() { return idPalabra; }
    public void setIdPalabra(Integer idPalabra) { this.idPalabra = idPalabra; }
    public String getTextoPalabra() { return textoPalabra; }
    public void setTextoPalabra(String textoPalabra) { this.textoPalabra = textoPalabra; }
    public String getImagenPalabra() { return imagenPalabra; }
    public void setImagenPalabra(String imagenPalabra) { this.imagenPalabra = imagenPalabra; }
    public String getAudioPalabra() { return audioPalabra; }
    public void setAudioPalabra(String audioPalabra) { this.audioPalabra = audioPalabra; }
    public Integer getNivelPalabra() { return nivelPalabra; }
    public void setNivelPalabra(Integer nivelPalabra) { this.nivelPalabra = nivelPalabra; }
    public int getVecesVistaPalabra() { return vecesVistaPalabra; }
    public void setVecesVistaPalabra(int vecesVistaPalabra) { this.vecesVistaPalabra = vecesVistaPalabra; }
    public int getTiempoVista() { return tiempoVista; }
    public void setTiempoVista(int tiempoVista) { this.tiempoVista = tiempoVista; }
    public int getVecesEscuchada() { return vecesEscuchada; }
    public void setVecesEscuchada(int vecesEscuchada) { this.vecesEscuchada = vecesEscuchada; }
    public boolean getResultado() { return resultado; }
    public void setResultado(boolean resultado) { this.resultado = resultado; }
}
