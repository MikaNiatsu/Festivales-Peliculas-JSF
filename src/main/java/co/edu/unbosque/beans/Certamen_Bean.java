package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Certamen;
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

@Named("certamen")
@ViewScoped
public class Certamen_Bean implements Serializable {
    private List<Certamen> certamenes;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String festival = "";
    private int certamen = 0;
    private String organizador = "";
    private List<Certamen> certamenesSeleccionados = new ArrayList<>();
    private List<String> festivales = new ArrayList<>();
    private List<String> ano_certamenes = new ArrayList<>();

    /**
     * Inicializa el bean de Certamen.
     * Este método se ejecuta automáticamente después de que el bean ha sido construido e inyectado,
     * permitiendo inicializar cualquier estado necesario para el funcionamiento del bean.
     * Utiliza Gson para convertir un JSON de certámenes obtenido desde la base de datos en una lista de objetos Certamen.
     * En caso de error al obtener los certámenes, establece la lista de certámenes como nula e imprime la traza de la excepción.
     */
    @PostConstruct
    public void init() {
        // Configurar Gson para adaptar LocalDate
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        // Definir el tipo de la lista de certámenes
        Type type = new TypeToken<List<Certamen>>() {
        }.getType();

        // Intentar obtener los certámenes desde la base de datos
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_certamen();");
            certamenes = gson.fromJson(json, type);
        } catch (Exception e) {
            // En caso de error, establecer la lista de certámenes como nula
            certamenes = null;
            // Imprimir la traza de la excepción
            e.printStackTrace();
        }
    }


    /**
     * Devuelve si el modo edición está activado.
     *
     * @return true si el modo edición está activado, false de lo contrario.
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
     * Devuelve si el modo eliminación está activado.
     *
     * @return true si el modo eliminación está activado, false de lo contrario.
     */
    public boolean isEsEliminar() {
        return esEliminar;
    }

    /**
     * Establece el modo eliminación.
     *
     * @param esEliminar true para activar el modo eliminación, false para desactivarlo.
     */
    public void setEsEliminar(boolean esEliminar) {
        this.esEliminar = esEliminar;
    }

    /**
     * Devuelve si alguna acción está en curso.
     *
     * @return true si alguna acción está en curso, false de lo contrario.
     */
    public boolean isEsAccion() {
        return esAccion;
    }

    /**
     * Establece si alguna acción está en curso.
     *
     * @param esAccion true para indicar que alguna acción está en curso, false de lo contrario.
     */
    public void setEsAccion(boolean esAccion) {
        this.esAccion = esAccion;
    }


    /**
     * Cambia el estado de selección de un certamen.
     * Si el certamen ya está seleccionado, lo deselecciona. Si no está seleccionado, lo selecciona.
     *
     * @param cer el certamen cuyo estado de selección se va a cambiar.
     */
    public void toggleSelected(Certamen cer) {
        if (certamenesSeleccionados.contains(cer)) {
            // Si el certamen ya está seleccionado, se deselecciona
            certamenesSeleccionados.remove(cer);
        } else {
            // Si el certamen no está seleccionado, se selecciona
            certamenesSeleccionados.add(cer);
        }
    }


    /**
     * Crea un nuevo certamen con la información proporcionada.
     * Se valida que el organizador no esté vacío y no exceda los 60 caracteres.
     * Si la validación falla, se muestra un mensaje de error en la interfaz.
     * Si ocurre algún error durante la creación del certamen, se muestra un mensaje de error en la interfaz.
     * Después de la creación exitosa del certamen, se refresca la página para actualizar la lista de certámenes.
     */
    public void crear() {
        try {
            // Validar que el organizador no esté vacío
            if (organizador.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Organizador inválido"));
                return;
            }
            // Validar que el organizador no exceda los 60 caracteres
            if (organizador.length() > 60) {
                FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Organizador demasiado extenso"));
                return;
            }
            // Llamar a la función SQL para crear el certamen
            Funciones_SQL.llamar_metodo(String.format("CALL crear_certamen('%s', '%s', '%s');", festival, certamen, organizador));
        } catch (Exception e) {
            // En caso de error, mostrar un mensaje de error en la interfaz
            FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Certamen inválido"));
            return;
        }
        // Después de la creación exitosa del certamen, refrescar la página para actualizar la lista de certámenes
        refrescar_pagina();
    }


    /**
     * Prepara el estado para eliminar un certamen.
     * Establece las banderas de acción y eliminación como verdaderas.
     * Esto indica que se está realizando una acción de eliminación.
     */
    public void eliminar() {
        esAccion = true;
        esEliminar = true;
    }

    /**
     * Prepara el estado para editar un certamen.
     * Establece las banderas de acción y edición como verdaderas.
     * Esto indica que se está realizando una acción de edición.
     */
    public void editar() {
        esAccion = true;
        esEditable = true;
    }

    /**
     * Guarda los cambios realizados en la edición de un certamen.
     * Establece las banderas de acción y edición como falsas.
     * Esto indica que se ha completado la acción de edición.
     */
    public void guardar_editado() {
        esAccion = false;
        esEditable = false;
    }


    /**
     * Actualiza la información de un certamen en la base de datos.
     * Se valida que el organizador no esté vacío y no exceda los 60 caracteres.
     * Si la validación falla, se muestra un mensaje de error en la interfaz.
     * Si ocurre algún error durante la actualización del certamen, se imprime la traza de la excepción.
     *
     * @param cer el certamen cuya información se va a actualizar.
     */
    public void actualizar(Certamen cer) {
        try {
            // Validar que el organizador no esté vacío
            if (cer.getOrganizador().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Organizador inválido"));
                return;
            }
            // Validar que el organizador no exceda los 60 caracteres
            if (cer.getOrganizador().length() > 60) {
                FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Organizador demasiado extenso"));
                return;
            }
            // Llamar a la función SQL para actualizar el certamen
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_certamen('%s','%s', '%s');", cer.getFestival(), cer.getCertamen(), cer.getOrganizador()));
        } catch (SQLException e) {
            // En caso de error, imprimir la traza de la excepción
            e.printStackTrace();
        }
    }


    /**
     * Confirma la eliminación de los certámenes seleccionados.
     * Establece las banderas de acción y eliminación como falsas.
     * Elimina los certámenes seleccionados de la base de datos y refresca la página para actualizar la lista de certámenes.
     */
    public void confirmar_eliminar() {
        // Establecer las banderas de acción y eliminación como falsas
        esAccion = false;
        esEliminar = false;

        // Iterar sobre los certámenes seleccionados para eliminarlos de la base de datos
        for (Certamen c : certamenesSeleccionados) {
            try {
                // Llamar a la función SQL para eliminar el certamen
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_certamen('%s');", c.getCertamen()));
            } catch (SQLException e) {
                // En caso de error, mostrar un mensaje de error en la interfaz y imprimir la traza de la excepción
                FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Certamen inválido"));
                e.printStackTrace();
            }
        }

        // Refrescar la página para actualizar la lista de certámenes
        refrescar_pagina();
    }


    /**
     * Cancela la acción de eliminación de certámenes.
     * Establece las banderas de acción y eliminación como falsas.
     * Limpia la lista de certámenes seleccionados, dejándola vacía.
     */
    public void cancelar_eliminar() {
        // Establecer las banderas de acción y eliminación como falsas
        esAccion = false;
        esEliminar = false;

        // Limpiar la lista de certámenes seleccionados
        certamenesSeleccionados = new ArrayList<>();
    }


    /**
     * Refresca la página actual redireccionando a la misma URL.
     * Esto se logra obteniendo el contexto externo de JSF y redireccionando a la misma página.
     * Si ocurre algún error durante la redirección, se imprime la traza de la excepción.
     */
    public void refrescar_pagina() {
        // Obtener el contexto externo de JSF
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            // Redireccionar a la misma página
            externalContext.redirect(externalContext.getRequestContextPath() + "/cetarmen.xhtml");
        } catch (IOException e) {
            // En caso de error, imprimir la traza de la excepción
            e.printStackTrace();
        }
    }


    /**
     * Genera un archivo PDF con la información de los certámenes.
     * La información se obtiene de la lista de certámenes actual.
     * Si la lista de certámenes es nula, se muestra un mensaje de error en la interfaz y se devuelve null.
     * Si ocurre algún error durante la generación del PDF, se imprime la traza de la excepción.
     *
     * @return un arreglo de bytes que representa el archivo PDF generado, o null si no hay certámenes para mostrar.
     */
    public byte[] generar_pdf() {
        // Crear un flujo de salida de bytes para almacenar el PDF generado
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Crear un documento PDF en formato horizontal
        Document document = new Document(PageSize.A4.rotate());

        try {
            // Obtener una instancia de PdfWriter para escribir en el flujo de salida
            PdfWriter.getInstance(document, outputStream);
            // Abrir el documento
            document.open();

            // Crear una tabla PDF con 3 columnas
            PdfPTable table = new PdfPTable(3);
            // Agregar el encabezado a la tabla
            header(table);

            // Verificar si la lista de certámenes no es nula
            if (certamenes != null) {
                // Iterar sobre la lista de certámenes y agregar cada uno a la tabla
                for (Certamen c : certamenes) {
                    agregar_rows(table, c);
                }
            } else {
                // Si la lista de certámenes es nula, mostrar un mensaje de error en la interfaz y devolver null
                FacesContext.getCurrentInstance().addMessage("certamen", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
                return null;
            }

            // Agregar la tabla al documento
            document.add(table);
            // Cerrar el documento
            document.close();
        } catch (DocumentException e) {
            // En caso de error durante la generación del PDF, imprimir la traza de la excepción
            e.printStackTrace();
        }

        // Devolver un arreglo de bytes que representa el archivo PDF generado
        return outputStream.toByteArray();
    }

    /**
     * Agrega el encabezado a una tabla PDF.
     * El encabezado consta de las columnas "Festival", "Certamen" y "Organizador".
     * Se establece un estilo de fuente en negrita y color blanco para el texto del encabezado.
     *
     * @param table la tabla PDF a la que se agregará el encabezado.
     */
    public void header(PdfPTable table) {
        // Arreglo de strings que representa los encabezados de las columnas
        String[] headers = {"Festival", "Certamen", "Organizador"};
        // Crear un objeto Font para el estilo del texto del encabezado
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(BaseColor.WHITE); // Establecer el color de la fuente en blanco

        // Iterar sobre cada encabezado en el arreglo
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(); // Crear una celda PDF
            cell.setBackgroundColor(new BaseColor(33, 150, 243)); // Establecer el color de fondo de la celda en azul
            cell.setPadding(3); // Establecer el espacio de relleno de la celda
            cell.setPhrase(new Phrase(header, font)); // Establecer el texto del encabezado en la celda con el estilo de fuente
            table.addCell(cell); // Agregar la celda a la tabla
        }
    }


    /**
     * Agrega una fila a una tabla PDF con la información de un certamen.
     * La fila contendrá el nombre del festival, el número del certamen y el organizador.
     *
     * @param table la tabla PDF a la que se agregará la fila.
     * @param c     el certamen del cual se obtendrá la información para agregar a la fila.
     */
    public void agregar_rows(PdfPTable table, Certamen c) {
        // Agregar el nombre del festival a la fila
        table.addCell(c.getFestival());
        // Agregar el número del certamen a la fila
        table.addCell(String.valueOf(c.getCertamen()));
        // Agregar el organizador a la fila
        table.addCell(c.getOrganizador());
    }


    /**
     * Descarga el archivo PDF generado de los certámenes.
     * Utiliza el método {@code Descargador.descargar_pdf()} para descargar el PDF generado.
     */
    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "certamenes.pdf");
    }

    /**
     * Obtiene la lista de certámenes seleccionados.
     *
     * @return la lista de certámenes seleccionados.
     */
    public List<Certamen> getCertamenesSeleccionados() {
        return certamenesSeleccionados;
    }

    /**
     * Establece la lista de certámenes seleccionados.
     *
     * @param certamenesSeleccionados la lista de certámenes seleccionados a establecer.
     */
    public void setCertamenesSeleccionados(List<Certamen> certamenesSeleccionados) {
        this.certamenesSeleccionados = certamenesSeleccionados;
    }

    /**
     * Obtiene el organizador del certamen.
     *
     * @return el organizador del certamen.
     */
    public String getOrganizador() {
        return organizador;
    }

    /**
     * Establece el organizador del certamen.
     *
     * @param organizador el organizador del certamen a establecer.
     */
    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    /**
     * Obtiene el número del certamen.
     *
     * @return el número del certamen.
     */
    public int getCertamen() {
        return certamen;
    }


    /**
     * Establece el número del certamen.
     *
     * @param certamen el número del certamen a establecer.
     */
    public void setCertamen(int certamen) {
        this.certamen = certamen;
    }

    /**
     * Obtiene el nombre del festival.
     *
     * @return el nombre del festival.
     */
    public String getFestival() {
        return festival;
    }

    /**
     * Establece el nombre del festival.
     *
     * @param festival el nombre del festival a establecer.
     */
    public void setFestival(String festival) {
        this.festival = festival;
    }

    /**
     * Obtiene la lista de certámenes.
     *
     * @return la lista de certámenes.
     */
    public List<Certamen> getCertamenes() {
        return certamenes;
    }

    /**
     * Establece la lista de certámenes.
     *
     * @param certamenes la lista de certámenes a establecer.
     */
    public void setCertamenes(List<Certamen> certamenes) {
        this.certamenes = certamenes;
    }

    /**
     * Obtiene una lista de nombres de festivales a partir de la lista de certámenes.
     * Esta lista se genera cada vez que se llama al método.
     *
     * @return una lista de nombres de festivales.
     */
    public List<String> getFestivales() {
        festivales = new ArrayList<>();
        for (Certamen c : certamenes) {
            festivales.add(c.getFestival());
        }
        return festivales;
    }


    /**
     * Establece la lista de nombres de festivales.
     *
     * @param festivales la lista de nombres de festivales a establecer.
     */
    public void setFestivales(List<String> festivales) {
        this.festivales = festivales;
    }

    /**
     * Obtiene una lista de números de certámenes como strings a partir de la lista de certámenes.
     * Esta lista se genera cada vez que se llama al método.
     *
     * @return una lista de números de certámenes como strings.
     */
    public List<String> getAno_certamenes() {
        ano_certamenes = new ArrayList<>();
        for (Certamen c : certamenes) {
            ano_certamenes.add(String.valueOf(c.getCertamen()));
        }
        return ano_certamenes;
    }

    /**
     * Establece la lista de números de certámenes como strings.
     *
     * @param ano_certamenes la lista de números de certámenes como strings a establecer.
     */
    public void setAno_certamenes(List<String> ano_certamenes) {
        this.ano_certamenes = ano_certamenes;
    }

    public List<String> get_Ano_certamenes(String festival) {
        ano_certamenes = new ArrayList<>();
        for (Certamen c : certamenes) {
            if (c.getFestival().equals(festival)) {
                ano_certamenes.add(String.valueOf(c.getCertamen()));
            }
        }
        return ano_certamenes;
    }

}
