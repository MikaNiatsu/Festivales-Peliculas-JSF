package co.edu.unbosque.persistence;

public class Trabajo {
    private int cip;
    private String nombre_persona;
    private String tarea;

    public Trabajo(int cip, String nombre_persona, String tarea) {
        this.cip = cip;
        this.nombre_persona = nombre_persona;
        this.tarea = tarea;
    }

    public int getCip() {
        return cip;
    }

    public void setCip(int cip) {
        this.cip = cip;
    }

    public String getNombre_persona() {
        return nombre_persona;
    }

    public void setNombre_persona(String nombre_persona) {
        this.nombre_persona = nombre_persona;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }
}
