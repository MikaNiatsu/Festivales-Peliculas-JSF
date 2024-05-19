package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Festival_Premio;
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

@Named("festival_premio")
@ViewScoped
public class Festival_Premio_Bean implements Serializable {

    private List<Festival_Premio> festival_premios_seleccionados = new ArrayList<>();
    private List<Festival_Premio> festival_premios;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String festival = "";
    private String premio = "";
    private String galardon = "";

    /**
     * Inicializa la lista de festivales premiados obteniéndola de la base de datos al inicio de la carga de la página.
     * Utiliza Gson para convertir el JSON obtenido en una lista de objetos Festival_Premio.
     * Si ocurre algún error durante el proceso, se establece la lista de festivales premiados como nula y se imprime la traza de la excepción.
     */
    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Festival_Premio>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_festival_premio();");
            festival_premios = gson.fromJson(json, type);
        } catch (Exception e) {
            festival_premios = null;
            e.printStackTrace();
        }
    }

    /**
     * Alterna la selección del festival premiado especificado.
     * Si el festival premiado ya está seleccionado, lo quita de la lista de selección.
     * De lo contrario, lo añade a la lista de selección.
     *
     * @param pre El festival premiado que se va a alternar.
     */
    public void toggleSelected(Festival_Premio pre) {
        if (festival_premios_seleccionados.contains(pre)) {
            festival_premios_seleccionados.remove(pre);
        } else {
            festival_premios_seleccionados.add(pre);
        }
    }

    /**
     * Crea un nuevo festival premiado con los datos proporcionados.
     * Verifica que el galardón no esté vacío y no sea demasiado largo antes de realizar la llamada al método SQL para crear el festival premiado.
     * Si ocurre algún error durante el proceso, se muestra un mensaje de error en la página y se imprime la traza de la excepción.
     * Después de crear el festival premiado, se refresca la página.
     */
    public void crear() {
        try {
            if (galardon.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Galardón inválido"));
                return;
            }
            if (galardon.length() > 50) {
                FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Galardón demasiado extenso"));
                return;
            }
            Funciones_SQL.llamar_metodo(String.format("CALL crear_festival_premio('%s', '%s', '%s');", festival, premio, galardon));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear Festival_Premio"));
            e.printStackTrace();
        }
        refrescar_pagina();
    }


    /**
     * Establece los indicadores de acción y eliminación como verdaderos para habilitar la eliminación de festivales premiados.
     */
    public void eliminar() {
        esAccion = true;
        esEliminar = true;
    }

    /**
     * Establece los indicadores de acción y edición como verdaderos para habilitar la edición de festivales premiados.
     */
    public void editar() {
        esAccion = true;
        esEditable = true;
    }

    /**
     * Restablece los indicadores de acción y edición como falsos después de guardar los cambios editados.
     */
    public void guardar_editado() {
        esAccion = false;
        esEditable = false;
    }

    /**
     * Actualiza el festival premiado con los datos proporcionados.
     * Verifica que el galardón no esté vacío y no sea demasiado largo antes de realizar la llamada al método SQL para actualizar el festival premiado.
     * Si ocurre algún error durante el proceso, se muestra un mensaje de error en la página y se imprime la traza de la excepción.
     *
     * @param pre El festival premiado que se va a actualizar.
     */
    public void actualizar(Festival_Premio pre) {
        try {
            if (pre.getGalardon().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Galardón inválido"));
                return;
            }
            if (pre.getGalardon().length() > 50) {
                FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Galardón demasiado extenso"));
                return;
            }
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_festival_premio('%s','%s','%s');", pre.getFestival(), pre.getPremio(), pre.getGalardon()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al actualizar festival_premio"));
            e.printStackTrace();
        }
    }

    /**
     * Confirma la eliminación de los festivales premiados seleccionados.
     * Establece los indicadores de acción y eliminación como falsos después de la confirmación.
     * Para cada festival premiado seleccionado, se llama al método SQL para eliminarlo.
     * Si ocurre algún error durante el proceso, se muestra un mensaje de error en la página y se imprime la traza de la excepción.
     * Se refresca la página después de completar la eliminación.
     */
    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Festival_Premio p : festival_premios_seleccionados) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_festival_premio('%s');", p.getFestival()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar festival_premio"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    /**
     * Cancela la eliminación de los festivales premiados seleccionados.
     * Establece los indicadores de acción y eliminación como falsos y vacía la lista de festivales premiados seleccionados.
     */
    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        festival_premios_seleccionados = new ArrayList<>();
    }

    /**
     * Refresca la página actual redireccionando a la página de festival premiado.
     * Se utiliza la clase ExternalContext para obtener el contexto externo de la aplicación y redirigir a la URL de la página de festival premiado.
     * Si ocurre algún error durante el proceso, se imprime la traza de la excepción.
     */
    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/festivalpremio.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Genera un archivo PDF con la lista de festivales premiados.
     * Crea un documento PDF con una tabla que muestra la información de los festivales premiados.
     * Si no hay festivales premiados disponibles, se muestra un mensaje de error en la página y se devuelve null.
     * Se utiliza la clase FacesContext para agregar mensajes de error si no hay registros para mostrar.
     * El archivo PDF generado se guarda en un flujo de salida de bytes y se devuelve como un arreglo de bytes.
     * Si ocurre un error durante el proceso de generación del PDF, se imprime la traza de la excepción.
     *
     * @return Arreglo de bytes que representa el archivo PDF generado, o null si no hay registros para mostrar.
     */
    public byte[] generar_pdf() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfPTable table = new PdfPTable(3);
            header(table);
            if (festival_premios == null) {
                FacesContext.getCurrentInstance().addMessage("festival_premio", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
                return null;
            } else {
                for (Festival_Premio p : festival_premios) {
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
     * Agrega el encabezado a la tabla PDF especificada.
     * Crea las celdas de encabezado de la tabla con el estilo de fuente y color especificado.
     * Los encabezados son los nombres de las columnas de la tabla: Festival, Premio y Galardón.
     *
     * @param table Tabla PDF a la que se agregará el encabezado.
     */
    public void header(PdfPTable table) {
        String[] headers = {"Festival", "Premio", "Galardón"};
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
     * Agrega una fila a la tabla PDF con la información del festival premiado especificado.
     * Agrega las celdas de la fila con los valores del festival, premio y galardón.
     *
     * @param table Tabla PDF a la que se agregará la fila.
     * @param p     Festival_Premio que contiene la información a agregar a la fila.
     */
    public void agregar_rows(PdfPTable table, Festival_Premio p) {
        table.addCell(p.getFestival());
        table.addCell(p.getPremio());
        table.addCell(p.getGalardon());
    }

    /**
     * Descarga un archivo PDF que contiene la lista de festivales premiados.
     * Utiliza el método generar_pdf() para obtener el contenido del archivo PDF y Descargador para realizar la descarga.
     * El nombre del archivo descargado se establece como "festivalpremio.pdf".
     */
    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "festivalpremio.pdf");
    }

    /**
     * Obtiene la lista de festivales premiados seleccionados.
     *
     * @return La lista de festivales premiados seleccionados.
     */
    public List<Festival_Premio> getFestival_premios_seleccionados() {
        return festival_premios_seleccionados;
    }

    /**
     * Establece la lista de festivales premiados seleccionados.
     *
     * @param festival_premios_seleccionados La lista de festivales premiados seleccionados a establecer.
     */
    public void setFestival_premios_seleccionados(List<Festival_Premio> festival_premios_seleccionados) {
        this.festival_premios_seleccionados = festival_premios_seleccionados;
    }

    /**
     * Obtiene la lista de todos los festivales premiados.
     *
     * @return La lista de todos los festivales premiados.
     */
    public List<Festival_Premio> getFestival_premios() {
        return festival_premios;
    }

    /**
     * Establece la lista de todos los festivales premiados.
     *
     * @param festival_premios La lista de festivales premiados a establecer.
     */
    public void setFestival_premios(List<Festival_Premio> festival_premios) {
        this.festival_premios = festival_premios;
    }

    /**
     * Verifica si la operación de edición está activa.
     *
     * @return true si la operación de edición está activa, false de lo contrario.
     */
    public boolean isEsEditable() {
        return esEditable;
    }

    /**
     * Establece el estado de la operación de edición.
     *
     * @param esEditable true para activar la operación de edición, false de lo contrario.
     */
    public void setEsEditable(boolean esEditable) {
        this.esEditable = esEditable;
    }

    /**
     * Verifica si la operación de eliminación está activa.
     *
     * @return true si la operación de eliminación está activa, false de lo contrario.
     */
    public boolean isEsEliminar() {
        return esEliminar;
    }


    /**
     * Establece si la operación de eliminación está activa.
     *
     * @param esEliminar true para activar la operación de eliminación, false de lo contrario.
     */
    public void setEsEliminar(boolean esEliminar) {
        this.esEliminar = esEliminar;
    }

    /**
     * Verifica si hay alguna acción en curso.
     *
     * @return true si hay alguna acción en curso, false de lo contrario.
     */
    public boolean isEsAccion() {
        return esAccion;
    }

    /**
     * Establece si hay alguna acción en curso.
     *
     * @param esAccion true para indicar que hay alguna acción en curso, false de lo contrario.
     */
    public void setEsAccion(boolean esAccion) {
        this.esAccion = esAccion;
    }

    /**
     * Obtiene el nombre del festival.
     *
     * @return El nombre del festival.
     */
    public String getFestival() {
        return festival;
    }

    /**
     * Establece el nombre del festival.
     *
     * @param festival El nombre del festival a establecer.
     */
    public void setFestival(String festival) {
        this.festival = festival;
    }

    /**
     * Obtiene el nombre del premio.
     *
     * @return El nombre del premio.
     */
    public String getPremio() {
        return premio;
    }

    /**
     * Establece el nombre del premio.
     *
     * @param premio El nombre del premio a establecer.
     */
    public void setPremio(String premio) {
        this.premio = premio;
    }

    /**
     * Obtiene el galardón asociado al premio.
     *
     * @return El galardón asociado al premio.
     */
    public String getGalardon() {
        return galardon;
    }

    /**
     * Establece el galardón asociado al premio.
     *
     * @param galardon El galardón asociado al premio a establecer.
     */
    public void setGalardon(String galardon) {
        this.galardon = galardon;
    }

    /**
     * Recupera una lista de premios asociados a un festival dado.
     *
     * @param festival el nombre del festival
     * @return una lista de premios asociados al festival
     */
    public List<String> get_prefio_festival(String festival) {
        List<String> list = new ArrayList<>();
        for (Festival_Premio fp : festival_premios) {
            if (fp.getFestival().equals(festival)) {
                list.add(fp.getPremio());
            }
        }
        if (list.isEmpty()) {
            list.add("No hay premios");
        }
        return list;
    }
}

