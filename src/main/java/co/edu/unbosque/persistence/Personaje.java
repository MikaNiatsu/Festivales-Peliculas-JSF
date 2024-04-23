package co.edu.unbosque.persistence;

public class Personaje {
    private String nombre_persona;
    private String nacionalidad_persona;
    private char sexo_persona;

    public Personaje(String nombre_persona, String nacionalidad_persona, char sexo_persona) {
        this.nombre_persona = nombre_persona;
        this.nacionalidad_persona = nacionalidad_persona;
        this.sexo_persona = sexo_persona;
    }

    public String getNombre_persona() {
        return nombre_persona;
    }

    public void setNombre_persona(String nombre_persona) {
        this.nombre_persona = nombre_persona;
    }

    public String getNacionalidad_persona() {
        return nacionalidad_persona;
    }

    public void setNacionalidad_persona(String nacionalidad_persona) {
        this.nacionalidad_persona = nacionalidad_persona;
    }

    public char getSexo_persona() {
        return sexo_persona;
    }

    public void setSexo_persona(char sexo_persona) {
        this.sexo_persona = sexo_persona;
    }
}
