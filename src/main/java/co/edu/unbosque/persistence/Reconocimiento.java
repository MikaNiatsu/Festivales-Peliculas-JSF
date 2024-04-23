package co.edu.unbosque.persistence;

public class Reconocimiento {
    private String festival;
    private String certamen;
    private String premio;
    private String nombre_persona;

    public Reconocimiento(String festival, String certamen, String premio, String nombre_persona) {
        this.festival = festival;
        this.certamen = certamen;
        this.premio = premio;
        this.nombre_persona = nombre_persona;
    }

    public String getFestival() {
        return festival;
    }

    public void setFestival(String festival) {
        this.festival = festival;
    }

    public String getCertamen() {
        return certamen;
    }

    public void setCertamen(String certamen) {
        this.certamen = certamen;
    }

    public String getPremio() {
        return premio;
    }

    public void setPremio(String premio) {
        this.premio = premio;
    }

    public String getNombre_persona() {
        return nombre_persona;
    }

    public void setNombre_persona(String nombre_persona) {
        this.nombre_persona = nombre_persona;
    }
}
