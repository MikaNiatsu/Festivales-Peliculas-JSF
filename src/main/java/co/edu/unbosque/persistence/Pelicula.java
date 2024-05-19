package co.edu.unbosque.persistence;

/**
 * La clase Pelicula representa una película con su información asociada.
 */
public class Pelicula {
    private String cip;
    private String titulo_p;
    private int ano_produccion;
    private String titulo_s;
    private String nacionalidad;
    private int presupuesto;
    private int duracion;

    /**
     * Constructor de la clase Pelicula.
     * 
     * @param cip             El CIP de la película.
     * @param titulo_p        El título principal de la película.
     * @param ano_produccion  El año de producción de la película.
     * @param titulo_s        El título secundario de la película.
     * @param nacionalidad    La nacionalidad de la película.
     * @param presupuesto     El presupuesto de la película.
     * @param duracion        La duración de la película en minutos.
     */
    public Pelicula(String cip, String titulo_p, int ano_produccion, String titulo_s, String nacionalidad, int presupuesto, int duracion) {
        this.cip = cip;
        this.titulo_p = titulo_p;
        this.ano_produccion = ano_produccion;
        this.titulo_s = titulo_s;
        this.nacionalidad = nacionalidad;
        this.presupuesto = presupuesto;
        this.duracion = duracion;
    }

    /**
     * Obtiene el CIP de la película.
     * 
     * @return El CIP de la película.
     */
    public String getCip() {
        return cip;
    }

    /**
     * Establece el CIP de la película.
     * 
     * @param cip El CIP de la película.
     */
    public void setCip(String cip) {
        this.cip = cip;
    }

    /**
     * Obtiene el título principal de la película.
     * 
     * @return El título principal de la película.
     */
    public String getTitulo_p() {
        return titulo_p;
    }

    /**
     * Establece el título principal de la película.
     * 
     * @param titulo_p El título principal de la película.
     */
    public void setTitulo_p(String titulo_p) {
        this.titulo_p = titulo_p;
    }

    /**
     * Obtiene el año de producción de la película.
     * 
     * @return El año de producción de la película.
     */
    public int getAno_produccion() {
        return ano_produccion;
    }

    /**
     * Establece el año de producción de la película.
     * 
     * @param ano_produccion El año de producción de la película.
     */
    public void setAno_produccion(int ano_produccion) {
        this.ano_produccion = ano_produccion;
    }

    /**
     * Obtiene el título secundario de la película.
     * 
     * @return El título secundario de la película.
     */
    public String getTitulo_s() {
        return titulo_s;
    }

    /**
     * Establece el título secundario de la película.
     * 
     * @param titulo_s El título secundario de la película.
     */
    public void setTitulo_s(String titulo_s) {
        this.titulo_s = titulo_s;
    }

    /**
     * Obtiene la nacionalidad de la película.
     * 
     * @return La nacionalidad de la película.
     */
    public String getNacionalidad() {
        return nacionalidad;
    }

    /**
     * Establece la nacionalidad de la película.
     * 
     * @param nacionalidad La nacionalidad de la película.
     */
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    /**
     * Obtiene el presupuesto de la película.
     * 
     * @return El presupuesto de la película.
     */
    public int getPresupuesto() {
        return presupuesto;
    }

    /**
     * Establece el presupuesto de la película.
     * 
     * @param presupuesto El presupuesto de la película.
     */
    public void setPresupuesto(int presupuesto) {
        this.presupuesto = presupuesto;
    }

    /**
     * Obtiene la duración de la película en minutos.
     * 
     * @return La duración de la película en minutos.
     */
    public int getDuracion() {
        return duracion;
    }

    /**
     * Establece la duración de la película en minutos.
     * 
     * @param duracion La duración de la película en minutos.
     */
    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }
}
