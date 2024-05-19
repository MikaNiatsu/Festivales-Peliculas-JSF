package co.edu.unbosque.persistence;

/**
 * La clase Trabajo representa un trabajo realizado por una persona, con su CIP, nombre y tarea asociada.
 */
public class Trabajo {
    private String cip;
    private String nombre_persona;
    private String tarea;

    /**
     * Constructor de la clase Trabajo.
     * 
     * @param cip             El CIP de la persona que realizó el trabajo.
     * @param nombre_persona  El nombre de la persona que realizó el trabajo.
     * @param tarea           La tarea realizada.
     */
    public Trabajo(String cip, String nombre_persona, String tarea) {
        this.cip = cip;
        this.nombre_persona = nombre_persona;
        this.tarea = tarea;
    }

    /**
     * Obtiene el CIP de la persona que realizó el trabajo.
     * 
     * @return El CIP de la persona.
     */
    public String getCip() {
        return cip;
    }

    /**
     * Establece el CIP de la persona que realizó el trabajo.
     * 
     * @param cip El CIP de la persona.
     */
    public void setCip(String cip) {
        this.cip = cip;
    }

    /**
     * Obtiene el nombre de la persona que realizó el trabajo.
     * 
     * @return El nombre de la persona.
     */
    public String getNombre_persona() {
        return nombre_persona;
    }

    /**
     * Establece el nombre de la persona que realizó el trabajo.
     * 
     * @param nombre_persona El nombre de la persona.
     */
    public void setNombre_persona(String nombre_persona) {
        this.nombre_persona = nombre_persona;
    }

    /**
     * Obtiene la tarea realizada.
     * 
     * @return La tarea realizada.
     */
    public String getTarea() {
        return tarea;
    }

    /**
     * Establece la tarea realizada.
     * 
     * @param tarea La tarea realizada.
     */
    public void setTarea(String tarea) {
        this.tarea = tarea;
    }
}
