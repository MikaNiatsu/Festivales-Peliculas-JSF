package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Festival_Premio;
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

@Named("festival_premio")
@ViewScoped
public class Festival_Premio_Bean implements Serializable {

    private List<Festival_Premio> festival_premios_seleccionados = new ArrayList<>();
    private List<Festival_Premio> festival_premios;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String festival = "";
    private String premio = "";
    private String galardon = "";

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Festival_Premio>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_festival_premio();");
            festival_premios = gson.fromJson(json, type);
        } catch (Exception e) {
            festival_premios = null;
            e.printStackTrace();
        }
    }

    public void toggleSelected(Festival_Premio pre) {
        if (festival_premios_seleccionados.contains(pre)) {
            festival_premios_seleccionados.remove(pre);
        } else {
            festival_premios_seleccionados.add(pre);
        }
    }

    public void crear() {
        try {
            if (galardon.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Galardon inválido"));
                return;
            }
            if (galardon.length() > 50) {
                FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Galardon demasiado extenso"));
                return;
            }
            Funciones_SQL.llamar_metodo(String.format("CALL crear_festival_premio('%s', '%s', '%s');", festival, premio, galardon));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear Festival_Premio"));
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

    public void actualizar(Festival_Premio pre) {
        try {
            if (pre.getGalardon().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Galardon inválido"));
                return;
            }
            if (pre.getGalardon().length() > 50) {
                FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Galardon demasiado extenso"));
                return;
            }
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_festival_premio('%s','%s','%s');", pre.getFestival(), pre.getPremio(), pre.getGalardon()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al actualizar festival_premio"));
            e.printStackTrace();
        }

    }

    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Festival_Premio p : festival_premios_seleccionados) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_festival_premio('%s');", p.getFestival()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar festival_premio"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        festival_premios_seleccionados = new ArrayList<>();
    }

    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/festivalpremio.xhtml");
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
            for (Festival_Premio p : festival_premios) {
                agregar_rows(table, p);
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    public void header(PdfPTable table) {
        String[] headers = {"Festival", "Premio", "Galardon"};
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

    public void agregar_rows(PdfPTable table, Festival_Premio p) {
        table.addCell(p.getFestival());
        table.addCell(p.getPremio());
        table.addCell(p.getGalardon());
    }

    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "festivalpremio.pdf");
    }

    public List<Festival_Premio> getFestival_premios_seleccionados() {
        return festival_premios_seleccionados;
    }

    public void setFestival_premios_seleccionados(List<Festival_Premio> festival_premios_seleccionados) {
        this.festival_premios_seleccionados = festival_premios_seleccionados;
    }

    public List<Festival_Premio> getFestival_premios() {
        return festival_premios;
    }

    public void setFestival_premios(List<Festival_Premio> festival_premios) {
        this.festival_premios = festival_premios;
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
