package co.edu.unbosque.persistence;

public class Certamen {
    private String festival;
    private int certamen;
    private String organizador;

    public Certamen(String festival, int certamen, String organizador) {
        this.festival = festival;
        this.certamen = certamen;
        this.organizador = organizador;
    }

    public String getFestival() {
        return festival;
    }

    public void setFestival(String festival) {
        this.festival = festival;
    }

    public int getCertamen() {
        return certamen;
    }

    public void setCertamen(int certamen) {
        this.certamen = certamen;
    }

    public String getOrganizador() {
        return organizador;
    }

    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }
}
