package co.edu.unbosque.persistence;

public class Otorgo {

    private String festival;
    private int certamen;
    private String premio;
    private String cip;

    public Otorgo(String festival, int certamen, String premio, String cip) {
        this.festival = festival;
        this.certamen = certamen;
        this.premio = premio;
        this.cip = cip;
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

    public String getPremio() {
        return premio;
    }

    public void setPremio(String premio) {
        this.premio = premio;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }
}
