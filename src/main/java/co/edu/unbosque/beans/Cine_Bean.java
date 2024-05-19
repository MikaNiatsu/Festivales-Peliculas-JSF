package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Cine;
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

@Named("cine")
@ViewScoped
public class Cine_Bean implements Serializable {
    private List<Cine> cines;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String cine = "";
    private String ciudad = "";
    private String direccion = "";
    private List<Cine> cinesSeleccionados = new ArrayList<>();
    private List<String> cines_st = new ArrayList<>();

    /**
 * Obtiene la lista de cines seleccionados.
 *
 * @return la lista de cines seleccionados.
 */
public List<Cine> getCinesSeleccionados() {
    return cinesSeleccionados;
}

/**
 * Establece la lista de cines seleccionados.
 *
 * @param cinesSeleccionados la lista de cines seleccionados a establecer.
 */
public void setCinesSeleccionados(List<Cine> cinesSeleccionados) {
    this.cinesSeleccionados = cinesSeleccionados;
}

/**
 * Obtiene la dirección del cine.
 *
 * @return la dirección del cine.
 */
public String getDireccion() {
    return direccion;
}

/**
 * Establece la dirección del cine.
 *
 * @param direccion la dirección del cine a establecer.
 */
public void setDireccion(String direccion) {
    this.direccion = direccion;
}

/**
 * Obtiene la ciudad del cine.
 *
 * @return la ciudad del cine.
 */
public String getCiudad() {
    return ciudad;
}

/**
 * Establece la ciudad del cine.
 *
 * @param ciudad la ciudad del cine a establecer.
 */
public void setCiudad(String ciudad) {
    this.ciudad = ciudad;
}

/**
 * Obtiene el nombre del cine.
 *
 * @return el nombre del cine.
 */
public String getCine() {
    return cine;
}

/**
 * Establece el nombre del cine.
 *
 * @param cine el nombre del cine a establecer.
 */
public void setCine(String cine) {
    this.cine = cine;
}

/**
 * Obtiene la lista de cines.
 *
 * @return la lista de cines.
 */
public List<Cine> getCines() {
    return cines;
}

/**
 * Establece la lista de cines.
 *
 * @param cines la lista de cines a establecer.
 */
public void setCines(List<Cine> cines) {
    this.cines = cines;
}


    /**
 * Inicializa la lista de cines utilizando datos obtenidos de la base de datos.
 * Utiliza el método {@code Funciones_SQL.llamar_metodo_json()} para obtener los datos en formato JSON
 * y luego los convierte a una lista de objetos {@code Cine} utilizando la librería Gson.
 * En caso de error, establece la lista de cines como nula e imprime la traza de la excepción.
 */
@PostConstruct
public void init() {
    // Crear un objeto Gson con un adaptador para LocalDate
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    // Obtener el tipo de lista de cines
    Type type = new TypeToken<List<Cine>>() {}.getType();
    try {
        // Obtener los datos de los cines en formato JSON
        String json = Funciones_SQL.llamar_metodo_json("CALL obtener_cine();");
        // Convertir el JSON a una lista de objetos Cine
        cines = gson.fromJson(json, type);
    } catch (Exception e) {
        // En caso de error, establecer la lista de cines como nula e imprimir la traza de la excepción
        cines = null;
        e.printStackTrace();
    }
}

/**
 * Verifica si el modo edición está activado.
 *
 * @return true si el modo edición está activado, false en caso contrario.
 */
public boolean isEsEditable() {
    return esEditable;
}

/**
 * Establece el modo edición.
 *
 * @param esEditable true para activar el modo edición, false para desactivarlo.
 */
public void setEsEditable(boolean esEditable) {
    this.esEditable = esEditable;
}

/**
 * Verifica si el modo eliminar está activado.
 *
 * @return true si el modo eliminar está activado, false en caso contrario.
 */
public boolean isEsEliminar() {
    return esEliminar;
}

/**
 * Establece el modo eliminar.
 *
 * @param esEliminar true para activar el modo eliminar, false para desactivarlo.
 */
public void setEsEliminar(boolean esEliminar) {
    this.esEliminar = esEliminar;
}

/**
 * Verifica si hay una acción en curso.
 *
 * @return true si hay una acción en curso, false en caso contrario.
 */
public boolean isEsAccion() {
    return esAccion;
}

/**
 * Establece si hay una acción en curso.
 *
 * @param esAccion true si hay una acción en curso, false en caso contrario.
 */
public void setEsAccion(boolean esAccion) {
    this.esAccion = esAccion;
}


    /**
 * Cambia el estado de selección del cine especificado.
 * Si el cine ya está seleccionado, lo elimina de la lista de cines seleccionados.
 * Si el cine no está seleccionado, lo agrega a la lista de cines seleccionados.
 *
 * @param cin el cine para cambiar su estado de selección.
 */
public void toggleSelected(Cine cin) {
    if (cinesSeleccionados.contains(cin)) {
        cinesSeleccionados.remove(cin);
    } else {
        cinesSeleccionados.add(cin);
    }
}


    /**
 * Crea un nuevo cine con la información proporcionada.
 * Verifica que los campos cine, ciudad y dirección no estén vacíos y no excedan la longitud máxima permitida.
 * Si algún campo no cumple con las condiciones, muestra un mensaje de error correspondiente.
 * Llama al método {@code Funciones_SQL.llamar_metodo()} para crear el cine en la base de datos.
 * En caso de error, muestra un mensaje de error.
 * Finalmente, refresca la página para mostrar los cambios.
 */
public void crear() {
    try {
        // Verificar que el nombre del cine no esté vacío y no exceda la longitud máxima
        if (cine.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cine inválido"));
            return;
        }
        if (cine.length() > 25) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cine demasiado extenso"));
            return;
        }
        // Verificar que el nombre de la ciudad no esté vacío y no exceda la longitud máxima
        if (ciudad.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ciudad inválida"));
            return;
        }
        if (ciudad.length() > 25) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ciudad demasiado extensa"));
            return;
        }
        // Verificar que la dirección no esté vacía y no exceda la longitud máxima
        if (direccion.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dirección inválida"));
            return;
        }
        if (direccion.length() > 60) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dirección demasiado extensa"));
            return;
        }
        // Llamar al método para crear el cine en la base de datos
        Funciones_SQL.llamar_metodo(String.format("CALL crear_cine('%s', '%s', '%s');", cine, ciudad, direccion));
    } catch (Exception e) {
        // En caso de error, mostrar un mensaje de error
        FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Certamen inválido"));
        return;
    }
    // Refrescar la página para mostrar los cambios
    refrescar_pagina();
}


    /**
 * Activa el modo de eliminación.
 * Establece las banderas {@code esAccion} y {@code esEliminar} como verdaderas para indicar que se está llevando a cabo una acción de eliminación.
 */
public void eliminar() {
    esAccion = true;
    esEliminar = true;
}

/**
 * Activa el modo de edición.
 * Establece las banderas {@code esAccion} y {@code esEditable} como verdaderas para indicar que se está llevando a cabo una acción de edición.
 */
public void editar() {
    esAccion = true;
    esEditable = true;
}

/**
 * Desactiva el modo de edición.
 * Establece las banderas {@code esAccion} y {@code esEditable} como falsas para indicar que la acción de edición ha sido completada.
 */
public void guardar_editado() {
    esAccion = false;
    esEditable = false;
}


    /**
 * Actualiza la información del cine especificado en la base de datos.
 * Verifica que los campos del cine no estén vacíos y no excedan la longitud máxima permitida.
 * Si algún campo no cumple con las condiciones, muestra un mensaje de error correspondiente.
 * Llama al método {@code Funciones_SQL.llamar_metodo()} para actualizar la información del cine en la base de datos.
 * En caso de error, muestra un mensaje de error y la traza de la excepción.
 *
 * @param cin el cine con la información actualizada.
 */
public void actualizar(Cine cin) {
    try {
        // Verificar que el nombre del cine no esté vacío y no exceda la longitud máxima
        if (cin.getCine().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cine inválido"));
            return;
        }
        if (cin.getCine().length() > 25) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cine demasiado extenso"));
            return;
        }
        // Verificar que el nombre de la ciudad no esté vacío y no exceda la longitud máxima
        if (cin.getCiudad_cine().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ciudad inválida"));
            return;
        }
        if (cin.getCiudad_cine().length() > 25) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ciudad demasiado extensa"));
            return;
        }
        // Verificar que la dirección no esté vacía y no exceda la longitud máxima
        if (cin.getDireccion_cine().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dirección inválida"));
            return;
        }
        if (cin.getDireccion_cine().length() > 60) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dirección demasiado extensa"));
            return;
        }
        // Llamar al método para actualizar la información del cine en la base de datos
        Funciones_SQL.llamar_metodo(String.format("CALL actualizar_cine('%s','%s', '%s');", cin.getCine(), cin.getCiudad_cine(), cin.getDireccion_cine()));

    } catch (SQLException e) {
        // En caso de error, mostrar un mensaje de error y la traza de la excepción
        FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cine inválida"));
        e.printStackTrace();
    }
}


    /**
 * Confirma y ejecuta la eliminación de los cines seleccionados.
 * Desactiva el modo de acción y el modo de eliminación.
 * Itera sobre la lista de cines seleccionados y llama al método {@code Funciones_SQL.llamar_metodo()} para eliminar cada uno de ellos de la base de datos.
 * En caso de error durante la eliminación de un cine, muestra un mensaje de error y la traza de la excepción.
 * Finalmente, refresca la página para mostrar los cambios.
 */
public void confirmar_eliminar() {
    esAccion = false;
    esEliminar = false;
    for (Cine c : cinesSeleccionados) {
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL eliminar_cine('%s');", c.getCine()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cine inválido"));
            e.printStackTrace();
        }
    }
    refrescar_pagina();
}

/**
 * Cancela la acción de eliminación de cines.
 * Desactiva el modo de acción y el modo de eliminación, y limpia la lista de cines seleccionados.
 */
public void cancelar_eliminar() {
    esAccion = false;
    esEliminar = false;
    cinesSeleccionados = new ArrayList<>();
}

/**
 * Refresca la página actualizando su contenido.
 * Obtiene el contexto externo de JSF y redirige a la página 'cine.xhtml'.
 * En caso de error durante la redirección, muestra la traza de la excepción.
 */
public void refrescar_pagina() {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    try {
        externalContext.redirect(externalContext.getRequestContextPath() + "/cine.xhtml");
    } catch (IOException e) {
        e.printStackTrace();
    }
}


   /**
 * Genera un archivo PDF con la información de los cines.
 * Crea un nuevo documento PDF con tamaño A4 en orientación horizontal.
 * Llama al método {@code header()} para agregar el encabezado de la tabla al PDF.
 * Verifica si la lista de cines está vacía. Si es así, muestra un mensaje de error y retorna null.
 * Si la lista de cines no está vacía, itera sobre ella y agrega las filas de datos a la tabla del PDF llamando al método {@code agregar_rows()}.
 * Finalmente, agrega la tabla al documento PDF, cierra el documento y retorna el contenido del PDF en forma de arreglo de bytes.
 *
 * @return un arreglo de bytes que representa el contenido del archivo PDF generado.
 */
public byte[] generar_pdf() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4.rotate());

    try {
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable table = new PdfPTable(3);
        header(table);
        if (cines == null) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
            return null;
        } else {
            for (Cine c : cines) {
                agregar_rows(table, c);
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
 * Agrega el encabezado de la tabla al documento PDF.
 * Crea celdas de encabezado para cada elemento del arreglo {@code headers} y las añade a la tabla {@code table}.
 * Configura el color y la fuente del texto del encabezado.
 *
 * @param table la tabla a la que se agregará el encabezado.
 */
public void header(PdfPTable table) {
    String[] headers = {"Cine", "Ciudad", "Dirección"};
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
 * Agrega una fila de datos al documento PDF.
 * Añade las celdas correspondientes al cine, ciudad y dirección del cine {@code c} a la tabla {@code table}.
 *
 * @param table la tabla a la que se agregará la fila de datos.
 * @param c     el objeto Cine del cual se extraerán los datos.
 */
public void agregar_rows(PdfPTable table, Cine c) {
    table.addCell(c.getCine());
    table.addCell(c.getCiudad_cine());
    table.addCell(c.getDireccion_cine());
}

/**
 * Descarga el archivo PDF generado con la información de los cines.
 * Utiliza el método {@code Descargador.descargar_pdf()} para descargar el archivo PDF generado por el método {@code generar_pdf()} con el nombre "cines.pdf".
 */
public void descargar_pdf() {
    Descargador.descargar_pdf(generar_pdf(), "cines.pdf");
}

/**
 * Obtiene una lista de nombres de cines en forma de cadena de texto.
 * Itera sobre la lista de cines y añade los nombres de los cines a la lista {@code cines_st}.
 *
 * @return una lista de nombres de cines.
 */
public List<String> getCines_st() {
    cines_st = new ArrayList<>();
    for (Cine c : cines) {
        cines_st.add(c.getCine());
    }
    return cines_st;
}

/**
 * Establece la lista de nombres de cines en forma de cadena de texto.
 *
 * @param cines_st la lista de nombres de cines a establecer.
 */
public void setCines_st(List<String> cines_st) {
    this.cines_st = cines_st;
}


}
