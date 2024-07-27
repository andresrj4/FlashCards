package com.flashcards_8.Entidades;

// Clase para cada palabra y sus estadisticas individuales
public class EstadisticasPalabra {
    private int idPalabra;
    private int totalTiempoVista;
    private int totalVecesEscuchada;
    private int totalVecesVista;
    private int totalAciertos;
    private int totalFallos;
    private String textoPalabra;
    private int nivel;

    public EstadisticasPalabra(int idPalabra, int totalVecesVista, int totalAciertos, int totalFallos, int totalTiempoVista, int totalVecesEscuchada) {
        this.idPalabra = idPalabra;
        this.totalVecesVista = totalVecesVista;
        this.totalAciertos = totalAciertos;
        this.totalFallos = totalFallos;
        this.totalTiempoVista = totalTiempoVista;
        this.totalVecesEscuchada = totalVecesEscuchada;
    }

    public int getIdPalabra() { return idPalabra; }
    public int getTotalTiempoVista() { return totalTiempoVista; }
    public void agregarTiempoVista(int tiempo) { this.totalTiempoVista += tiempo; }
    public int getTotalVecesEscuchada() { return totalVecesEscuchada; }
    public void incrementarVecesEscuchada() { this.totalVecesEscuchada += 1; }
    public void setTotalVecesEscuchada(int vecesEscuchada) { this.totalVecesEscuchada = vecesEscuchada; }
    public int getTotalVecesVista() { return totalVecesVista; }
    public void incrementarVecesVista(int veces) { this.totalVecesVista += veces; }
    public int getTotalAciertos() { return totalAciertos; }
    public void incrementarAciertos(int aciertos) { this.totalAciertos += aciertos; }
    public int getTotalFallos() { return totalFallos; }
    public void incrementarFallos(int fallos) { this.totalFallos += fallos; }

    public int getVecesVistaTotal() {
        return totalVecesVista;
    }

    public float getPromedioAciertos() {
        int totalIntentos = totalAciertos + totalFallos;
        return totalIntentos == 0 ? 0 : (float) totalAciertos / totalIntentos;
    }

    public String getTextoPalabra() {
        return textoPalabra;
    }

    public void setTextoPalabra(String textoPalabra) {
        this.textoPalabra = textoPalabra;
    }

    public int getNivelPalabra() {
        return nivel;
    }

    public void setNivelPalabra(int nivel) {
        this.nivel = nivel;
    }
}
