package co.edu.unbosque.persistence;

public class Pelicula {
    private int cip;
    private String titulo_p;
    private int ano_produccion;
    private String titulo_s;
    private String nacionalidad;
    private int presupuesto;
    private int duracion;

    public Pelicula(int cip, String titulo_p, int ano_produccion, String titulo_s, String nacionalidad, int presupuesto, int duracion) {
        this.cip = cip;
        this.titulo_p = titulo_p;
        this.ano_produccion = ano_produccion;
        this.titulo_s = titulo_s;
        this.nacionalidad = nacionalidad;
        this.presupuesto = presupuesto;
        this.duracion = duracion;
    }

    public int getCip() {
        return cip;
    }

    public void setCip(int cip) {
        this.cip = cip;
    }

    public String getTitulo_p() {
        return titulo_p;
    }

    public void setTitulo_p(String titulo_p) {
        this.titulo_p = titulo_p;
    }

    public int getAno_produccion() {
        return ano_produccion;
    }

    public void setAno_produccion(int ano_produccion) {
        this.ano_produccion = ano_produccion;
    }

    public String getTitulo_s() {
        return titulo_s;
    }

    public void setTitulo_s(String titulo_s) {
        this.titulo_s = titulo_s;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public int getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(int presupuesto) {
        this.presupuesto = presupuesto;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }
}
