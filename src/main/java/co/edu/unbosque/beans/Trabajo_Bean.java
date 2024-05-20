package co.edu.unbosque.beans;


import co.edu.unbosque.persistence.Trabajo;
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

@Named("trabajo")
@ViewScoped
public class Trabajo_Bean implements Serializable {
    private List<Trabajo> trabajos;
    private List<Trabajo> trabajos_seleccionados = new ArrayList<>();
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String trabajo = "";
    private String cip = "";
    private String nombre = "";

    /**
     * Devuelve el nombre del trabajo.
     *
     * @return El nombre del trabajo.
     */
    public String getTrabajo() {
        return trabajo;
    }

    /**
     * Establece el nombre del trabajo.
     *
     * @param trabajo El nombre del trabajo a establecer.
     */
    public void setTrabajo(String trabajo) {
        this.trabajo = trabajo;
    }

    /**
     * Devuelve el CIP del trabajo.
     *
     * @return El CIP del trabajo.
     */
    public String getCip() {
        return cip;
    }

    /**
     * Establece el CIP del trabajo.
     *
     * @param cip El CIP del trabajo a establecer.
     */
    public void setCip(String cip) {
        this.cip = cip;
    }

    /**
     * Devuelve el nombre.
     *
     * @return El nombre.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre.
     *
     * @param nombre El nombre a establecer.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Inicializa la clase. Obtiene los trabajos de la base de datos y los asigna a la lista de trabajos.
     */
    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Trabajo>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_trabajo();");
            trabajos = gson.fromJson(json, type);

        } catch (Exception e) {
            trabajos = null;
            e.printStackTrace();
        }
    }

    /**
     * Devuelve la lista de trabajos.
     *
     * @return La lista de trabajos.
     */
    public List<Trabajo> getTrabajos() {
        return trabajos;
    }

    /**
     * Establece la lista de trabajos.
     *
     * @param trabajos La lista de trabajos a establecer.
     */
    public void setTrabajos(List<Trabajo> trabajos) {
        this.trabajos = trabajos;
    }

    /**
     * Devuelve la lista de trabajos seleccionados.
     *
     * @return La lista de trabajos seleccionados.
     */
    public List<Trabajo> getTrabajos_seleccionados() {
        return trabajos_seleccionados;
    }

    /**
     * Establece la lista de trabajos seleccionados.
     *
     * @param trabajos_seleccionados La lista de trabajos seleccionados a establecer.
     */
    public void setTrabajos_seleccionados(List<Trabajo> trabajos_seleccionados) {
        this.trabajos_seleccionados = trabajos_seleccionados;
    }

    /**
     * Verifica si es editable.
     *
     * @return True si es editable, False en caso contrario.
     */
    public boolean isEsEditable() {
        return esEditable;
    }

    /**
     * Establece si es editable.
     *
     * @param esEditable True si es editable, False en caso contrario.
     */
    public void setEsEditable(boolean esEditable) {
        this.esEditable = esEditable;
    }

    /**
     * Verifica si es eliminar.
     *
     * @return True si es eliminar, False en caso contrario.
     */
    public boolean isEsEliminar() {
        return esEliminar;
    }

    /**
     * Establece si es eliminar.
     *
     * @param esEliminar True si es eliminar, False en caso contrario.
     */
    public void setEsEliminar(boolean esEliminar) {
        this.esEliminar = esEliminar;
    }

    /**
     * Verifica si es una acción.
     *
     * @return True si es una acción, False en caso contrario.
     */
    public boolean isEsAccion() {
        return esAccion;
    }

    /**
     * Establece si es una acción.
     *
     * @param esAccion True si es una acción, False en caso contrario.
     */
    public void setEsAccion(boolean esAccion) {
        this.esAccion = esAccion;
    }

    /**
     * Alterna la selección de un trabajo.
     *
     * @param tra El trabajo para alternar la selección.
     */
    public void toggleSelected(Trabajo tra) {
        if (trabajos_seleccionados.contains(tra)) {
            trabajos_seleccionados.remove(tra);
        } else {
            trabajos_seleccionados.add(tra);
        }
    }

    /**
     * Crea un nuevo trabajo. Verifica si el CIP ya existe en la lista de trabajos.
     * Si el CIP ya existe, muestra un mensaje de error. Llama a la función SQL para crear el trabajo.
     * Actualiza la página después de la operación.
     */
    public void crear() {
        try {
            for (Trabajo t : trabajos) {
                if (t.getCip().equals(cip) && t.getTarea().equals(trabajo)) {
                    FacesContext.getCurrentInstance().addMessage("tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "CIP ya existe"));
                    return;
                }
            }
            Funciones_SQL.llamar_metodo(String.format("CALL crear_trabajo('%s', '%s', '%s');", cip, nombre, trabajo));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Tarea inválida"));
            e.printStackTrace();
        }
        refrescar_pagina();
    }

    /**
     * Marca la acción como eliminar.
     */
    public void eliminar() {
        esAccion = true;
        esEliminar = true;
    }

    /**
     * Marca la acción como editar.
     */
    public void editar() {
        esAccion = true;
        esEditable = true;
    }

    /**
     * Marca la acción como guardar editado.
     */
    public void guardar_editado() {
        esAccion = false;
        esEditable = false;
    }

    /**
     * Actualiza un trabajo. Llama a la función SQL para actualizar el trabajo.
     *
     * @param tra El trabajo a actualizar.
     */
    public void actualizar(Trabajo tra) {
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_trabajo('%s','%s', '%s');", tra.getCip(), tra.getNombre_persona(), tra.getTarea()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Confirma la eliminación de trabajos seleccionados. Llama a la función SQL para eliminar los trabajos seleccionados.
     * Muestra un mensaje de error si hay algún problema.
     * Actualiza la página después de la operación.
     */
    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Trabajo t : trabajos_seleccionados) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_trabajo('%s');", t.getCip()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Tarea inválida"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    /**
     * Cancela la eliminación de trabajos. Restablece las variables relacionadas con la eliminación.
     */
    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        trabajos_seleccionados = new ArrayList<>();
    }

    /**
     * Refresca la página actual redireccionando a la página de trabajo.
     */
    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/trabajo.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera un PDF con la información de los trabajos. Si no hay registros, muestra un mensaje de error.
     * Devuelve el contenido del PDF en un array de bytes.
     *
     * @return Contenido del PDF en un array de bytes.
     */
    public byte[] generar_pdf() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfPTable table = new PdfPTable(3);
            header(table);
            if (trabajos == null) {
                FacesContext.getCurrentInstance().addMessage("tarea", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
                return null;
            } else {
                for (Trabajo t : trabajos) {
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
     * Agrega una fila a la tabla PDF con la información de un trabajo.
     *
     * @param table La tabla PDF a la que se agregará la fila.
     * @param t     El trabajo del cual se agregará la información a la fila.
     */
    public void agregar_rows(PdfPTable table, Trabajo t) {
        table.addCell(t.getCip());
        table.addCell(t.getTarea());
        table.addCell(t.getNombre_persona());
    }

    /**
     * Agrega un encabezado a la tabla PDF con los nombres de las columnas.
     *
     * @param table La tabla PDF a la que se agregará el encabezado.
     */
    public void header(PdfPTable table) {
        String[] headers = {"CIP", "Tarea", "Persona"};
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
     * Descarga un archivo PDF que contiene la información de los trabajos.
     */
    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "trabajos.pdf");
    }

}
