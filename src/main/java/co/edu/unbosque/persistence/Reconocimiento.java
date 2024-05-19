package co.edu.unbosque.persistence;

/**
 * La clase Reconocimiento representa un premio otorgado a una persona en un festival y certamen específicos.
 */
public class Reconocimiento {
    private String festival;
    private String certamen;
    private String premio;
    private String nombre_persona;

    /**
     * Constructor de la clase Reconocimiento.
     * 
     * @param festival       El nombre del festival.
     * @param certamen       El nombre del certamen.
     * @param premio         El premio otorgado.
     * @param nombre_persona El nombre de la persona premiada.
     */
    public Reconocimiento(String festival, String certamen, String premio, String nombre_persona) {
        this.festival = festival;
        this.certamen = certamen;
        this.premio = premio;
        this.nombre_persona = nombre_persona;
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
     * Obtiene el nombre del certamen.
     * 
     * @return El nombre del certamen.
     */
    public String getCertamen() {
        return certamen;
    }

    /**
     * Establece el nombre del certamen.
     * 
     * @param certamen El nombre del certamen.
     */
    public void setCertamen(String certamen) {
        this.certamen = certamen;
    }

    /**
     * Obtiene el premio otorgado.
     * 
     * @return El premio otorgado.
     */
    public String getPremio() {
        return premio;
    }

    /**
     * Establece el premio otorgado.
     * 
     * @param premio El premio otorgado.
     */
    public void setPremio(String premio) {
        this.premio = premio;
    }

    /**
     * Obtiene el nombre de la persona premiada.
     * 
     * @return El nombre de la persona premiada.
     */
    public String getNombre_persona() {
        return nombre_persona;
    }

    /**
     * Establece el nombre de la persona premiada.
     * 
     * @param nombre_persona El nombre de la persona premiada.
     */
    public void setNombre_persona(String nombre_persona) {
        this.nombre_persona = nombre_persona;
    }
}
