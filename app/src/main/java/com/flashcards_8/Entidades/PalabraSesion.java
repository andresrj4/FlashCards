package com.flashcards_8.Entidades;

// Clase para los datos monitoreables de cada palabra
public class PalabraSesion {
    private int tiempoVista;
    private int vecesEscuchada;
    private int vecesVista;
    private int aciertosPalabraSesion;
    private int fallosPalabraSesion;

    public PalabraSesion() {
        this.tiempoVista = 0;
        this.vecesEscuchada = 0;
        this.vecesVista = 0;
        this.aciertosPalabraSesion = 0;
        this.fallosPalabraSesion = 0;
    }

    public int getTiempoVista() { return tiempoVista; }
    public void incrementarTiempoVista(int incremento) { this.tiempoVista += incremento; }

    public int getVecesEscuchada() { return vecesEscuchada; }
    public void incrementarVecesEscuchada() { this.vecesEscuchada += 1; }

    public int getVecesVista() { return vecesVista; }
    public void incrementarVecesVista() { this.vecesVista += 1; }

    public int getAciertosPalabraSesion() { return aciertosPalabraSesion; }
    public void incrementarAciertos() { this.aciertosPalabraSesion += 1; }

    public int getFallosPalabraSesion() { return fallosPalabraSesion; }
    public void incrementarFallos() { this.fallosPalabraSesion += 1; }
}
