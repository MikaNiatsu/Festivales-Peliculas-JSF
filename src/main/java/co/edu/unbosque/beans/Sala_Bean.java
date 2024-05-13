package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Sala;
import co.edu.unbosque.services.Descargador;
import co.edu.unbosque.services.Funciones_SQL;
import co.edu.unbosque.services.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Named("sala")
@ViewScoped
public class Sala_Bean implements Serializable {

    private List<Sala> salas_seleccionadas = new ArrayList<>();
    private List<Sala> salas;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String cine = "";
    private int sala = 0;
    private int aforo = 0;
    private List<String> salas_st = new ArrayList<>();
    private List<String> cines = new ArrayList<>();

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Sala>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_sala();");
            salas = gson.fromJson(json, type);
        } catch (Exception e) {
            salas = null;
            e.printStackTrace();
        }
    }

    public void toggleSelected(Sala sal) {
        if (salas_seleccionadas.contains(sal)) {
            salas_seleccionadas.remove(sal);
        } else {
            salas_seleccionadas.add(sal);
        }
    }

    public void crear() {
        if (sala < 1 || sala > 99) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Numero de sala no valido (entre 1 y 99)"));
            return;
        }
        if (aforo < 1 || aforo > 999) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Aforo no valido (entre 1 y 999)"));
            return;
        }
        for (Sala s : salas_seleccionadas) {
            if (s.getSala() == sala && s.getCine().equals(cine)) {
                FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una sala en ese cine con ese numero"));
                return;
            }
        }
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL crear_sala('%s', '%s', '%s');", cine, sala, aforo));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear sala"));
            e.printStackTrace();
        }
        refrescar_pagina();
    }

    public void eliminar() {
        esAccion = true;
        esEliminar = true;
    }

    public void editar() {
        esAccion = true;
        esEditable = true;
    }

    public void guardar_editado() {
        esAccion = false;
        esEditable = false;
    }

    public void actualizar(Sala sal) {
        if (sal.getSala() < 1 || sal.getSala() > 99) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Numero de sala no valido (entre 1 y 99)"));
            return;
        }
        if (sal.getAforo() < 1 || sal.getAforo() > 999) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Aforo no valido (entre 1 y 999)"));
            return;
        }

        try {
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_sala('%s','%s','%s');", sal.getCine(), sal.getSala(), sal.getAforo()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al actualizar sala"));
            e.printStackTrace();
        }

    }

    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Sala s : salas_seleccionadas) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_sala('%s');", s.getCine()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar sala"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        salas_seleccionadas = new ArrayList<>();
    }

    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/sala.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] generar_pdf() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            PdfPTable table = new PdfPTable(3);
            header(table);
            if (salas != null)
                for (Sala s : salas) {
                    agregar_rows(table, s);
                }
            else {
                FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
                return null;
            }
            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    public void header(PdfPTable table) {
        String[] headers = {"Cine", "Sala", "Aforo"};
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(BaseColor.WHITE);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new BaseColor(33, 150, 243)); // Color azul
            cell.setPadding(3);
            cell.setPhrase(new Phrase(header, font));
            table.addCell(cell);
        }
    }

    public void agregar_rows(PdfPTable table, Sala s) {
        table.addCell(s.getCine());
        table.addCell(String.valueOf(s.getSala()));
        table.addCell(String.valueOf(s.getAforo()));
    }

    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "salas.pdf");
    }

    public List<Sala> getSalas_seleccionadas() {
        return salas_seleccionadas;
    }

    public void setSalas_seleccionadas(List<Sala> salas_seleccionadas) {
        this.salas_seleccionadas = salas_seleccionadas;
    }

    public List<Sala> getSalas() {
        return salas;
    }

    public void setSalas(List<Sala> salas) {
        this.salas = salas;
    }

    public boolean isEsEditable() {
        return esEditable;
    }

    public void setEsEditable(boolean esEditable) {
        this.esEditable = esEditable;
    }

    public boolean isEsEliminar() {
        return esEliminar;
    }

    public void setEsEliminar(boolean esEliminar) {
        this.esEliminar = esEliminar;
    }

    public boolean isEsAccion() {
        return esAccion;
    }

    public void setEsAccion(boolean esAccion) {
        this.esAccion = esAccion;
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

    public List<String> getSalas_st() {
        salas_st = new ArrayList<>();
        for (Sala s : salas) {
            salas_st.add(String.valueOf(s.getSala()));
        }
        return salas_st;
    }

    public void setSalas_st(List<String> salas_st) {
        this.salas_st = salas_st;
    }

    public List<String> getCines() {
        cines = new ArrayList<>();
        for (Sala s : salas) {
            cines.add(s.getCine());
        }
        return cines;
    }

    public void setCines(List<String> cines) {
        this.cines = cines;
    }
}
