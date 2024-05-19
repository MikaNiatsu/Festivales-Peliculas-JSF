package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Premio;
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

@Named("premio")
@ViewScoped
public class Premio_Bean implements Serializable {
    private List<Premio> premios_seleccionados = new ArrayList<>();
    private List<String> premios_st = new ArrayList<>();
    private List<Premio> premios;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String tarea = "";
    private String premio = "";

   /**
 * Inicializa la lista de premios utilizando datos obtenidos de la base de datos.
 */
@PostConstruct
public void init() {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    Type type = new TypeToken<List<Premio>>() {}.getType();
    try {
        String json = Funciones_SQL.llamar_metodo_json("CALL obtener_premio();");
        premios = gson.fromJson(json, type);
    } catch (Exception e) {
        premios = null;
        e.printStackTrace();
    }
}

/**
 * Alterna la selección de un premio en la lista de premios seleccionados.
 * @param pre El premio a seleccionar o deseleccionar.
 */
public void toggleSelected(Premio pre) {
    if (premios_seleccionados.contains(pre)) {
        premios_seleccionados.remove(pre);
    } else {
        premios_seleccionados.add(pre);
    }
}

/**
 * Crea un nuevo premio con el nombre y la tarea especificados.
 */
public void crear() {
    try {
        if (premio.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Premio inválido"));
            return;
        }
        if (premio.length() > 50) {
            FacesContext.getCurrentInstance().addMessage("premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Premio demasiado extenso"));
            return;
        }
        Funciones_SQL.llamar_metodo(String.format("CALL crear_premio('%s', '%s');", premio, tarea));
    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage("premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear premio"));
        e.printStackTrace();
    }
    refrescar_pagina();
}


    /**
 * Establece las banderas de acción y eliminación como verdaderas.
 */
public void eliminar() {
    esAccion = true;
    esEliminar = true;
}

/**
 * Establece las banderas de acción y edición como verdaderas.
 */
public void editar() {
    esAccion = true;
    esEditable = true;
}

/**
 * Establece las banderas de acción y edición como falsas.
 */
public void guardar_editado() {
    esAccion = false;
    esEditable = false;
}

/**
 * Actualiza la información del premio especificado en la base de datos.
 * @param pre El premio cuya información se actualizará.
 */
public void actualizar(Premio pre) {
    try {
        if (pre.getPremio().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Premio inválido"));
            return;
        }
        if (pre.getPremio().length() > 50) {
            FacesContext.getCurrentInstance().addMessage("premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Premio demasiado extenso"));
            return;
        }
        Funciones_SQL.llamar_metodo(String.format("CALL actualizar_premio('%s','%s');", pre.getPremio(), pre.getTarea()));
    } catch (SQLException e) {
        FacesContext.getCurrentInstance().addMessage("premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al actualizar premio"));
        e.printStackTrace();
    }
}

/**
 * Confirma la eliminación de los premios seleccionados, eliminándolos de la base de datos.
 */
public void confirmar_eliminar() {
    esAccion = false;
    esEliminar = false;
    for (Premio p : premios_seleccionados) {
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL eliminar_premio('%s');", p.getPremio()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar premio"));
            e.printStackTrace();
        }
    }
    refrescar_pagina();
}


    /**
 * Cancela la acción de eliminación, estableciendo las banderas correspondientes como falsas y vaciando la lista de premios seleccionados.
 */
public void cancelar_eliminar() {
    esAccion = false;
    esEliminar = false;
    premios_seleccionados = new ArrayList<>();
}

/**
 * Refresca la página redireccionando a la página de premios.
 */
public void refrescar_pagina() {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    try {
        externalContext.redirect(externalContext.getRequestContextPath() + "/premio.xhtml");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

/**
 * Genera un archivo PDF con la información de los premios.
 * @return Un array de bytes que representa el archivo PDF.
 */
public byte[] generar_pdf() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4.rotate());

    try {
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable table = new PdfPTable(2);
        header(table);
        if (premios == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
            return null;
        } else {
            for (Premio p : premios) {
                agregar_rows(table, p);
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
 * Agrega el encabezado a la tabla PDF.
 * @param table La tabla PDF a la que se agregará el encabezado.
 */
public void header(PdfPTable table) {
    String[] headers = {"Premio", "Tarea"};
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
 * Agrega las filas correspondientes a un premio en la tabla PDF.
 * @param table La tabla PDF a la que se agregarán las filas.
 * @param p El premio del cual se extraerán los datos.
 */
public void agregar_rows(PdfPTable table, Premio p) {
    table.addCell(p.getTarea());
    table.addCell(p.getPremio());
}

/**
 * Descarga el archivo PDF generado.
 */
public void descargar_pdf() {
    Descargador.descargar_pdf(generar_pdf(), "premios.pdf");
}

/**
 * Obtiene la lista de premios seleccionados.
 * @return La lista de premios seleccionados.
 */
public List<Premio> getPremios_seleccionados() {
    return premios_seleccionados;
}

/**
 * Obtiene la lista de premios.
 * @return La lista de premios.
 */
public List<Premio> getPremios() {
    return premios;
}

/**
 * Establece la lista de premios.
 * @param premios La lista de premios a establecer.
 */
public void setPremios(List<Premio> premios) {
    this.premios = premios;
}

/**
 * Obtiene una lista de los nombres de los premios.
 * @return Una lista de los nombres de los premios.
 */
public List<String> getPremios_st() {
    premios_st = new ArrayList<>();
    for (Premio p : premios) {
        premios_st.add(p.getPremio());
    }
    return premios_st;
}

/**
 * Indica si la acción actual es editable.
 * @return true si la acción actual es editable, false de lo contrario.
 */
public boolean isEsEditable() {
    return esEditable;
}

/**
 * Establece si la acción actual es editable.
 * @param esEditable true si la acción actual es editable, false de lo contrario.
 */
public void setEsEditable(boolean esEditable) {
    this.esEditable = esEditable;
}

/**
 * Indica si la acción actual es eliminar.
 * @return true si la acción actual es eliminar, false de lo contrario.
 */
public boolean isEsEliminar() {
    return esEliminar;
}

/**
 * Establece si la acción actual es eliminar.
 * @param esEliminar true si la acción actual es eliminar, false de lo contrario.
 */
public void setEsEliminar(boolean esEliminar) {
    this.esEliminar = esEliminar;
}

/**
 * Indica si la acción actual es una acción.
 * @return true si la acción actual es una acción, false de lo contrario.
 */
public boolean isEsAccion() {
    return esAccion;
}

/**
 * Establece si la acción actual es una acción.
 * @param esAccion true si la acción actual es una acción, false de lo contrario.
 */
public void setEsAccion(boolean esAccion) {
    this.esAccion = esAccion;
}

/**
 * Obtiene la tarea.
 * @return La tarea.
 */
public String getTarea() {
    return tarea;
}

/**
 * Establece la tarea.
 * @param tarea La tarea a establecer.
 */
public void setTarea(String tarea) {
    this.tarea = tarea;
}

/**
 * Obtiene el premio.
 * @return El premio.
 */
public String getPremio() {
    return premio;
}

/**
 * Establece el premio.
 * @param premio El premio a establecer.
 */
public void setPremio(String premio) {
    this.premio = premio;
}

}
