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
    private List<String> festivales_st = new ArrayList<>();

   /**
 * Obtiene una lista de nombres de festivales en forma de cadena de texto.
 * Itera sobre la lista de festivales y añade los nombres de los festivales a la lista {@code festivales_st}.
 *
 * @return una lista de nombres de festivales.
 */
public List<String> getFestivales_st() {
    festivales_st = new ArrayList<>();
    for (Festival f : festivales) {
        festivales_st.add(f.getFestival());
    }
    return festivales_st;
}

/**
 * Establece la lista de nombres de festivales en forma de cadena de texto.
 *
 * @param festivales_st la lista de nombres de festivales a establecer.
 */
public void setFestivales_st(List<String> festivales_st) {
    this.festivales_st = festivales_st;
}

/**
 * Inicializa la lista de festivales cargando los datos desde la base de datos.
 * Utiliza la clase Gson para convertir el JSON obtenido de la base de datos en una lista de objetos Festival.
 * Llama al método {@code Funciones_SQL.llamar_metodo_json()} para obtener los datos de los festivales desde la base de datos.
 * Si ocurre algún error durante la obtención de los datos, establece la lista de festivales como null e imprime la excepción.
 */
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


    /**
 * Obtiene una lista de festivales.
 *
 * @return una lista de objetos Festival.
 */
public List<Festival> getFestivales() {
    return festivales;
}

/**
 * Establece la lista de festivales.
 *
 * @param festivales la lista de festivales a establecer.
 */
public void setFestivales(List<Festival> festivales) {
    this.festivales = festivales;
}

/**
 * Obtiene el estado de edición.
 *
 * @return true si la edición está habilitada, false de lo contrario.
 */
public boolean isEsEditable() {
    return esEditable;
}

/**
 * Establece el estado de edición.
 *
 * @param esEditable el estado de edición a establecer.
 */
public void setEsEditable(boolean esEditable) {
    this.esEditable = esEditable;
}

/**
 * Obtiene una lista de festivales seleccionados.
 *
 * @return una lista de objetos Festival seleccionados.
 */
public List<Festival> getFestivalesSeleccionados() {
    return festivalesSeleccionados;
}

/**
 * Establece la lista de festivales seleccionados.
 *
 * @param festivalesSeleccionados la lista de festivales seleccionados a establecer.
 */
public void setFestivalesSeleccionados(List<Festival> festivalesSeleccionados) {
    this.festivalesSeleccionados = festivalesSeleccionados;
}

/**
 * Obtiene el estado de eliminación.
 *
 * @return true si la eliminación está habilitada, false de lo contrario.
 */
public boolean isEsEliminar() {
    return esEliminar;
}

/**
 * Establece el estado de eliminación.
 *
 * @param esEliminar el estado de eliminación a establecer.
 */
public void setEsEliminar(boolean esEliminar) {
    this.esEliminar = esEliminar;
}

/**
 * Obtiene el estado de acción.
 *
 * @return true si hay una acción en curso, false de lo contrario.
 */
public boolean isEsAccion() {
    return esAccion;
}

/**
 * Establece el estado de acción.
 *
 * @param esAccion el estado de acción a establecer.
 */
public void setEsAccion(boolean esAccion) {
    this.esAccion = esAccion;
}


    /**
 * Alterna la selección de un festival en la lista de festivales seleccionados.
 *
 * @param fes el festival para el que se alternará la selección.
 */
public void toggleSelected(Festival fes) {
    if (festivalesSeleccionados.contains(fes)) {
        festivalesSeleccionados.remove(fes);
    } else {
        festivalesSeleccionados.add(fes);
    }
}

/**
 * Crea un nuevo festival con el nombre y la fecha especificados.
 *
 * @throws FacesMessage si ocurre un error durante la creación del festival.
 */
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
        if (festivales.contains(new Festival(nombre, LocalDate.parse(fecha)))) {
            FacesContext.getCurrentInstance().addMessage("fecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Festival ya existente"));
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
    refrescar_pagina();
}

    /**
 * Establece el estado de acción y eliminación en verdadero.
 */
public void eliminar() {
    esAccion = true;
    esEliminar = true;
}

/**
 * Establece el estado de acción y edición en verdadero.
 */
public void editar() {
    esAccion = true;
    esEditable = true;
}

/**
 * Establece el estado de acción y edición en falso.
 */
public void guardar_editado() {
    esAccion = false;
    esEditable = false;
}

/**
 * Actualiza la información del festival en la base de datos.
 *
 * @param fes el festival con la información actualizada.
 */
public void actualizar_festival(Festival fes) {
    try {
        if (fes.getFundacion() == null) {
            FacesContext.getCurrentInstance().addMessage("fecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha inválida"));
            return;
        }
        Funciones_SQL.llamar_metodo(String.format("CALL actualizar_festival('%s','%s');", fes.getFestival(), fes.getFundacion()));
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    /**
 * Confirma la eliminación de los festivales seleccionados, estableciendo el estado de acción y eliminación en falso, y realiza la eliminación en la base de datos.
 * También refresca la página para reflejar los cambios.
 */
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

/**
 * Cancela la eliminación de los festivales seleccionados, estableciendo el estado de acción y eliminación en falso, y vacía la lista de festivales seleccionados.
 */
public void cancelar_eliminar() {
    esAccion = false;
    esEliminar = false;
    festivalesSeleccionados = new ArrayList<>();
}

/**
 * Refresca la página actual redirigiendo a la página del festival.
 */
public void refrescar_pagina() {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    try {
        externalContext.redirect(externalContext.getRequestContextPath() + "/festival.xhtml");
    } catch (IOException e) {
        e.printStackTrace();
    }
}


   /**
 * Genera un archivo PDF que contiene la información de los festivales proporcionados.
 * 
 * @param festivales La lista de festivales cuya información se incluirá en el PDF.
 * @return Un array de bytes que representa el contenido del archivo PDF generado, o null si no hay festivales proporcionados.
 */
public byte[] generar_pdf(List<Festival> festivales) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4.rotate());

    try {
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable table = new PdfPTable(2);
        header(table);
        if (festivales == null) {
            FacesContext.getCurrentInstance().addMessage("festivales", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
            return null;
        } else {
            for (Festival f : festivales) {
                agregar_rows(table, f);
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
 * Agrega el encabezado a una tabla PDF con dos columnas: "Festival" y "Fundación".
 * 
 * @param table La tabla a la que se agregará el encabezado.
 */
public void header(PdfPTable table) {
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


    /**
 * Agrega una fila a la tabla PDF con la información del festival proporcionado.
 * 
 * @param table La tabla a la que se agregará la fila.
 * @param f El festival del cual se tomará la información para agregar a la fila.
 */
public void agregar_rows(PdfPTable table, Festival f) {
    table.addCell(f.getFestival());
    table.addCell(f.getFundacion().toString());
}

/**
 * Descarga un archivo PDF que contiene la información de los festivales actualmente mostrados.
 */
public void descargar_pdf() {
    Descargador.descargar_pdf(generar_pdf(festivales), "festivales.pdf");
}

/**
 * Obtiene la fecha del festival.
 * 
 * @return La fecha del festival.
 */
public String getFecha() {
    return fecha;
}

/**
 * Establece la fecha del festival.
 * 
 * @param fecha La fecha del festival a establecer.
 */
public void setFecha(String fecha) {
    this.fecha = fecha;
}

/**
 * Obtiene el nombre del festival.
 * 
 * @return El nombre del festival.
 */
public String getNombre() {
    return nombre;
}

/**
 * Establece el nombre del festival.
 * 
 * @param nombre El nombre del festival a establecer.
 */
public void setNombre(String nombre) {
    this.nombre = nombre;
}



}
