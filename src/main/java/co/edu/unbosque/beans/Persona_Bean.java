package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Personaje;
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

@Named("personaje")
@ViewScoped
public class Persona_Bean implements Serializable {
    private List<Personaje> personajes;
    private List<Personaje> personajes_seleccionadas = new ArrayList<>();
    private ArrayList<String> nombres = new ArrayList<>();
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String nacionalidad = "";
    private char sexo;
    private String nombre = "";

    /**
 * Inicializa la lista de personajes utilizando datos obtenidos de la base de datos.
 */
@PostConstruct
public void init() {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    Type type = new TypeToken<List<Personaje>>() {
    }.getType();
    try {
        String json = Funciones_SQL.llamar_metodo_json("CALL obtener_personaje();");
        personajes = gson.fromJson(json, type);

    } catch (Exception e) {
        personajes = null;
        e.printStackTrace();
    }
}

/**
 * Obtiene el nombre del personaje.
 * @return El nombre del personaje.
 */
public String getNombre() {
    return nombre;
}

/**
 * Establece el nombre del personaje.
 * @param nombre El nombre del personaje a establecer.
 */
public void setNombre(String nombre) {
    this.nombre = nombre;
}

/**
 * Verifica si se está en modo de edición.
 * @return true si se está en modo de edición, false de lo contrario.
 */
public boolean isEsEditable() {
    return esEditable;
}

/**
 * Establece el modo de edición.
 * @param esEditable true para activar el modo de edición, false de lo contrario.
 */
public void setEsEditable(boolean esEditable) {
    this.esEditable = esEditable;
}

/**
 * Verifica si se está en modo de eliminación.
 * @return true si se está en modo de eliminación, false de lo contrario.
 */
public boolean isEsEliminar() {
    return esEliminar;
}

/**
 * Establece el modo de eliminación.
 * @param esEliminar true para activar el modo de eliminación, false de lo contrario.
 */
public void setEsEliminar(boolean esEliminar) {
    this.esEliminar = esEliminar;
}

/**
 * Verifica si se está en modo de acción.
 * @return true si se está en modo de acción, false de lo contrario.
 */
public boolean isEsAccion() {
    return esAccion;
}

/**
 * Establece el modo de acción.
 * @param esAccion true para activar el modo de acción, false de lo contrario.
 */
public void setEsAccion(boolean esAccion) {
    this.esAccion = esAccion;
}


    /**
 * Alterna la selección del personaje.
 * Si el personaje ya está seleccionado, lo elimina de la lista de selección.
 * Si el personaje no está seleccionado, lo agrega a la lista de selección.
 * @param per El personaje a seleccionar o deseleccionar.
 */
public void toggleSelected(Personaje per) {
    if (personajes_seleccionadas.contains(per)) {
        personajes_seleccionadas.remove(per);
    } else {
        personajes_seleccionadas.add(per);
    }
}

/**
 * Crea un nuevo personaje con los datos proporcionados.
 * Se asegura de que los datos proporcionados sean válidos antes de crear el personaje.
 * Si algún dato es inválido, se muestra un mensaje de error.
 */
public void crear() {
    try {
        if (nacionalidad.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nacionalidad inválida"));
            return;
        }
        if (nombre.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nombre inválido"));
            return;
        }
        if (nacionalidad.length() > 15) {
            FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nacionalidad muy grande"));
            return;
        }
        if (nombre.length() > 15) {
            FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nombre muy grande"));
            return;
        }
        Funciones_SQL.llamar_metodo(String.format("CALL crear_personaje('%s', '%s', '%s');", nombre, nacionalidad, sexo));
    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Personaje inválido"));
        e.printStackTrace();
    }
    refrescar_pagina();
}


    /**
 * Activa la acción de eliminar.
 */
public void eliminar() {
    esAccion = true;
    esEliminar = true;
}

/**
 * Activa la acción de editar.
 */
public void editar() {
    esAccion = true;
    esEditable = true;
}

/**
 * Desactiva la acción de edición.
 */
public void guardar_editado() {
    esAccion = false;
    esEditable = false;
}

/**
 * Actualiza los datos del personaje proporcionado.
 * Se asegura de que los datos sean válidos antes de realizar la actualización.
 * Si algún dato es inválido, se muestra un mensaje de error.
 * @param per El personaje a actualizar.
 */
public void actualizar(Personaje per) {
    if (nacionalidad.isEmpty()) {
        FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nacionalidad inválida"));
        return;
    }
    if (nacionalidad.length() > 15) {
        FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nacionalidad muy grande"));
        return;
    }
    if (nombre.length() > 15) {
        FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nombre muy grande"));
        return;
    }
    try {
        Funciones_SQL.llamar_metodo(String.format("CALL actualizar_personaje('%s','%s', '%s');", per.getNombre_persona(), per.getNacionalidad_persona(), per.getSexo_persona()));
    } catch (SQLException e) {
        FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Personaje inválido"));
        e.printStackTrace();
    }
}

/**
 * Confirma la eliminación de los personajes seleccionados.
 * Desactiva la acción de eliminar una vez completada la operación.
 */
public void confirmar_eliminar() {
    esAccion = false;
    esEliminar = false;
    for (Personaje p : personajes_seleccionadas) {
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL eliminar_personaje('%s');", p.getNombre_persona()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Personaje inválido"));
            e.printStackTrace();
        }
    }
    refrescar_pagina();
}


    /**
 * Cancela la acción de eliminación y limpia la lista de personajes seleccionados.
 */
public void cancelar_eliminar() {
    esAccion = false;
    esEliminar = false;
    personajes_seleccionadas = new ArrayList<>();
}

/**
 * Refresca la página redirigiendo a la página de personas.
 */
public void refrescar_pagina() {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    try {
        externalContext.redirect(externalContext.getRequestContextPath() + "/persona.xhtml");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

/**
 * Genera un archivo PDF con la información de los personajes.
 * @return Un array de bytes que representa el archivo PDF generado.
 */
public byte[] generar_pdf() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4.rotate());

    try {
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable table = new PdfPTable(3);
        header(table);
        if (personajes == null) {
            FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
            return null;
        } else {
            for (Personaje p : personajes) {
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
 * Agrega una fila a la tabla PDF con los datos del personaje proporcionado.
 * @param table La tabla a la que se agregarán las filas.
 * @param p El personaje del que se extraerán los datos para la fila.
 */
public void agregar_rows(PdfPTable table, Personaje p) {
    table.addCell(p.getNombre_persona());
    table.addCell(p.getNacionalidad_persona());
    table.addCell(p.getSexo_persona() + "");
}

/**
 * Agrega un encabezado a la tabla PDF con los nombres de las columnas.
 * @param table La tabla a la que se agregará el encabezado.
 */
public void header(PdfPTable table) {
    String[] headers = {"Nombre", "Nacionalidad", "Sexo"};
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


    /**
 * Descarga un archivo PDF con la información de los personajes.
 */
public void descargar_pdf() {
    Descargador.descargar_pdf(generar_pdf(), "personajes.pdf");
}

/**
 * Obtiene la lista de personajes.
 * @return La lista de personajes.
 */
public List<Personaje> getPersonajes() {
    return personajes;
}

/**
 * Establece la lista de personajes.
 * @param personajes La lista de personajes a establecer.
 */
public void setPersonajes(List<Personaje> personajes) {
    this.personajes = personajes;
}

/**
 * Obtiene la lista de personajes seleccionados.
 * @return La lista de personajes seleccionados.
 */
public List<Personaje> getPersonajes_seleccionadas() {
    return personajes_seleccionadas;
}

/**
 * Establece la lista de personajes seleccionados.
 * @param personajes_seleccionadas La lista de personajes seleccionados a establecer.
 */
public void setPersonajes_seleccionadas(List<Personaje> personajes_seleccionadas) {
    this.personajes_seleccionadas = personajes_seleccionadas;
}

/**
 * Obtiene la nacionalidad.
 * @return La nacionalidad.
 */
public String getNacionalidad() {
    return nacionalidad;
}

/**
 * Establece la nacionalidad.
 * @param nacionalidad La nacionalidad a establecer.
 */
public void setNacionalidad(String nacionalidad) {
    this.nacionalidad = nacionalidad;
}

/**
 * Obtiene el sexo.
 * @return El sexo.
 */
public char getSexo() {
    return sexo;
}

/**
 * Establece el sexo.
 * @param sexo El sexo a establecer.
 */
public void setSexo(char sexo) {
    this.sexo = sexo;
}

/**
 * Obtiene una lista de nombres de los personajes.
 * @return La lista de nombres de los personajes.
 */
public ArrayList<String> getNombres() {
    nombres = new ArrayList<>();
    for (Personaje p : personajes) {
        nombres.add(p.getNombre_persona());
    }
    return nombres;
}

/**
 * Establece la lista de nombres de los personajes.
 * @param nombres La lista de nombres de los personajes a establecer.
 */
public void setNombres(ArrayList<String> nombres) {
    this.nombres = nombres;
}

}
