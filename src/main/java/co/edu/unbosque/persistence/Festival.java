package co.edu.unbosque.persistence;

import java.time.LocalDate;

public class Festival {
    private String festival;
    private LocalDate fundacion;

    public Festival(String festival, LocalDate fundacion) {
        this.festival = festival;
        this.fundacion = fundacion;
    }

    public String getFestival() {
        return festival;
    }

    public void setFestival(String festival) {
        this.festival = festival;
    }

    public LocalDate getFundacion() {
        return fundacion;
    }

    public void setFundacion(LocalDate fundacion) {
        this.fundacion = fundacion;
    }
}
