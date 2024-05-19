package co.edu.unbosque.persistence;

import java.time.LocalDate;

/**
 * La clase Proyeccion representa la proyección de una película en un cine.
 */
public class Proyeccion {
    private String cine;
    private int sala;
    private int cip;
    private LocalDate fecha_estreno;
    private int dias_estreno;
    private int espectadores;
    private int recaudacion;

    /**
     * Constructor de la clase Proyeccion.
     * 
     * @param cine           El nombre del cine donde se proyecta la película.
     * @param sala           El número de sala donde se proyecta la película.
     * @param cip            El código de identificación de la película.
     * @param fecha_estreno  La fecha de estreno de la película.
     * @param dias_estreno   La duración en días de la proyección.
     * @param espectadores   El número de espectadores durante la proyección.
     * @param recaudacion    La recaudación obtenida durante la proyección.
     */
    public Proyeccion(String cine, int sala, int cip, LocalDate fecha_estreno, int dias_estreno, int espectadores, int recaudacion) {
        this.cine = cine;
        this.sala = sala;
        this.cip = cip;
        this.fecha_estreno = fecha_estreno;
        this.dias_estreno = dias_estreno;
        this.espectadores = espectadores;
        this.recaudacion = recaudacion;
    }

    /**
     * Obtiene el nombre del cine.
     * 
     * @return El nombre del cine.
     */
    public String getCine() {
        return cine;
    }

    /**
     * Establece el nombre del cine.
     * 
     * @param cine El nombre del cine.
     */
    public void setCine(String cine) {
        this.cine = cine;
    }

    /**
     * Obtiene el número de sala.
     * 
     * @return El número de sala.
     */
    public int getSala() {
        return sala;
    }

    /**
     * Establece el número de sala.
     * 
     * @param sala El número de sala.
     */
    public void setSala(int sala) {
        this.sala = sala;
    }

    /**
     * Obtiene el código de identificación de la película.
     * 
     * @return El código de identificación de la película.
     */
    public int getCip() {
        return cip;
    }

    /**
     * Establece el código de identificación de la película.
     * 
     * @param cip El código de identificación de la película.
     */
    public void setCip(int cip) {
        this.cip = cip;
    }

    /**
     * Obtiene la fecha de estreno.
     * 
     * @return La fecha de estreno.
     */
    public LocalDate getFecha_estreno() {
        return fecha_estreno;
    }

    /**
     * Establece la fecha de estreno.
     * 
     * @param fecha_estreno La fecha de estreno.
     */
    public void setFecha_estreno(LocalDate fecha_estreno) {
        this.fecha_estreno = fecha_estreno;
    }

    /**
     * Obtiene la duración en días de la proyección.
     * 
     * @return La duración en días de la proyección.
     */
    public int getDias_estreno() {
        return dias_estreno;
    }

    /**
     * Establece la duración en días de la proyección.
     * 
     * @param dias_estreno La duración en días de la proyección.
     */
    public void setDias_estreno(int dias_estreno) {
        this.dias_estreno = dias_estreno;
    }

    /**
     * Obtiene el número de espectadores durante la proyección.
     * 
     * @return El número de espectadores durante la proyección.
     */
    public int getEspectadores() {
        return espectadores;
    }

    /**
     * Establece el número de espectadores durante la proyección.
     * 
     * @param espectadores El número de espectadores durante la proyección.
     */
    public void setEspectadores(int espectadores) {
        this.espectadores = espectadores;
    }

    /**
     * Obtiene la recaudación obtenida durante la proyección.
     * 
     * @return La recaudación obtenida durante la proyección.
     */
    public int getRecaudacion() {
        return recaudacion;
    }

    /**
     * Establece la recaudación obtenida durante la proyección.
     * 
     * @param recaudacion La recaudación obtenida durante la proyección.
     */
    public void setRecaudacion(int recaudacion) {
        this.recaudacion = recaudacion;
    }
}
