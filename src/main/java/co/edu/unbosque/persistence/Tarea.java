package co.edu.unbosque.persistence;

/**
 * La clase Tarea representa una tarea con su descripción y sexo asociado.
 */
public class Tarea {
    private String tarea;
    private char sexo_tarea;

    /**
     * Constructor de la clase Tarea.
     * 
     * @param tarea       La descripción de la tarea.
     * @param sexo_tarea  El sexo asociado a la tarea.
     */
    public Tarea(String tarea, char sexo_tarea) {
        this.tarea = tarea;
        this.sexo_tarea = sexo_tarea;
    }

    /**
     * Obtiene la descripción de la tarea.
     * 
     * @return La descripción de la tarea.
     */
    public String getTarea() {
        return tarea;
    }

    /**
     * Establece la descripción de la tarea.
     * 
     * @param tarea La descripción de la tarea.
     */
    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    /**
     * Obtiene el sexo asociado a la tarea.
     * 
     * @return El sexo asociado a la tarea.
     */
    public char getSexo_tarea() {
        return sexo_tarea;
    }

    /**
     * Establece el sexo asociado a la tarea.
     * 
     * @param sexo_tarea El sexo asociado a la tarea.
     */
    public void setSexo_tarea(char sexo_tarea) {
        this.sexo_tarea = sexo_tarea;
    }
}
