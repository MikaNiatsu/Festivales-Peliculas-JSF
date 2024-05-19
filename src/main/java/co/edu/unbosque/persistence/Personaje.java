package co.edu.unbosque.persistence;

/**
 * La clase Personaje representa un personaje de una película con su información asociada.
 */
public class Personaje {
    private String nombre_persona;
    private String nacionalidad_persona;
    private char sexo_persona;

    /**
     * Constructor de la clase Personaje.
     * 
     * @param nombre_persona       El nombre del personaje.
     * @param nacionalidad_persona La nacionalidad del personaje.
     * @param sexo_persona         El sexo del personaje ('M' para masculino, 'F' para femenino).
     */
    public Personaje(String nombre_persona, String nacionalidad_persona, char sexo_persona) {
        this.nombre_persona = nombre_persona;
        this.nacionalidad_persona = nacionalidad_persona;
        this.sexo_persona = sexo_persona;
    }

    /**
     * Obtiene el nombre del personaje.
     * 
     * @return El nombre del personaje.
     */
    public String getNombre_persona() {
        return nombre_persona;
    }

    /**
     * Establece el nombre del personaje.
     * 
     * @param nombre_persona El nombre del personaje.
     */
    public void setNombre_persona(String nombre_persona) {
        this.nombre_persona = nombre_persona;
    }

    /**
     * Obtiene la nacionalidad del personaje.
     * 
     * @return La nacionalidad del personaje.
     */
    public String getNacionalidad_persona() {
        return nacionalidad_persona;
    }

    /**
     * Establece la nacionalidad del personaje.
     * 
     * @param nacionalidad_persona La nacionalidad del personaje.
     */
    public void setNacionalidad_persona(String nacionalidad_persona) {
        this.nacionalidad_persona = nacionalidad_persona;
    }

    /**
     * Obtiene el sexo del personaje.
     * 
     * @return El sexo del personaje ('M' para masculino, 'F' para femenino).
     */
    public char getSexo_persona() {
        return sexo_persona;
    }

    /**
     * Establece el sexo del personaje.
     * 
     * @param sexo_persona El sexo del personaje ('M' para masculino, 'F' para femenino).
     */
    public void setSexo_persona(char sexo_persona) {
        this.sexo_persona = sexo_persona;
    }
}
