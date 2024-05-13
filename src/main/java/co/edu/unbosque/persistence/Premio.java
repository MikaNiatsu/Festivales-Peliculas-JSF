package co.edu.unbosque.persistence;

public class Premio {
    private String premio;
    private String tarea;

    public Premio(String tarea, String premio) {
        this.premio = premio;
        this.tarea = tarea;
    }

    public String getPremio() {
        return premio;
    }

    public void setPremio(String premio) {
        this.premio = premio;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }
}
