package co.edu.unbosque.persistence;

/**
 * La clase Festival_Premio representa la relación entre un festival, un premio y un galardón.
 */
public class Festival_Premio {
    private String festival;
    private String premio;
    private String galardon;

    /**
     * Constructor de la clase Festival_Premio.
     * 
     * @param festival El nombre del festival.
     * @param premio   El nombre del premio.
     * @param galardon El nombre del galardón.
     */
    public Festival_Premio(String festival, String premio, String galardon) {
        this.festival = festival;
        this.premio = premio;
        this.galardon = galardon;
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
     * Obtiene el nombre del premio.
     * 
     * @return El nombre del premio.
     */
    public String getPremio() {
        return premio;
    }

    /**
     * Establece el nombre del premio.
     * 
     * @param premio El nombre del premio.
     */
    public void setPremio(String premio) {
        this.premio = premio;
    }

    /**
     * Obtiene el nombre del galardón.
     * 
     * @return El nombre del galardón.
     */
    public String getGalardon() {
        return galardon;
    }

    /**
     * Establece el nombre del galardón.
     * 
     * @param galardon El nombre del galardón.
     */
    public void setGalardon(String galardon) {
        this.galardon = galardon;
    }
}
