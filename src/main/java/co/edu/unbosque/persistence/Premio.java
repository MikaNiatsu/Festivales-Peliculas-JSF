package co.edu.unbosque.persistence;

/**
 * La clase Premio representa un premio asociado a una tarea.
 */
public class Premio {
    private String premio;
    private String tarea;

    /**
     * Constructor de la clase Premio.
     * 
     * @param tarea  La tarea asociada al premio.
     * @param premio El premio obtenido.
     */
    public Premio(String tarea, String premio) {
        this.premio = premio;
        this.tarea = tarea;
    }

    /**
     * Obtiene el premio.
     * 
     * @return El premio obtenido.
     */
    public String getPremio() {
        return premio;
    }

    /**
     * Establece el premio.
     * 
     * @param premio El premio obtenido.
     */
    public void setPremio(String premio) {
        this.premio = premio;
    }

    /**
     * Obtiene la tarea asociada al premio.
     * 
     * @return La tarea asociada al premio.
     */
    public String getTarea() {
        return tarea;
    }

    /**
     * Establece la tarea asociada al premio.
     * 
     * @param tarea La tarea asociada al premio.
     */
    public void setTarea(String tarea) {
        this.tarea = tarea;
    }
}
