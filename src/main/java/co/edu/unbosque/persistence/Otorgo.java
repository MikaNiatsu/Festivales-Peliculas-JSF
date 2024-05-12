package co.edu.unbosque.persistence;

public class Otorgo {

    private String festival;
    private String certamen;
    private String premio;
    private int cip;

    public Otorgo(String festival, String certamen, String premio, int cip) {
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

    public int getCip() {
        return cip;
    }

    public void setCip(int cip) {
        this.cip = cip;
    }
}
