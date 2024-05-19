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

    /**
 * Inicializa la lista de tareas obteniendo los datos de la base de datos.
 * Utiliza Gson para convertir el JSON obtenido de la base de datos en objetos de tipo Tarea.
 * Cualquier excepción durante el proceso de obtención de datos se maneja imprimiendo la traza de la excepción y estableciendo la lista de tareas en null.
 */
@PostConstruct
public void init() {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    Type type = new TypeToken<List<Tarea>>() {}.getType();
    try {
        String json = Funciones_SQL.llamar_metodo_json("CALL obtener_tarea();");
        tareas = gson.fromJson(json, type);
    } catch (Exception e) {
        tareas = null;
        e.printStackTrace();
    }
}

/**
 * Devuelve una lista de nombres de tareas como cadenas de texto.
 * 
 * @return Una lista de nombres de tareas.
 */
public ArrayList<String> getTareas_st() {
    tareas_st = new ArrayList<>();
    for (Tarea t : tareas) {
        tareas_st.add(t.getTarea());
    }
    return tareas_st;
}

    /**
 * Establece la lista de nombres de tareas como cadenas de texto.
 *
 * @param tareas_st La nueva lista de nombres de tareas.
 */
public void setTareas_st(ArrayList<String> tareas_st) {
    this.tareas_st = tareas_st;
}

/**
 * Devuelve la lista de todas las tareas.
 *
 * @return La lista de todas las tareas.
 */
public List<Tarea> getTareas() {
    return tareas;
}

/**
 * Establece la lista de todas las tareas.
 *
 * @param tareas La nueva lista de todas las tareas.
 */
public void setTareas(List<Tarea> tareas) {
    this.tareas = tareas;
}

/**
 * Devuelve la lista de tareas seleccionadas.
 *
 * @return La lista de tareas seleccionadas.
 */
public List<Tarea> getTareas_seleccionadas() {
    return tareas_seleccionadas;
}

/**
 * Establece la lista de tareas seleccionadas.
 *
 * @param tareas_seleccionadas La nueva lista de tareas seleccionadas.
 */
public void setTareas_seleccionadas(List<Tarea> tareas_seleccionadas) {
    this.tareas_seleccionadas = tareas_seleccionadas;
}

/**
 * Indica si la información de las tareas es editable.
 *
 * @return true si es editable, false en caso contrario.
 */
public boolean isEsEditable() {
    return esEditable;
}

/**
 * Establece si la información de las tareas es editable.
 *
 * @param esEditable true si es editable, false en caso contrario.
 */
public void setEsEditable(boolean esEditable) {
    this.esEditable = esEditable;
}

/**
 * Indica si la acción actual es de eliminación.
 *
 * @return true si es de eliminación, false en caso contrario.
 */
public boolean isEsEliminar() {
    return esEliminar;
}

/**
 * Establece si la acción actual es de eliminación.
 *
 * @param esEliminar true si es de eliminación, false en caso contrario.
 */
public void setEsEliminar(boolean esEliminar) {
    this.esEliminar = esEliminar;
}

/**
 * Indica si hay alguna acción en curso.
 *
 * @return true si hay alguna acción en curso, false en caso contrario.
 */
public boolean isEsAccion() {
    return esAccion;
}

/**
 * Establece si hay alguna acción en curso.
 *
 * @param esAccion true si hay alguna acción en curso, false en caso contrario.
 */
public void setEsAccion(boolean esAccion) {
    this.esAccion = esAccion;
}

/**
 * Devuelve el nombre de la tarea.
 *
 * @return El nombre de la tarea.
 */
public String getTarea() {
    return tarea;
}

/**
 * Establece el nombre de la tarea.
 *
 * @param tarea El nuevo nombre de la tarea.
 */
public void setTarea(String tarea) {
    this.tarea = tarea;
}

/**
 * Devuelve el sexo de la tarea.
 *
 * @return El sexo de la tarea.
 */
public char getSexo_tarea() {
    return sexo_tarea;
}

/**
 * Establece el sexo de la tarea.
 *
 * @param sexo_tarea El nuevo sexo de la tarea.
 */
public void setSexo_tarea(char sexo_tarea) {
    this.sexo_tarea = sexo_tarea;
}

/**
 * Alterna la selección de una tarea. Si la tarea está seleccionada, la elimina de la lista;
 * de lo contrario, la agrega a la lista de tareas seleccionadas.
 *
 * @param tar La tarea para alternar la selección.
 */
public void toggleSelected(Tarea tar) {
    if (tareas_seleccionadas.contains(tar)) {
        tareas_seleccionadas.remove(tar);
    } else {
        tareas_seleccionadas.add(tar);
    }
}


    /**
 * Crea una nueva tarea.
 */
public void crear() {
    try {
        // Validación de la tarea
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
        // Llamada al método SQL para crear la tarea
        Funciones_SQL.llamar_metodo(String.format("CALL crear_tarea('%s', '%s');", tarea, sexo_tarea));
        tarea = "";
        sexo_tarea = ' ';
    } catch (Exception e) {
        e.printStackTrace();
    }
    refrescar_pagina();
}

/**
 * Indica que se va a eliminar una tarea.
 */
public void eliminar() {
    esAccion = true;
    esEliminar = true;
}

/**
 * Indica que se va a editar una tarea.
 */
public void editar() {
    esAccion = true;
    esEditable = true;
}

/**
 * Indica que se ha guardado la edición de una tarea.
 */
public void guardar_editado() {
    esAccion = false;
    esEditable = false;
}

/**
 * Actualiza la información de una tarea.
 *
 * @param tar La tarea a actualizar.
 */
public void actualizar(Tarea tar) {
    try {
        if (sexo_tarea == ' ') {
            FacesContext.getCurrentInstance().addMessage("sexo_tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Sexo inválido"));
            return;
        }
        // Llamada al método SQL para actualizar la tarea
        Funciones_SQL.llamar_metodo(String.format("CALL actualizar_tarea('%s','%s');", tar.getTarea(), tar.getSexo_tarea()));
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/**
 * Confirma la eliminación de las tareas seleccionadas.
 */
public void confirmar_eliminar() {
    esAccion = false;
    esEliminar = false;
    for (Tarea t : tareas_seleccionadas) {
        try {
            // Llamada al método SQL para eliminar la tarea
            Funciones_SQL.llamar_metodo(String.format("CALL eliminar_tarea('%s');", t.getTarea()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    refrescar_pagina();
}

/**
 * Cancela la acción de eliminación.
 */
public void cancelar_eliminar() {
    esAccion = false;
    esEliminar = false;
    tareas_seleccionadas = new ArrayList<>();
}

/**
 * Refresca la página actual.
 */
public void refrescar_pagina() {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    try {
        externalContext.redirect(externalContext.getRequestContextPath() + "/tarea.xhtml");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

/**
 * Genera un archivo PDF que contiene la lista de tareas.
 *
 * @param tareas La lista de tareas a incluir en el PDF.
 * @return Un arreglo de bytes que representa el PDF generado.
 */
public byte[] generar_pdf(List<Tarea> tareas) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4.rotate());

    try {
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable table = new PdfPTable(2);
        header(table);
        if (tareas == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
            return null;
        } else {
            for (Tarea t : tareas) {
                agregar_rows(table, t);
            }
        }
        document.add(table);
        document.close();
    } catch (DocumentException e) {
        e.printStackTrace();
    }

    return outputStream.toByteArray();
}

/**
 * Agrega el encabezado a una tabla PDF.
 *
 * @param table La tabla a la que se agregará el encabezado.
 */
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

/**
 * Agrega filas a una tabla PDF que contiene información de tareas.
 *
 * @param table La tabla a la que se agregarán las filas.
 * @param t     La tarea cuya información se agregará como fila.
 */
public void agregar_rows(PdfPTable table, Tarea t) {
    table.addCell(t.getTarea());
    table.addCell(t.getSexo_tarea() + "");
}

/**
 * Descarga un archivo PDF que contiene la lista de tareas.
 */
public void descargar_pdf() {
    Descargador.descargar_pdf(generar_pdf(tareas), "tareas.pdf");
}



}
