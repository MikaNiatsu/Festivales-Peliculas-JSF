package co.edu.unbosque.persistence;

public class Tarea {
    private String tarea;
    private char sexo_tarea;

    public Tarea(String tarea, char sexo_tarea) {
        this.tarea = tarea;
        this.sexo_tarea = sexo_tarea;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public char getSexo_tarea() {
        return sexo_tarea;
    }

    public void setSexo_tarea(char sexo_tarea) {
        this.sexo_tarea = sexo_tarea;
    }
}
