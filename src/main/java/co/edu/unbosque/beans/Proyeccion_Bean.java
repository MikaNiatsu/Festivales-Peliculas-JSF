package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Proyeccion;
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

@Named("proyeccion")
@ViewScoped
public class Proyeccion_Bean implements Serializable {

    private List<Proyeccion> proyecciones_seleccionadas = new ArrayList<>();
    private List<Proyeccion> proyecciones;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String cine = "";
    private int sala = 0;
    private String cip = "";
    private String fecha = "";
    private int dias_estreno = 0;
    private int espectadores = 0;
    private int recaudacion = 0;

    /**
     * Inicializa el bean de respaldo para la página, obteniendo las proyecciones de películas
     * desde la base de datos y convirtiéndolas en objetos Proyeccion utilizando Gson.
     * Si ocurre algún error durante la obtención de datos, establece la lista de proyecciones
     * en nulo e imprime el error en la consola.
     */
    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Proyeccion>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_proyeccion();");
            proyecciones = gson.fromJson(json, type);
        } catch (Exception e) {
            proyecciones = null;
            e.printStackTrace();
        }
    }

    /**
     * Alterna la selección de una proyección dada. Si la proyección ya está en la lista
     * de proyecciones seleccionadas, la elimina. De lo contrario, la agrega a la lista.
     *
     * @param pro La proyección que se va a seleccionar o deseleccionar.
     */
    public void toggleSelected(Proyeccion pro) {
        if (proyecciones_seleccionadas.contains(pro)) {
            proyecciones_seleccionadas.remove(pro);
        } else {
            proyecciones_seleccionadas.add(pro);
        }
    }


    /**
     * Crea una nueva proyección de película con los parámetros proporcionados.
     * Verifica que los valores proporcionados para la sala, días de estreno, espectadores
     * y recaudación estén dentro de rangos válidos.
     * Verifica también si ya existe una proyección en la misma sala del cine y en la misma fecha.
     * Si la fecha proporcionada no es válida, se lanza un mensaje de error.
     */
    public void crear() {
        if (sala < 1 || sala > 99) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Número de sala no válido (entre 1 y 99)"));
            return;
        }
        if (dias_estreno < 1 || dias_estreno > 365) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Días no válido (entre 1 y 365)"));
            return;
        }
        if (espectadores < 0 || espectadores > 999999) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Espectadores no válido (entre 0 y 999999)"));
            return;
        }
        if (recaudacion < 0 || recaudacion > 99999999) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Recaudación no válida (entre 0 y 99999999)"));
            return;
        }
        for (Proyeccion s : proyecciones_seleccionadas) {
            if (s.getSala() == sala && s.getCine().equals(cine) && s.getFecha_estreno().equals(LocalDate.parse(fecha))) {
                FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una proyección en ese cine con ese número de sala, ese día"));
                return;
            }
        }
        try {
            LocalDate parsedDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha no válida"));
            return;
        }
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL crear_proyeccion('%s', '%s', '%s', '%s', '%s', '%s', '%s');", cine, sala, cip, fecha, dias_estreno, espectadores, recaudacion));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear sala"));
            e.printStackTrace();
        }
        refrescar_pagina();
    }


    /**
     * Establece las banderas de acción y eliminación en verdadero.
     * Se utiliza para indicar que se está realizando una acción de eliminación.
     */
    public void eliminar() {
        esAccion = true;
        esEliminar = true;
    }

    /**
     * Establece las banderas de acción y edición en verdadero.
     * Se utiliza para indicar que se está realizando una acción de edición.
     */
    public void editar() {
        esAccion = true;
        esEditable = true;
    }

    /**
     * Establece las banderas de acción y edición en falso.
     * Se utiliza para indicar que la edición ha sido guardada.
     */
    public void guardar_editado() {
        esAccion = false;
        esEditable = false;
    }

    /**
     * Actualiza la información de una proyección con los nuevos valores proporcionados.
     * Verifica que los valores proporcionados para la sala, días de estreno, espectadores
     * y recaudación estén dentro de rangos válidos.
     * Si la fecha de estreno no es válida, se lanza un mensaje de error.
     *
     * @param pro La proyección a actualizar.
     */
    public void actualizar(Proyeccion pro) {
        if (pro.getSala() < 1 || pro.getSala() > 99) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Número de sala no válido (entre 1 y 99)"));
            return;
        }
        if (pro.getDias_estreno() < 1 || pro.getDias_estreno() > 365) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Días no válidos (entre 1 y 365)"));
            return;
        }
        if (pro.getEspectadores() < 0 || pro.getEspectadores() > 999999) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Número de espectadores no válido (entre 0 y 999999)"));
            return;
        }
        if (pro.getRecaudacion() < 0 || pro.getRecaudacion() > 99999999) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Recaudación no válida (entre 0 y 99999999)"));
            return;
        }
        if (pro.getFecha_estreno() == null) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha no válida"));
            return;
        }
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_proyeccion('%s','%s','%s','%s','%s','%s','%s');", pro.getCine(), pro.getSala(), pro.getCip(), pro.getFecha_estreno(), pro.getDias_estreno(), pro.getEspectadores(), pro.getRecaudacion()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al actualizar proyección"));
            e.printStackTrace();
        }
    }


    /**
     * Confirma la eliminación de las proyecciones seleccionadas.
     * Realiza una llamada a la base de datos para eliminar cada proyección seleccionada.
     * Si ocurre un error durante la eliminación, se muestra un mensaje de error.
     */
    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Proyeccion s : proyecciones_seleccionadas) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_proyeccion('%s');", s.getCine()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar proyeccion"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    /**
     * Cancela el proceso de eliminación de las proyecciones seleccionadas.
     * Resetea las banderas de acción y eliminación, y limpia la lista de proyecciones seleccionadas.
     */
    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        proyecciones_seleccionadas = new ArrayList<>();
    }

    /**
     * Refresca la página actual redirigiendo al usuario a la misma página.
     * Si ocurre un error durante la redirección, se muestra un mensaje de error.
     */
    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/proyeccion.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera un archivo PDF con la información de las proyecciones.
     * Si no hay proyecciones para mostrar, se muestra un mensaje de error.
     *
     * @return Un arreglo de bytes que representa el contenido del PDF generado.
     */
    public byte[] generar_pdf() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            PdfPTable table = new PdfPTable(7);
            header(table);
            if (proyecciones != null) {
                for (Proyeccion s : proyecciones) {
                    agregar_rows(table, s);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
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
     * Agrega el encabezado de la tabla al PDF con los nombres de las columnas.
     *
     * @param table La tabla a la que se le agregarán los encabezados.
     */
    public void header(PdfPTable table) {
        String[] headers = {"Cine", "Cip", "Sala", "Recaudacion", "Espectadores", "Fecha estreno", "Dias estreno"};
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
     * Agrega una fila a la tabla del PDF con los datos de una proyección.
     *
     * @param table La tabla a la que se le agregarán las filas.
     * @param s     La proyección cuyos datos se agregarán a la tabla.
     */
    public void agregar_rows(PdfPTable table, Proyeccion s) {
        table.addCell(s.getCine());
        table.addCell(s.getCip() + "");
        table.addCell(s.getSala() + "");
        table.addCell(s.getRecaudacion() + "");
        table.addCell(s.getEspectadores() + "");
        table.addCell(s.getFecha_estreno() + "");
        table.addCell(s.getDias_estreno() + "");
    }


    /**
     * Descarga un archivo PDF generado con la información de las proyecciones.
     * Utiliza la clase Descargador para realizar la descarga.
     */
    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "proyecciones.pdf");
    }

    /**
     * Obtiene la lista de proyecciones seleccionadas.
     *
     * @return Una lista de proyecciones seleccionadas.
     */
    public List<Proyeccion> getProyecciones_seleccionadas() {
        return proyecciones_seleccionadas;
    }

    /**
     * Establece la lista de proyecciones seleccionadas.
     *
     * @param proyecciones_seleccionadas Una lista de proyecciones seleccionadas.
     */
    public void setProyecciones_seleccionadas(List<Proyeccion> proyecciones_seleccionadas) {
        this.proyecciones_seleccionadas = proyecciones_seleccionadas;
    }

    /**
     * Obtiene la lista de todas las proyecciones.
     *
     * @return Una lista de todas las proyecciones.
     */
    public List<Proyeccion> getProyecciones() {
        return proyecciones;
    }

    /**
     * Establece la lista de todas las proyecciones.
     *
     * @param proyecciones Una lista de todas las proyecciones.
     */
    public void setProyecciones(List<Proyeccion> proyecciones) {
        this.proyecciones = proyecciones;
    }

    /**
     * Verifica si el estado es editable.
     *
     * @return true si es editable, false en caso contrario.
     */
    public boolean isEsEditable() {
        return esEditable;
    }

    /**
     * Establece el estado editable.
     *
     * @param esEditable true para hacer editable, false en caso contrario.
     */
    public void setEsEditable(boolean esEditable) {
        this.esEditable = esEditable;
    }

    /**
     * Verifica si el estado es eliminar.
     *
     * @return true si es eliminar, false en caso contrario.
     */
    public boolean isEsEliminar() {
        return esEliminar;
    }

    /**
     * Establece el estado eliminar.
     *
     * @param esEliminar true para eliminar, false en caso contrario.
     */
    public void setEsEliminar(boolean esEliminar) {
        this.esEliminar = esEliminar;
    }

    /**
     * Verifica si el estado es acción.
     *
     * @return true si es acción, false en caso contrario.
     */
    public boolean isEsAccion() {
        return esAccion;
    }

    /**
     * Establece el estado acción.
     *
     * @param esAccion true para acción, false en caso contrario.
     */
    public void setEsAccion(boolean esAccion) {
        this.esAccion = esAccion;
    }

    /**
     * Obtiene el cine de la proyección.
     *
     * @return El nombre del cine.
     */
    public String getCine() {
        return cine;
    }

    /**
     * Establece el cine de la proyección.
     *
     * @param cine El nombre del cine.
     */
    public void setCine(String cine) {
        this.cine = cine;
    }

    /**
     * Obtiene la sala de la proyección.
     *
     * @return El número de sala.
     */
    public int getSala() {
        return sala;
    }

    /**
     * Establece la sala de la proyección.
     *
     * @param sala El número de sala.
     */
    public void setSala(int sala) {
        this.sala = sala;
    }

    /**
     * Obtiene el CIP de la proyección.
     *
     * @return El CIP de la proyección.
     */
    public String getCip() {
        return cip;
    }

    /**
     * Establece el CIP de la proyección.
     *
     * @param cip El CIP de la proyección.
     */
    public void setCip(String cip) {
        this.cip = cip;
    }

    /**
     * Obtiene la fecha de la proyección.
     *
     * @return La fecha de la proyección.
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha de la proyección.
     *
     * @param fecha La fecha de la proyección.
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene los días de estreno de la proyección.
     *
     * @return Los días de estreno.
     */
    public int getDias_estreno() {
        return dias_estreno;
    }

    /**
     * Establece los días de estreno de la proyección.
     *
     * @param dias_estreno Los días de estreno.
     */
    public void setDias_estreno(int dias_estreno) {
        this.dias_estreno = dias_estreno;
    }

    /**
     * Obtiene el número de espectadores de la proyección.
     *
     * @return El número de espectadores.
     */
    public int getEspectadores() {
        return espectadores;
    }

    /**
     * Establece el número de espectadores de la proyección.
     *
     * @param espectadores El número de espectadores.
     */
    public void setEspectadores(int espectadores) {
        this.espectadores = espectadores;
    }

    /**
     * Obtiene la recaudación de la proyección.
     *
     * @return La recaudación.
     */
    public int getRecaudacion() {
        return recaudacion;
    }

    /**
     * Establece la recaudación de la proyección.
     *
     * @param recaudacion La recaudación.
     */
    public void setRecaudacion(int recaudacion) {
        this.recaudacion = recaudacion;
    }

}
