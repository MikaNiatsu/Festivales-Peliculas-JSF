package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Reconocimiento;
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

@Named("reconocimiento")
@ViewScoped
public class Reconocimiento_Bean implements Serializable {

    private List<Reconocimiento> reconocimientos_seleccionadas = new ArrayList<>();
    private List<Reconocimiento> reconocimientos;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String festival = "";
    private int anio = 0;
    private String premio = "";
    private String nombre = "";

    /**
     * Inicializa el bean y carga los datos de reconocimientos desde la base de datos.
     * Utiliza Gson para convertir el JSON recibido en una lista de objetos de tipo Sala.
     */
    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Reconocimiento>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_reconocimiento();");
            reconocimientos = gson.fromJson(json, type);
        } catch (Exception e) {
            reconocimientos = null;
            e.printStackTrace();
        }
    }

    /**
     * Alterna el estado de selección de un reconocimiento.
     * Si el reconocimiento está seleccionado, lo deselecciona; si no lo está, lo selecciona.
     *
     * @param rec El reconocimiento a alternar.
     */
    public void toggleSelected(Reconocimiento rec) {
        if (reconocimientos_seleccionadas.contains(rec)) {
            reconocimientos_seleccionadas.remove(rec);
        } else {
            reconocimientos_seleccionadas.add(rec);
        }
    }

    /**
     * Crea un nuevo reconocimiento.
     * Verifica que el nombre no esté vacío y que no supere los 35 caracteres.
     * Muestra mensajes de error si las validaciones fallan.
     * Realiza una llamada a la base de datos para crear el reconocimiento.
     */
    public void crear() {
        if (nombre.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre no puede estar vacio"));
            return;
        }
        if (nombre.length() > 35) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre no puede superar los 35 caracteres"));
            return;
        }
        if (premio.equals("No hay premios")) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay premios para ese festival"));
            return;
        }
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL crear_reconocimiento('%s', '%s', '%s', '%s');", festival, anio, premio, nombre));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear reconocimiento"));
            e.printStackTrace();
        }
        refrescar_pagina();
    }

    /**
     * Activa el modo de eliminación.
     * Establece los indicadores de acción y eliminación en true.
     */
    public void eliminar() {
        esAccion = true;
        esEliminar = true;
    }

    /**
     * Activa el modo de edición.
     * Establece los indicadores de acción y edición en true.
     */
    public void editar() {
        esAccion = true;
        esEditable = true;
    }

    /**
     * Guarda los cambios realizados en el modo de edición.
     * Establece los indicadores de acción y edición en false.
     */
    public void guardar_editado() {
        esAccion = false;
        esEditable = false;
    }


    /**
     * Actualiza un reconocimiento existente.
     * Verifica que el nombre no esté vacío y que no supere los 35 caracteres.
     * Muestra mensajes de error si las validaciones fallan.
     * Realiza una llamada a la base de datos para actualizar el reconocimiento.
     *
     * @param rec El reconocimiento a actualizar.
     */
    public void actualizar(Reconocimiento rec) {
        if (rec.getNombre_persona().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre no puede estar vacio"));
            return;
        }
        if (rec.getNombre_persona().length() > 35) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre no puede superar los 35 caracteres"));
            return;
        }
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_reconocimiento('%s','%s','%s', '%s');", rec.getFestival(), rec.getCertamen(), rec.getPremio(), rec.getNombre_persona()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al actualizar reconocimiento"));
            e.printStackTrace();
        }
    }

    /**
     * Confirma la eliminación de los reconocimientos seleccionados.
     * Realiza una llamada a la base de datos para eliminar cada reconocimiento seleccionado.
     * Si ocurre un error durante la eliminación, se muestra un mensaje de error.
     */
    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Reconocimiento r : reconocimientos_seleccionadas) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_reconocimiento('%s');", r.getFestival()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("reconocimiento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar reconocimiento"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    /**
     * Cancela el proceso de eliminación de los reconocimientos seleccionados.
     * Resetea las banderas de acción y eliminación, y limpia la lista de reconocimientos seleccionados.
     */
    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        reconocimientos_seleccionadas = new ArrayList<>();
    }

    /**
     * Refresca la página actual redirigiendo al usuario a la misma página.
     * Si ocurre un error durante la redirección, se muestra un mensaje de error.
     */
    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/reconocimiento.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera un archivo PDF con la información de los reconocimientos.
     *
     * @return Un array de bytes que representa el contenido del PDF.
     */
    public byte[] generar_pdf() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            PdfPTable table = new PdfPTable(4);
            header(table);
            if (reconocimientos != null) {
                for (Reconocimiento r : reconocimientos) {
                    agregar_rows(table, r);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
                return null;
            }
            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    /**
     * Añade el encabezado a la tabla del PDF.
     *
     * @param table La tabla a la que se añadirán los encabezados.
     */
    public void header(PdfPTable table) {
        String[] headers = {"Festival", "Certamen", "Premio", "Persona"};
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
     * Añade una fila a la tabla del PDF con la información de un reconocimiento.
     *
     * @param table La tabla a la que se añadirá la fila.
     * @param r     El reconocimiento cuya información se añadirá.
     */
    public void agregar_rows(PdfPTable table, Reconocimiento r) {
        table.addCell(r.getFestival());
        table.addCell(r.getCertamen());
        table.addCell(r.getPremio());
        table.addCell(r.getNombre_persona());
    }


    /**
     * Descarga un archivo PDF generado con la información de los reconocimientos.
     * Utiliza el método generar_pdf() para crear el PDF y luego lo descarga con el nombre "reconocimientos.pdf".
     */
    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "reconocimientos.pdf");
    }

    /**
     * Obtiene la lista de reconocimientos seleccionados.
     *
     * @return La lista de reconocimientos seleccionados.
     */
    public List<Reconocimiento> getReconocimientos_seleccionadas() {
        return reconocimientos_seleccionadas;
    }

    /**
     * Establece la lista de reconocimientos seleccionados.
     *
     * @param reconocimientos_seleccionadas La nueva lista de reconocimientos seleccionados.
     */
    public void setReconocimientos_seleccionadas(List<Reconocimiento> reconocimientos_seleccionadas) {
        this.reconocimientos_seleccionadas = reconocimientos_seleccionadas;
    }

    /**
     * Obtiene la lista de todos los reconocimientos.
     *
     * @return La lista de todos los reconocimientos.
     */
    public List<Reconocimiento> getReconocimientos() {
        return reconocimientos;
    }

    /**
     * Establece la lista de todos los reconocimientos.
     *
     * @param reconocimientos La nueva lista de todos los reconocimientos.
     */
    public void setReconocimientos(List<Reconocimiento> reconocimientos) {
        this.reconocimientos = reconocimientos;
    }

    /**
     * Verifica si la acción actual es editable.
     *
     * @return true si es editable, false en caso contrario.
     */
    public boolean isEsEditable() {
        return esEditable;
    }

    /**
     * Establece si la acción actual es editable.
     *
     * @param esEditable true para hacer la acción editable, false en caso contrario.
     */
    public void setEsEditable(boolean esEditable) {
        this.esEditable = esEditable;
    }

    /**
     * Verifica si la acción actual es de eliminación.
     *
     * @return true si es eliminación, false en caso contrario.
     */
    public boolean isEsEliminar() {
        return esEliminar;
    }

    /**
     * Establece si la acción actual es de eliminación.
     *
     * @param esEliminar true para hacer la acción de eliminación, false en caso contrario.
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
     * @param esAccion true para indicar que hay una acción en curso, false en caso contrario.
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
     * @param festival El nuevo nombre del festival.
     */
    public void setFestival(String festival) {
        this.festival = festival;
    }

    /**
     * Obtiene el año del festival.
     *
     * @return El año del festival.
     */
    public int getAnio() {
        return anio;
    }

    /**
     * Establece el año del festival.
     *
     * @param anio El nuevo año del festival.
     */
    public void setAnio(int anio) {
        this.anio = anio;
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
     * @param premio El nuevo nombre del premio.
     */
    public void setPremio(String premio) {
        this.premio = premio;
    }

    /**
     * Obtiene el nombre de la persona reconocida.
     *
     * @return El nombre de la persona reconocida.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la persona reconocida.
     *
     * @param nombre El nuevo nombre de la persona reconocida.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
