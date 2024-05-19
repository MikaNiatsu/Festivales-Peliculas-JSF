package co.edu.unbosque.persistence;

/**
 * La clase Otorgo representa la relación entre un festival, un certamen, un premio y un participante.
 */
public class Otorgo {
    private String festival;
    private int certamen;
    private String premio;
    private String cip;

    /**
     * Constructor de la clase Otorgo.
     * 
     * @param festival El nombre del festival.
     * @param certamen El número de certamen.
     * @param premio El nombre del premio otorgado.
     * @param cip El CIP del participante al que se le otorga el premio.
     */
    public Otorgo(String festival, int certamen, String premio, String cip) {
        this.festival = festival;
        this.certamen = certamen;
        this.premio = premio;
        this.cip = cip;
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
     * Obtiene el nombre del premio otorgado.
     * 
     * @return El nombre del premio otorgado.
     */
    public String getPremio() {
        return premio;
    }

    /**
     * Establece el nombre del premio otorgado.
     * 
     * @param premio El nombre del premio otorgado.
     */
    public void setPremio(String premio) {
        this.premio = premio;
    }

    /**
     * Obtiene el CIP del participante al que se le otorga el premio.
     * 
     * @return El CIP del participante al que se le otorga el premio.
     */
    public String getCip() {
        return cip;
    }

    /**
     * Establece el CIP del participante al que se le otorga el premio.
     * 
     * @param cip El CIP del participante al que se le otorga el premio.
     */
    public void setCip(String cip) {
        this.cip = cip;
    }
}
