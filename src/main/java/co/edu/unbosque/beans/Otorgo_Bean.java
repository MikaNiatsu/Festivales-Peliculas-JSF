package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Otorgo;
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

@Named("otorgo")
@ViewScoped
public class Otorgo_Bean implements Serializable {

    private List<Otorgo> otorgos_seleccionados = new ArrayList<>();
    private List<Otorgo> otorgos;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String festival = "";
    private int certamen = 0;
    private String premio = "";
    private String cip = "";

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Otorgo>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_otorgo();");
            otorgos = gson.fromJson(json, type);
        } catch (Exception e) {
            otorgos = null;
            e.printStackTrace();
        }
    }

    public void toggleSelected(Otorgo oto) {
        if (otorgos_seleccionados.contains(oto)) {
            otorgos_seleccionados.remove(oto);
        } else {
            otorgos_seleccionados.add(oto);
        }
    }

    public void crear() {
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL crear_otorgo('%s', '%s', '%s', '%s');", cip, festival, premio, certamen));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("otorgo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear Otorgo"));
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

    public void actualizar(Otorgo oto) {
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_otorgo('%s','%s','%s','%s');", oto.getFestival(), oto.getCip(), oto.getPremio(), oto.getCertamen()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("otorgo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al actualizar otorgo"));
            e.printStackTrace();
        }

    }

    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Otorgo o : otorgos_seleccionados) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_otorgo('%s');", o.getFestival()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("otorgo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar otorgo"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        otorgos_seleccionados = new ArrayList<>();
    }

    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/otorgo.xhtml");
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
            if (otorgos != null)
                for (Otorgo o : otorgos) {
                    agregar_rows(table, o);
                }
            else {
                FacesContext.getCurrentInstance().addMessage("otorgo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
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
        String[] headers = {"Festival", "Certamen", "Premio", "CIP"};
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

    public void agregar_rows(PdfPTable table, Otorgo o) {
        table.addCell(o.getFestival());
        table.addCell(o.getCertamen() + "");
        table.addCell(o.getPremio());
        table.addCell(o.getCip());
    }

    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "otorgos.pdf");
    }

    public List<Otorgo> getOtorgos_seleccionados() {
        return otorgos_seleccionados;
    }

    public void setOtorgos_seleccionados(List<Otorgo> otorgos_seleccionados) {
        this.otorgos_seleccionados = otorgos_seleccionados;
    }

    public List<Otorgo> getOtorgos() {
        return otorgos;
    }

    public void setOtorgos(List<Otorgo> otorgos) {
        this.otorgos = otorgos;
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
