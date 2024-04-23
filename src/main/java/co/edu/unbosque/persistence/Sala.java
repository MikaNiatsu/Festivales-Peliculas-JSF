package co.edu.unbosque.persistence;

public class Sala {
    private String cine;
    private int sala;
    private int aforo;

    public Sala(String cine, int sala, int aforo) {
        this.cine = cine;
        this.sala = sala;
        this.aforo = aforo;
    }

    public String getCine() {
        return cine;
    }

    public void setCine(String cine) {
        this.cine = cine;
    }

    public int getSala() {
        return sala;
    }

    public void setSala(int sala) {
        this.sala = sala;
    }

    public int getAforo() {
        return aforo;
    }

    public void setAforo(int aforo) {
        this.aforo = aforo;
    }
}
