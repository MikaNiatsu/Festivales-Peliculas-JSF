package co.edu.unbosque.persistence;

import java.time.LocalDate;

public class Proyeccion {
    private String cine;
    private int sala;
    private int cip;
    private LocalDate fecha_estreno;
    private int dias_estreno;
    private int espectadores;
    private int recaudacion;

    public Proyeccion(String cine, int sala, int cip, LocalDate fecha_estreno, int dias_estreno, int espectadores, int recaudacion) {
        this.cine = cine;
        this.sala = sala;
        this.cip = cip;
        this.fecha_estreno = fecha_estreno;
        this.dias_estreno = dias_estreno;
        this.espectadores = espectadores;
        this.recaudacion = recaudacion;
    }

    public String getCine() {
        return cine;
    }

    public void setCine(String cine) {
        this.cine = cine;
    }

    public int getSala() {
        return sala;
    }

    public void setSala(int sala) {
        this.sala = sala;
    }

    public int getCip() {
        return cip;
    }

    public void setCip(int cip) {
        this.cip = cip;
    }

    public LocalDate getFecha_estreno() {
        return fecha_estreno;
    }

    public void setFecha_estreno(LocalDate fecha_estreno) {
        this.fecha_estreno = fecha_estreno;
    }

    public int getDias_estreno() {
        return dias_estreno;
    }

    public void setDias_estreno(int dias_estreno) {
        this.dias_estreno = dias_estreno;
    }

    public int getEspectadores() {
        return espectadores;
    }

    public void setEspectadores(int espectadores) {
        this.espectadores = espectadores;
    }

    public int getRecaudacion() {
        return recaudacion;
    }

    public void setRecaudacion(int recaudacion) {
        this.recaudacion = recaudacion;
    }
}
