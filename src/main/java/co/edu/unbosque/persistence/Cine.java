package co.edu.unbosque.persistence;

public class Cine {
    private String cine;
    private String ciudad_cine;
    private String direccion_cine;

    /**
     * Constructor de la clase Cine.
     *
     * @param cine           El nombre del cine.
     * @param ciudad_cine    La ciudad donde se encuentra el cine.
     * @param direccion_cine La dirección del cine.
     */
    public Cine(String cine, String ciudad_cine, String direccion_cine) {
        this.cine = cine;
        this.ciudad_cine = ciudad_cine;
        this.direccion_cine = direccion_cine;
    }

    /**
     * Obtiene el nombre del cine.
     *
     * @return El nombre del cine.
     */
    public String getCine() {
        return cine;
    }

    /**
     * Establece el nombre del cine.
     *
     * @param cine El nombre del cine.
     */
    public void setCine(String cine) {
        this.cine = cine;
    }

    /**
     * Obtiene la ciudad donde se encuentra el cine.
     *
     * @return La ciudad del cine.
     */
    public String getCiudad_cine() {
        return ciudad_cine;
    }

    /**
     * Establece la ciudad donde se encuentra el cine.
     *
     * @param ciudad_cine La ciudad del cine.
     */
    public void setCiudad_cine(String ciudad_cine) {
        this.ciudad_cine = ciudad_cine;
    }

    /**
     * Obtiene la dirección del cine.
     *
     * @return La dirección del cine.
     */
    public String getDireccion_cine() {
        return direccion_cine;
    }

    /**
     * Establece la dirección del cine.
     *
     * @param direccion_cine La dirección del cine.
     */
    public void setDireccion_cine(String direccion_cine) {
        this.direccion_cine = direccion_cine;
    }
}
