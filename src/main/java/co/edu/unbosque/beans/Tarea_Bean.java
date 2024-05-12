package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Tarea;
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

@Named("tarea")
@ViewScoped
public class Tarea_Bean implements Serializable {
    private List<Tarea> tareas;
    private List<Tarea> tareas_seleccionadas = new ArrayList<>();
    private ArrayList<String> tareas_st = new ArrayList<>();
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String tarea = "";
    private char sexo_tarea;

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Tarea>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_tarea();");
            tareas = gson.fromJson(json, type);
        } catch (Exception e) {
            tareas = null;
            e.printStackTrace();
        }
    }

    public ArrayList<String> getTareas_st() {
        tareas_st = new ArrayList<>();
        for (Tarea t : tareas) {
            tareas_st.add(t.getTarea());
        }
        return tareas_st;
    }

    public void setTareas_st(ArrayList<String> tareas_st) {
        this.tareas_st = tareas_st;
    }

    public List<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(List<Tarea> tareas) {
        this.tareas = tareas;
    }

    public List<Tarea> getTareas_seleccionadas() {
        return tareas_seleccionadas;
    }

    public void setTareas_seleccionadas(List<Tarea> tareas_seleccionadas) {
        this.tareas_seleccionadas = tareas_seleccionadas;
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

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public char getSexo_tarea() {
        return sexo_tarea;
    }

    public void setSexo_tarea(char sexo_tarea) {
        this.sexo_tarea = sexo_tarea;
    }

    public void toggleSelected(Tarea tar) {
        if (tareas_seleccionadas.contains(tar)) {
            tareas_seleccionadas.remove(tar);
        } else {
            tareas_seleccionadas.add(tar);
        }
    }

    public void crear() {
        try {
            if (tarea == null || tarea.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Tarea inválida"));
                return;
            }
            if (tarea.length() > 29) {
                FacesContext.getCurrentInstance().addMessage("tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Tarea demasiado extensa"));
                return;
            }
            if (tareas.contains(new Tarea(tarea, sexo_tarea))) {
                FacesContext.getCurrentInstance().addMessage("tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Tarea ya existe"));
                return;
            }
            if (sexo_tarea == ' ') {
                FacesContext.getCurrentInstance().addMessage("sexo_tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Sexo inválido"));
                return;
            }
            Funciones_SQL.llamar_metodo(String.format("CALL crear_tarea('%s', '%s');", tarea, sexo_tarea));
            tarea = "";
            sexo_tarea = ' ';
        } catch (Exception e) {
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

    public void actualizar(Tarea tar) {
        try {
            if (sexo_tarea == ' ') {
                FacesContext.getCurrentInstance().addMessage("sexo_tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Sexo inválido"));
                return;
            }
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_tarea('%s','%s');", tar.getTarea(), tar.getSexo_tarea()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Tarea t : tareas_seleccionadas) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_tarea('%s');", t.getTarea()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        tareas_seleccionadas = new ArrayList<>();
    }

    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/tarea.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] generar_pdf(List<Tarea> tareas) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfPTable table = new PdfPTable(2);
            header(table);
            for (Tarea t : tareas) {
                agregar_rows(table, t);
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    public void header(PdfPTable table) {
        String[] headers = {"Tarea", "Sexo"};
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(BaseColor.WHITE);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new BaseColor(33, 150, 243)); // Color azul
            cell.setPadding(2);
            cell.setPhrase(new Phrase(header, font));
            table.addCell(cell);
        }
    }

    public void agregar_rows(PdfPTable table, Tarea t) {
        table.addCell(t.getTarea());
        table.addCell(t.getSexo_tarea() + "");
    }

    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(tareas), "tareas.pdf");
    }


}
