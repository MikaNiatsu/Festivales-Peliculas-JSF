package co.edu.unbosque.persistence;

public class Festival_Premio {
    private String festival;
    private String premio;
    private String galardon;

    public Festival_Premio(String festival, String premio, String galardon) {
        this.festival = festival;
        this.premio = premio;
        this.galardon = galardon;
    }

    public String getFestival() {
        return festival;
    }

    public void setFestival(String festival) {
        this.festival = festival;
    }

    public String getPremio() {
        return premio;
    }

    public void setPremio(String premio) {
        this.premio = premio;
    }

    public String getGalardon() {
        return galardon;
    }

    public void setGalardon(String galardon) {
        this.galardon = galardon;
    }
}
