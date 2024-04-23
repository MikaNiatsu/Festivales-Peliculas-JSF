package co.edu.unbosque.persistence;

public class Cine {
    private String cine;
    private String ciudad_cine;
    private String direccion_cine;

    public Cine(String cine, String ciudad_cine, String direccion_cine) {
        this.cine = cine;
        this.ciudad_cine = ciudad_cine;
        this.direccion_cine = direccion_cine;
    }

    public String getCine() {
        return cine;
    }

    public void setCine(String cine) {
        this.cine = cine;
    }

    public String getCiudad_cine() {
        return ciudad_cine;
    }

    public void setCiudad_cine(String ciudad_cine) {
        this.ciudad_cine = ciudad_cine;
    }

    public String getDireccion_cine() {
        return direccion_cine;
    }

    public void setDireccion_cine(String direccion_cine) {
        this.direccion_cine = direccion_cine;
    }
}
