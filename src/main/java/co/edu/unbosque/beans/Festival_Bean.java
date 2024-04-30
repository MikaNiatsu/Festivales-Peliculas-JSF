package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Festival;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named("festival")
@ViewScoped
public class Festival_Bean implements Serializable {
    private List<Festival> festivales;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String nombre = "";
    private String fecha = "";
    private List<Festival> festivalesSeleccionados = new ArrayList<>();

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Festival>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_festivales();");
            festivales = gson.fromJson(json, type);
        } catch (Exception e) {
            festivales = null;
            e.printStackTrace();
        }
    }

    public List<Festival> getFestivales() {
        return festivales;
    }

    public void setFestivales(List<Festival> festivales) {
        this.festivales = festivales;
    }

    public boolean isEsEditable() {
        return esEditable;
    }

    public void setEsEditable(boolean esEditable) {
        this.esEditable = esEditable;
    }


    public List<Festival> getFestivalesSeleccionados() {
        return festivalesSeleccionados;
    }

    public void setFestivalesSeleccionados(List<Festival> festivalesSeleccionados) {
        this.festivalesSeleccionados = festivalesSeleccionados;
    }

    public boolean isEsEliminar() {
        return esEliminar;
    }

    public boolean isEsAccion() {
        return esAccion;
    }

    public void setEsAccion(boolean esAccion) {
        this.esAccion = esAccion;
    }

    public void setEsEliminar(boolean esEliminar) {
        this.esEliminar = esEliminar;
    }

    public void toggleSelected(Festival fes) {
        if (festivalesSeleccionados.contains(fes)) {
            festivalesSeleccionados.remove(fes);
        } else {
            festivalesSeleccionados.add(fes);
        }
    }

    public void crear() {
        try {
            if (nombre == null || nombre.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("nombre", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nombre inválido"));
                return;
            }
            if (nombre.length() > 39) {
                FacesContext.getCurrentInstance().addMessage("nombre", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nombre demasiado extenso  "));
                return;
            }
            if (fecha == null || fecha.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("fecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha inválida"));
                return;
            }
            LocalDate parsedDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Funciones_SQL.llamar_metodo(String.format("CALL crear_festival('%s', '%s');", nombre, fecha));
            nombre = "";
            fecha = "";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("fecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha inválida"));
            return;
        }

        System.out.println(nombre + " " + fecha);
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

    public void actualizar_festival(Festival fes) {
        try {
            if (fes.getFundacion() == null) {
                fes.setFundacion(obtener_festival_anterior(fes.getFestival()));
                FacesContext.getCurrentInstance().addMessage("fecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha inválida"));
                return;
            }
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_festival('%s','%s');", fes.getFestival(), fes.getFundacion()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public LocalDate obtener_festival_anterior(String festival) {
        for (Festival f : festivales) {
            if (f.getFestival().equals(festival)) {
                return f.getFundacion();
            }
        }
        return null;
    }

    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Festival f : festivalesSeleccionados) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_festival('%s');", f.getFestival()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        festivalesSeleccionados = new ArrayList<>();
    }

    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/index.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] generar_pdf(List<Festival> festivales) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfPTable table = new PdfPTable(2);
            header(table);
            for (Festival f : festivales) {
                agregar_rows(table, f);
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    public static void header(PdfPTable table) {
        String[] headers = {"Festival", "Fundación"};
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(BaseColor.WHITE);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new BaseColor(33, 150, 243)); // Color azul
            cell.setPadding(5);
            cell.setPhrase(new Phrase(header, font));
            table.addCell(cell);
        }
    }

    public static void agregar_rows(PdfPTable table, Festival f) {
        table.addCell(f.getFestival());
        table.addCell(f.getFundacion().toString());
    }

    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(festivales), "festivales.pdf");
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


}
