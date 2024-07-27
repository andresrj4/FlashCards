package com.flashcards_8.Entidades;

import java.util.List;

// Actualmente guarda datos en tabla, sin embargo no se ha desarrollado esta funcionalidad (considerada
// obsoleta con la tabla de estadisticas de palabra por alumno (EstadisticasPalabra)
public class EstadisticasAlumno {
    private int idAlumno;
    private int palabrasVistasTotales;
    private int totalAciertos;
    private int totalFallos;
    private int totalSesionesPractica;
    private int totalSesionesPrueba;
    private float tiempoPromedioPorPalabra;
    private int palabraMasVista;
    private int palabraMasAcertada;
    private int palabraMenosAcertada;
    private int intervaloPreferido;

    public EstadisticasAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
        this.totalAciertos = 0;
        this.totalFallos = 0;
        this.totalSesionesPractica = 0;
        this.totalSesionesPrueba = 0;
        this.tiempoPromedioPorPalabra = 0;
        this.palabrasVistasTotales = 0;
        this.palabraMasVista = -1;
        this.palabraMasAcertada = -1;
        this.palabraMenosAcertada = -1;
        this.intervaloPreferido = 0;
    }

    public int getIdAlumno() { return idAlumno; }
    public int getTotalAciertos() { return totalAciertos; }
    public void incrementarAciertos(int aciertos) { this.totalAciertos += aciertos; }
    public int getTotalFallos() { return totalFallos; }
    public void incrementarFallos(int fallos) { this.totalFallos += fallos; }
    public int getTotalSesionesPractica() { return totalSesionesPractica; }
    public void incrementarSesionesPractica() { this.totalSesionesPractica += 1; }
    public void setTotalSesionesPractica(int totalSesionesPractica) { this.totalSesionesPractica = totalSesionesPractica; }
    public int getTotalSesionesPrueba() { return totalSesionesPrueba; }
    public void incrementarSesionesPrueba() { this.totalSesionesPrueba += 1; }
    public void setTotalSesionesPrueba(int totalSesionesPrueba) { this.totalSesionesPrueba = totalSesionesPrueba; }
    public float getTiempoPromedioPorPalabra() { return tiempoPromedioPorPalabra; }
    public void actualizarTiempoPromedioPorPalabra(float tiempoPromedio) { this.tiempoPromedioPorPalabra = tiempoPromedio; }
    public int getPalabrasVistasTotales() { return palabrasVistasTotales; }
    public void incrementarPalabrasVistasTotales(int cantidad) { this.palabrasVistasTotales += cantidad; }
    public int getPalabraMasVista() { return palabraMasVista; }
    public void setPalabraMasVista(int palabraMasVista) { this.palabraMasVista = palabraMasVista; }
    public int getPalabraMasAcertada() { return palabraMasAcertada; }
    public void setPalabraMasAcertada(int palabraMasAcertada) { this.palabraMasAcertada = palabraMasAcertada; }
    public int getPalabraMenosAcertada() { return palabraMenosAcertada; }
    public void setPalabraMenosAcertada(int palabraMenosAcertada) { this.palabraMenosAcertada = palabraMenosAcertada; }
    public int getIntervaloPreferido() { return intervaloPreferido; }
    public void setIntervaloPreferido(int intervaloPreferido) { this.intervaloPreferido = intervaloPreferido; }

    // Cálculos de estadísticas derivadas
    public int obtenerPalabraMasVista(List<EstadisticasPalabra> estadisticasPalabras) {
        EstadisticasPalabra palabraMasVista = estadisticasPalabras.stream()
                .max((p1, p2) -> Integer.compare(p1.getVecesVistaTotal(), p2.getVecesVistaTotal()))
                .orElse(null);
        return palabraMasVista != null ? palabraMasVista.getIdPalabra() : -1;
    }

    public int obtenerPalabraMasAcertada(List<EstadisticasPalabra> estadisticasPalabras) {
        EstadisticasPalabra palabraMasAcertada = estadisticasPalabras.stream()
                .max((p1, p2) -> Float.compare(p1.getPromedioAciertos(), p2.getPromedioAciertos()))
                .orElse(null);
        return palabraMasAcertada != null ? palabraMasAcertada.getIdPalabra() : -1;
    }

    public int obtenerPalabraMenosAcertada(List<EstadisticasPalabra> estadisticasPalabras) {
        EstadisticasPalabra palabraMenosAcertada = estadisticasPalabras.stream()
                .min((p1, p2) -> Float.compare(p1.getPromedioAciertos(), p2.getPromedioAciertos()))
                .orElse(null);
        return palabraMenosAcertada != null ? palabraMenosAcertada.getIdPalabra() : -1;
    }

    public int obtenerPalabraMenosVista(List<EstadisticasPalabra> estadisticasPalabras) {
        EstadisticasPalabra palabraMenosVista = estadisticasPalabras.stream()
                .min((p1, p2) -> Integer.compare(p1.getVecesVistaTotal(), p2.getVecesVistaTotal()))
                .orElse(null);
        return palabraMenosVista != null ? palabraMenosVista.getIdPalabra() : -1;
    }

    public int obtenerIntervaloPreferido(List<Sesion> sesionesPractica) {
        return (int) sesionesPractica.stream()
                .mapToInt(Sesion::getIntervaloSesion)
                .average()
                .orElse(0);
    }
}
