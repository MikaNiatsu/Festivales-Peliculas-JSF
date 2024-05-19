package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Pelicula;
import co.edu.unbosque.services.Descargador;
import co.edu.unbosque.services.Funciones_SQL;
import co.edu.unbosque.services.Posters;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Named("pelicula")
@ViewScoped
public class Pelicula_Bean implements Serializable {
    private List<Pelicula> peliculas;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String titulo_p;
    private int ano_produccion;
    private String titulo_s;
    private String nacionalidad;
    private int presupuesto;
    private int duracion;
    private List<String> cips = new ArrayList<>();
    private String url;
    private List<Pelicula> peliculasSeleccionadas = new ArrayList<>();

    /**
 * Obtiene la lista de CIPs de las películas.
 * @return La lista de CIPs.
 */
public List<String> getCips() {
    cips = new ArrayList<>();
    for (Pelicula p : peliculas) {
        cips.add(p.getCip());
    }
    return cips;
}

/**
 * Establece la lista de CIPs de las películas.
 * @param cips La lista de CIPs a establecer.
 */
public void setCips(List<String> cips) {
    this.cips = cips;
}

/**
 * Obtiene la URL.
 * @return La URL.
 */
public String getUrl() {
    return url;
}

/**
 * Establece la URL.
 * @param url La URL a establecer.
 */
public void setUrl(String url) {
    this.url = url;
}

/**
 * Obtiene el título primario.
 * @return El título primario.
 */
public String getTitulo_p() {
    return titulo_p;
}

/**
 * Establece el título primario.
 * @param titulo_p El título primario a establecer.
 */
public void setTitulo_p(String titulo_p) {
    this.titulo_p = titulo_p;
}

/**
 * Obtiene el año de producción.
 * @return El año de producción.
 */
public int getAno_produccion() {
    return ano_produccion;
}

/**
 * Establece el año de producción.
 * @param ano_produccion El año de producción a establecer.
 */
public void setAno_produccion(int ano_produccion) {
    this.ano_produccion = ano_produccion;
}

/**
 * Obtiene el título secundario.
 * @return El título secundario.
 */
public String getTitulo_s() {
    return titulo_s;
}

/**
 * Establece el título secundario.
 * @param titulo_s El título secundario a establecer.
 */
public void setTitulo_s(String titulo_s) {
    this.titulo_s = titulo_s;
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
 * Obtiene la duración.
 * @return La duración.
 */
public int getDuracion() {
    return duracion;
}

/**
 * Establece la duración.
 * @param duracion La duración a establecer.
 */
public void setDuracion(int duracion) {
    this.duracion = duracion;
}


    /**
 * Inicializa la lista de películas obteniéndola desde la base de datos.
 */
@PostConstruct
public void init() {
    Gson gson = new GsonBuilder().create();
    Type type = new TypeToken<List<Pelicula>>() {}.getType();
    try {
        String json = Funciones_SQL.llamar_metodo_json("CALL obtener_peliculas();");
        peliculas = gson.fromJson(json, type);
    } catch (Exception e) {
        peliculas = null;
        throw new RuntimeException(e);
    }
}

/**
 * Obtiene la lista de películas.
 * @return La lista de películas.
 */
public List<Pelicula> getPeliculas() {
    return peliculas;
}

/**
 * Establece la lista de películas.
 * @param peliculas La lista de películas a establecer.
 */
public void setPeliculas(List<Pelicula> peliculas) {
    this.peliculas = peliculas;
}

/**
 * Indica si la funcionalidad de edición está activa.
 * @return true si la funcionalidad de edición está activa, false de lo contrario.
 */
public boolean isEsEditable() {
    return esEditable;
}

/**
 * Establece si la funcionalidad de edición está activa.
 * @param esEditable true para activar la funcionalidad de edición, false de lo contrario.
 */
public void setEsEditable(boolean esEditable) {
    this.esEditable = esEditable;
}

/**
 * Indica si la funcionalidad de eliminación está activa.
 * @return true si la funcionalidad de eliminación está activa, false de lo contrario.
 */
public boolean isEsEliminar() {
    return esEliminar;
}

/**
 * Establece si la funcionalidad de eliminación está activa.
 * @param esEliminar true para activar la funcionalidad de eliminación, false de lo contrario.
 */
public void setEsEliminar(boolean esEliminar) {
    this.esEliminar = esEliminar;
}

/**
 * Indica si hay una acción en curso.
 * @return true si hay una acción en curso, false de lo contrario.
 */
public boolean isEsAccion() {
    return esAccion;
}

/**
 * Establece si hay una acción en curso.
 * @param esAccion true si hay una acción en curso, false de lo contrario.
 */
public void setEsAccion(boolean esAccion) {
    this.esAccion = esAccion;
}

/**
 * Obtiene el presupuesto.
 * @return El presupuesto.
 */
public int getPresupuesto() {
    return presupuesto;
}

/**
 * Establece el presupuesto.
 * @param presupuesto El presupuesto a establecer.
 */
public void setPresupuesto(int presupuesto) {
    this.presupuesto = presupuesto;
}


  /**
 * Obtiene la lista de películas seleccionadas.
 * 
 * @return La lista de películas seleccionadas.
 */
public List<Pelicula> getPeliculasSeleccionadas() {
    return peliculasSeleccionadas;
}

/**
 * Establece la lista de películas seleccionadas.
 * 
 * @param peliculasSeleccionadas La nueva lista de películas seleccionadas.
 */
public void setPeliculasSeleccionadas(List<Pelicula> peliculasSeleccionadas) {
    this.peliculasSeleccionadas = peliculasSeleccionadas;
}


/**
 * Crea una nueva película con la información proporcionada y la añade a la lista de películas.
 * Si hay algún error en la validación de los datos, se muestran mensajes de error.
 */
public void crear() {
    try {
        // Validar los campos de la película
        if (titulo_p == null || titulo_p.isEmpty() || titulo_p.length() > 44) {
            FacesContext.getCurrentInstance().addMessage("titulo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Título inválido"));
            return;
        }
        if (ano_produccion <= 0 || Integer.toString(ano_produccion).length() > 4) {
            FacesContext.getCurrentInstance().addMessage("ano_produccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Año inválido"));
            return;
        }
        if (titulo_s == null || titulo_s.isEmpty() || titulo_s.length() > 44) {
            FacesContext.getCurrentInstance().addMessage("titulo_s", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Título inválido"));
            return;
        }
        if (nacionalidad == null || nacionalidad.isEmpty() || nacionalidad.length() > 14) {
            FacesContext.getCurrentInstance().addMessage("nacionalidad", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nacionalidad inválida"));
            return;
        }
        if (presupuesto <= 0) {
            FacesContext.getCurrentInstance().addMessage("presupuesto", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Presupuesto inválido"));
            return;
        }
        if (duracion <= 0 || Integer.toString(duracion).length() > 3) {
            FacesContext.getCurrentInstance().addMessage("duracion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Duración inválida"));
            return;
        }
        for (Pelicula p : peliculas) {
            if (p.getTitulo_p().equals(titulo_p)) {
                FacesContext.getCurrentInstance().addMessage("titulo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Película ya existente"));
                return;
            }
        }
        
        // Obtener el nuevo ID de la película
        String cip = Integer.parseInt(Funciones_SQL.llamar_metodo_json("CALL id_alto_pelicula();")) + 1 + "";
        
        // Reemplazar comillas simples y dobles para evitar problemas con la base de datos
        titulo_p = titulo_p.replace("'", "''").replace("\"", "''");
        titulo_s = titulo_s.replace("'", "''").replace("\"", "''");
        nacionalidad = nacionalidad.replace("'", "''").replace("\"", "''");
        
        // Llamar al procedimiento almacenado para crear la película en la base de datos
        Funciones_SQL.llamar_metodo(String.format("CALL crear_pelicula('%s','%s', %d, '%s', '%s', %d, %d);", cip, titulo_p, ano_produccion, titulo_s, nacionalidad, presupuesto, duracion));
        
        // Agregar el póster de la película si se proporcionó una URL válida
        if (!url.isEmpty() && !url.equals(" "))
            Posters.agregar(cip, url);
        
        // Actualizar la página para reflejar los cambios
        refrescar_pagina();
    } catch (Exception e) {
        // En caso de error, lanzar una excepción RuntimeException para abortar la operación
        throw new RuntimeException(e);
    }
}


   /**
 * Obtiene la URL del póster de una película.
 *
 * @param id     El identificador único de la película.
 * @param value  El valor asociado al póster.
 * @return La URL del póster de la película.
 */
public String obtener_poster(String id, String value) {
    return Posters.obtener_poster(id, value);
}

/**
 * Alterna la selección de una película en la lista de películas seleccionadas.
 *
 * @param pel La película para alternar su selección.
 */
public void toggleSelected(Pelicula pel) {
    if (peliculasSeleccionadas.contains(pel)) {
        peliculasSeleccionadas.remove(pel);
    } else {
        peliculasSeleccionadas.add(pel);
    }
}

/**
 * Establece el estado de acción como eliminar.
 */
public void eliminar() {
    esAccion = true;
    esEliminar = true;
}

/**
 * Establece el estado de acción como editar.
 */
public void editar() {
    esAccion = true;
    esEditable = true;
}

/**
 * Establece el estado de acción como no editar.
 */
public void guardar_editado() {
    esAccion = false;
    esEditable = false;
}

/**
 * Obtiene el título de una película.
 *
 * @param pel La película de la que se obtiene el título.
 * @return El título de la película.
 */
public String titulo_pelicula(Pelicula pel) {
    return pel.getTitulo_p().replace("'", " ").replace("\"", " ");
}


    /**
 * Actualiza la información de una película en la base de datos.
 *
 * @param pel La película con la información actualizada.
 */
public void actualizar_pelicula(Pelicula pel) {
    try {
        if (pel.getTitulo_p() == null || pel.getTitulo_p().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("titulo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Titulo inválido"));
            return;
        }
        if (pel.getTitulo_p().length() > 44) {
            FacesContext.getCurrentInstance().addMessage("titulo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Titulo demasiado extenso"));
            return;
        }
        if (pel.getPresupuesto() == 0 || (pel.getPresupuesto() + "").length() > 10) {
            FacesContext.getCurrentInstance().addMessage("presupuesto", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Presupuesto inválido"));
            return;
        }
        if (pel.getDuracion() == 0 || (pel.getDuracion() + "").length() > 3) {
            FacesContext.getCurrentInstance().addMessage("duracion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Duración inválida"));
            return;
        }
        if (pel.getTitulo_s() == null || pel.getTitulo_s().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("titulo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Titulo inválido"));
            return;
        }
        if (pel.getTitulo_s().length() > 44) {
            FacesContext.getCurrentInstance().addMessage("titulo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Titulo demasiado extenso"));
            return;
        }
        if (pel.getNacionalidad() == null || pel.getNacionalidad().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("nacionalidad", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nacionalidad inválida"));
            return;
        }
        if (pel.getNacionalidad().length() > 14) {
            FacesContext.getCurrentInstance().addMessage("nacionalidad", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nacionalidad demasiado extensa"));
            return;
        }
        if (pel.getAno_produccion() == 0 || (pel.getAno_produccion() + "").length() != 4) {
            FacesContext.getCurrentInstance().addMessage("ano_produccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ano inválido"));
            return;
        }
        Funciones_SQL.llamar_metodo(String.format("CALL actualizar_pelicula('%s','%s', '%s', '%s', '%s', '%s', '%s');", pel.getCip(), pel.getTitulo_p(), pel.getAno_produccion() + "", pel.getTitulo_s(), pel.getNacionalidad(), pel.getPresupuesto() + "", pel.getDuracion() + ""));
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}


    /**
 * Confirma y ejecuta la eliminación de las películas seleccionadas.
 * Se eliminan las películas seleccionadas de la base de datos y se borran sus posters asociados.
 */
public void confirmar_eliminar() {
    esAccion = false;
    esEliminar = false;
    for (Pelicula p : peliculasSeleccionadas) {
        try {
            Posters.borrar(p.getCip());
            Funciones_SQL.llamar_metodo(String.format("CALL eliminar_pelicula('%s');", p.getCip()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    refrescar_pagina();
}

/**
 * Cancela la acción de eliminación.
 * Restaura los valores de esAccion y esEliminar a false y limpia la lista de películas seleccionadas.
 */
public void cancelar_eliminar() {
    esAccion = false;
    esEliminar = false;
    peliculasSeleccionadas = new ArrayList<>();
}

/**
 * Refresca la página actual redireccionando al usuario a la página de películas.
 */
public void refrescar_pagina() {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    try {
        externalContext.redirect(externalContext.getRequestContextPath() + "/pelicula.xhtml");
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}


   /**
 * Genera un archivo PDF con la información de las películas proporcionadas.
 * @param peliculas Lista de películas a incluir en el PDF.
 * @return Array de bytes del PDF generado.
 */
public byte[] generar_pdf(List<Pelicula> peliculas) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4.rotate());

    try {
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable table = new PdfPTable(7);
        header(table);
        if (peliculas == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
            return null;
        } else {
            for (Pelicula p : peliculas) {
                agregar_rows(table, p);
            }
        }

        document.add(table);
        document.close();
    } catch (DocumentException e) {
        throw new RuntimeException(e);
    }

    return outputStream.toByteArray();
}

/**
 * Agrega el encabezado a la tabla del PDF.
 * @param table Tabla a la que se agregará el encabezado.
 */
public void header(PdfPTable table) {
    String[] headers = {"CIP", "Titulo Original", "Ano de Producción", "Titulo Español", "Nacionalidad", "Presupuesto", "Duración"};
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
 * Agrega una fila con la información de una película a la tabla del PDF.
 * @param table Tabla a la que se agregará la fila.
 * @param p Película de la que se obtendrá la información.
 */
public void agregar_rows(PdfPTable table, Pelicula p) {
    table.addCell(p.getCip());
    table.addCell(p.getTitulo_p());
    table.addCell(p.getAno_produccion() + "");
    table.addCell(p.getTitulo_s());
    table.addCell(p.getNacionalidad());
    table.addCell(p.getPresupuesto() + "");
    table.addCell(p.getDuracion() + "");
}

/**
 * Descarga un archivo PDF con la información de las películas.
 */
public void descargar_pdf() {
    Descargador.descargar_pdf(generar_pdf(peliculas), "peliculas.pdf");
}

}
