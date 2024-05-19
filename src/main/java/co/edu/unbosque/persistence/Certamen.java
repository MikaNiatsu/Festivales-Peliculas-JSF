package co.edu.unbosque.persistence;

public class Certamen {
    private String festival;
    private int certamen;
    private String organizador;

    /**
     * Constructor de la clase Certamen.
     * 
     * @param festival    El nombre del festival.
     * @param certamen    El número del certamen.
     * @param organizador El organizador del certamen.
     */
    public Certamen(String festival, int certamen, String organizador) {
        this.festival = festival;
        this.certamen = certamen;
        this.organizador = organizador;
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
     * Obtiene el número del certamen.
     * 
     * @return El número del certamen.
     */
    public int getCertamen() {
        return certamen;
    }

    /**
     * Establece el número del certamen.
     * 
     * @param certamen El número del certamen.
     */
    public void setCertamen(int certamen) {
        this.certamen = certamen;
    }

    /**
     * Obtiene el organizador del certamen.
     * 
     * @return El organizador del certamen.
     */
    public String getOrganizador() {
        return organizador;
    }

    /**
     * Establece el organizador del certamen.
     * 
     * @param organizador El organizador del certamen.
     */
    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }
}
