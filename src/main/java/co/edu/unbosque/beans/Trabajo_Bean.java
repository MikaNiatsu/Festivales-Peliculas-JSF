package co.edu.unbosque.beans;


import co.edu.unbosque.persistence.Trabajo;
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

@Named("trabajo")
@ViewScoped
public class Trabajo_Bean implements Serializable {
    private List<Trabajo> trabajos;
    private List<Trabajo> trabajos_seleccionados = new ArrayList<>();
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String trabajo = "";
    private String cip = "";
    private String nombre = "";

    public String getTrabajo() {
        return trabajo;
    }

    public void setTrabajo(String trabajo) {
        this.trabajo = trabajo;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Trabajo>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_trabajo();");
            trabajos = gson.fromJson(json, type);

        } catch (Exception e) {
            trabajos = null;
            e.printStackTrace();
        }
    }

    public List<Trabajo> getTrabajos() {
        return trabajos;
    }

    public void setTrabajos(List<Trabajo> trabajos) {
        this.trabajos = trabajos;
    }

    public List<Trabajo> getTrabajos_seleccionados() {
        return trabajos_seleccionados;
    }

    public void setTrabajos_seleccionados(List<Trabajo> trabajos_seleccionados) {
        this.trabajos_seleccionados = trabajos_seleccionados;
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

    public void toggleSelected(Trabajo tra) {
        if (trabajos_seleccionados.contains(tra)) {
            trabajos_seleccionados.remove(tra);
        } else {
            trabajos_seleccionados.add(tra);
        }
    }

    public void crear() {
        try {
            for (Trabajo t : trabajos) {
                if (t.getCip().equals(cip)) {
                    FacesContext.getCurrentInstance().addMessage("tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "CIP ya existe"));
                    return;
                }
            }
            Funciones_SQL.llamar_metodo(String.format("CALL crear_trabajo('%s', '%s', '%s');", cip, nombre, trabajo));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Tarea inválida"));
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

    public void actualizar(Trabajo tra) {
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_trabajo('%s','%s', '%s');", tra.getCip(), tra.getNombre_persona(), tra.getTarea()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Trabajo t : trabajos_seleccionados) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_trabajo('%s');", t.getCip()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Tarea inválida"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        trabajos_seleccionados = new ArrayList<>();
    }

    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/trabajo.xhtml");
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
            for (Trabajo t : trabajos) {
                agregar_rows(table, t);
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    public void agregar_rows(PdfPTable table, Trabajo t) {
        table.addCell(t.getCip());
        table.addCell(t.getTarea());
        table.addCell(t.getNombre_persona());
    }

    public void header(PdfPTable table) {
        String[] headers = {"CIP", "Tarea", "Persona"};
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

    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "trabajos.pdf");
    }
}