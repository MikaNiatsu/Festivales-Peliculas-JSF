package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Certamen;
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

@Named("certamen")
@ViewScoped
public class Certamen_Bean implements Serializable {
    private List<Certamen> certamenes;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String festival = "";
    private int certamen = 0;
    private String organizador = "";
    private List<Certamen> certamenesSeleccionados = new ArrayList<>();
    private List<String> festivales = new ArrayList<>();
    private List<String> ano_certamenes = new ArrayList<>();

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Certamen>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_certamen();");
            certamenes = gson.fromJson(json, type);
        } catch (Exception e) {
            certamenes = null;
            e.printStackTrace();
        }
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

    public void toggleSelected(Certamen cer) {
        if (certamenesSeleccionados.contains(cer)) {
            certamenesSeleccionados.remove(cer);
        } else {
            certamenesSeleccionados.add(cer);
        }
    }

    public void crear() {
        try {
            if (organizador.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Organizador inválido"));
                return;
            }
            if (organizador.length() > 60) {
                FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Organizador demasiado extenso"));
                return;
            }
            Funciones_SQL.llamar_metodo(String.format("CALL crear_certamen('%s', '%s', '%s');", festival, certamen, organizador));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Certamen inválida"));
            return;
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

    public void actualizar(Certamen cer) {
        try {
            if (cer.getOrganizador().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Organizador inválida"));
                return;
            }
            if (cer.getOrganizador().length() > 60) {
                FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Organizador demasiado extenso"));
                return;
            }
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_certamen('%s','%s', '%s');", festival, certamen, organizador));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Certamen c : certamenesSeleccionados) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_certamen('%s');", c.getCertamen()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Certamen inválido"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        certamenesSeleccionados = new ArrayList<>();
    }

    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/cetarmen.xhtml");
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

            if (certamenes != null) {
                for (Certamen c : certamenes) {
                    agregar_rows(table, c);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
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
        String[] headers = {"Festival", "Certamen", "Organizador"};
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

    public void agregar_rows(PdfPTable table, Certamen c) {
        table.addCell(c.getFestival());
        table.addCell(String.valueOf(c.getCertamen()));
        table.addCell(c.getOrganizador());
    }

    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "certamenes.pdf");
    }

    public List<Certamen> getCertamenesSeleccionados() {
        return certamenesSeleccionados;
    }

    public void setCertamenesSeleccionados(List<Certamen> certamenesSeleccionados) {
        this.certamenesSeleccionados = certamenesSeleccionados;
    }

    public String getOrganizador() {
        return organizador;
    }

    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    public int getCertamen() {
        return certamen;
    }

    public void setCertamen(int certamen) {
        this.certamen = certamen;
    }

    public String getFestival() {
        return festival;
    }

    public void setFestival(String festival) {
        this.festival = festival;
    }

    public List<Certamen> getCertamenes() {
        return certamenes;
    }

    public void setCertamenes(List<Certamen> certamenes) {
        this.certamenes = certamenes;
    }

    public List<String> getFestivales() {
        festivales = new ArrayList<>();
        for (Certamen c : certamenes) {
            festivales.add(c.getFestival());
        }
        return festivales;
    }

    public void setFestivales(List<String> festivales) {
        this.festivales = festivales;
    }

    public List<String> getAno_certamenes() {
        ano_certamenes = new ArrayList<>();
        for (Certamen c : certamenes) {
            ano_certamenes.add(String.valueOf(c.getCertamen()));
        }
        return ano_certamenes;
    }

    public void setAno_certamenes(List<String> ano_certamenes) {
        this.ano_certamenes = ano_certamenes;
    }
}
