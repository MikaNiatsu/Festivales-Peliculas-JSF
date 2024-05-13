package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Reconocimiento;
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

@Named("reconocimiento")
@ViewScoped
public class Reconocimiento_Bean implements Serializable {

    private List<Reconocimiento> reconocimientos_seleccionadas = new ArrayList<>();
    private List<Reconocimiento> reconocimientos;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String festival = "";
    private int anio = 0;
    private String premio = "";
    private String nombre = "";

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Sala>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_reconocimiento();");
            reconocimientos = gson.fromJson(json, type);
        } catch (Exception e) {
            reconocimientos = null;
            e.printStackTrace();
        }
    }

    public void toggleSelected(Reconocimiento rec) {
        if (reconocimientos_seleccionadas.contains(rec)) {
            reconocimientos_seleccionadas.remove(rec);
        } else {
            reconocimientos_seleccionadas.add(rec);
        }
    }

    public void crear() {
        if (nombre.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre no puede estar vacio"));
            return;
        }
        if (nombre.length() > 35) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre no puede superar los 35 caracteres"));
            return;
        }
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL crear_reconocimiento('%s', '%s', '%s', '%s');", festival, anio, premio, nombre));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear reconocimiento"));
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

    public void actualizar(Reconocimiento rec) {
        if (rec.getNombre_persona().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre no puede estar vacio"));
            return;
        }
        if (rec.getNombre_persona().length() > 35) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre no puede superar los 35 caracteres"));
            return;
        }
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_reconocimiento('%s','%s','%s', '%s');", rec.getFestival(), rec.getCertamen(), rec.getPremio(), rec.getNombre_persona()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al actualizar reconocimiento"));
            e.printStackTrace();
        }

    }

    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Reconocimiento r : reconocimientos_seleccionadas) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_reconocimiento('%s');", r.getFestival()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar reconocimiento"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        reconocimientos_seleccionadas = new ArrayList<>();
    }

    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/reconocimiento.xhtml");
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
            PdfPTable table = new PdfPTable(4);
            header(table);
            if (reconocimientos != null)
                for (Reconocimiento r : reconocimientos) {
                    agregar_rows(table, r);
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
        String[] headers = {"Festival", "Certamen", "Premio", "Persona"};
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

    public void agregar_rows(PdfPTable table, Reconocimiento r) {
        table.addCell(r.getFestival());
        table.addCell(r.getCertamen());
        table.addCell(r.getPremio());
        table.addCell(r.getNombre_persona());
    }

    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "reconocimientos.pdf");
    }

    public List<Reconocimiento> getReconocimientos_seleccionadas() {
        return reconocimientos_seleccionadas;
    }

    public void setReconocimientos_seleccionadas(List<Reconocimiento> reconocimientos_seleccionadas) {
        this.reconocimientos_seleccionadas = reconocimientos_seleccionadas;
    }

    public List<Reconocimiento> getReconocimientos() {
        return reconocimientos;
    }

    public void setReconocimientos(List<Reconocimiento> reconocimientos) {
        this.reconocimientos = reconocimientos;
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

    public String getFestival() {
        return festival;
    }

    public void setFestival(String festival) {
        this.festival = festival;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getPremio() {
        return premio;
    }

    public void setPremio(String premio) {
        this.premio = premio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
