package co.edu.unbosque.persistence;

import java.time.LocalDate;

/**
 * La clase Festival representa un festival con su nombre y fecha de fundación.
 */
public class Festival {
    private String festival;
    private LocalDate fundacion;

    /**
     * Constructor de la clase Festival.
     * 
     * @param festival  El nombre del festival.
     * @param fundacion La fecha de fundación del festival.
     */
    public Festival(String festival, LocalDate fundacion) {
        this.festival = festival;
        this.fundacion = fundacion;
    }

    /**
     * Obtiene el nombre del festival.
     * 
     * @return El nombre del festival.
     */
    public String getFestival() {
        return festival;
    }

    /**
     * Establece el nombre del festival.
     * 
     * @param festival El nombre del festival.
     */
    public void setFestival(String festival) {
        this.festival = festival;
    }

    /**
     * Obtiene la fecha de fundación del festival.
     * 
     * @return La fecha de fundación del festival.
     */
    public LocalDate getFundacion() {
        return fundacion;
    }

    /**
     * Establece la fecha de fundación del festival.
     * 
     * @param fundacion La fecha de fundación del festival.
     */
    public void setFundacion(LocalDate fundacion) {
        this.fundacion = fundacion;
    }
}
