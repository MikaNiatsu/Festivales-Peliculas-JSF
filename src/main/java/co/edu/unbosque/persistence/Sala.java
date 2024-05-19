package co.edu.unbosque.persistence;

/**
 * La clase Sala representa una sala de cine con su respectivo cine, número de sala y aforo.
 */
public class Sala {
    private String cine;
    private int sala;
    private int aforo;

    /**
     * Constructor de la clase Sala.
     * 
     * @param cine  El nombre del cine al que pertenece la sala.
     * @param sala  El número de la sala.
     * @param aforo El aforo de la sala.
     */
    public Sala(String cine, int sala, int aforo) {
        this.cine = cine;
        this.sala = sala;
        this.aforo = aforo;
    }

    /**
     * Obtiene el nombre del cine al que pertenece la sala.
     * 
     * @return El nombre del cine.
     */
    public String getCine() {
        return cine;
    }

    /**
     * Establece el nombre del cine al que pertenece la sala.
     * 
     * @param cine El nombre del cine.
     */
    public void setCine(String cine) {
        this.cine = cine;
    }

    /**
     * Obtiene el número de la sala.
     * 
     * @return El número de la sala.
     */
    public int getSala() {
        return sala;
    }

    /**
     * Establece el número de la sala.
     * 
     * @param sala El número de la sala.
     */
    public void setSala(int sala) {
        this.sala = sala;
    }

    /**
     * Obtiene el aforo de la sala.
     * 
     * @return El aforo de la sala.
     */
    public int getAforo() {
        return aforo;
    }

    /**
     * Establece el aforo de la sala.
     * 
     * @param aforo El aforo de la sala.
     */
    public void setAforo(int aforo) {
        this.aforo = aforo;
    }
}
