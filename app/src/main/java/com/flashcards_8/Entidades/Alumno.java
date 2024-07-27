package com.flashcards_8.Entidades;

public class Alumno {

    private Integer idAlumno;
    private String nombreAlumno;
    private String apellidosAlumno;
    private String sexoAlumno;
    private String edadAlumno;
    private String edadMentalAlumno;
    private String comentariosAlumno;

    public Alumno(Integer idAlumno, String nombreAlumno, String apellidosAlumno, String sexoAlumno, String edadAlumno, String edadMentalAlumno, String comentariosAlumno) {
        this.idAlumno = idAlumno;
        this.nombreAlumno = nombreAlumno;
        this.apellidosAlumno = apellidosAlumno;
        this.sexoAlumno = sexoAlumno;
        this.edadAlumno = edadAlumno;
        this.edadMentalAlumno = edadMentalAlumno;
        this.comentariosAlumno = comentariosAlumno;
    }

    public Alumno() {}

    public Integer getIdAlumno() {
        return idAlumno;
    }
    public void setIdAlumno(Integer idAlumno) {
        this.idAlumno = idAlumno;
    }
    public String getNombreAlumno() {return nombreAlumno;}
    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }
    public String getApellidosAlumno() {
        return apellidosAlumno;
    }
    public void setApellidosAlumno(String apellidosAlumno) {this.apellidosAlumno = apellidosAlumno;}
    public String getSexoAlumno() {
        return sexoAlumno;
    }
    public void setSexoAlumno(String sexoAlumno) {
        this.sexoAlumno = sexoAlumno;
    }
    public String getEdadAlumno() {
        return edadAlumno;
    }
    public void setEdadAlumno(String edadAlumno) {
        this.edadAlumno = edadAlumno;
    }
    public String getEdadMentalAlumno() {
        return edadMentalAlumno;
    }
    public void setEdadMentalAlumno(String edadMentalAlumno) {this.edadMentalAlumno = edadMentalAlumno;}
    public String getComentariosAlumno() {
        return comentariosAlumno;
    }
    public void setComentariosAlumno(String comentariosAlumno) {this.comentariosAlumno = comentariosAlumno;}
}
