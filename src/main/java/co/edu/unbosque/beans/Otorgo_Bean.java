package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Otorgo;
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

@Named("otorgo")
@ViewScoped
public class Otorgo_Bean implements Serializable {

    private List<Otorgo> otorgos_seleccionados = new ArrayList<>();
    private List<Otorgo> otorgos;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String festival = "";
    private int certamen = 0;
    private String premio = "";
    private String cip = "";

    /**
     * Inicializa el bean.
     */
    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Otorgo>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_otorgo();");
            otorgos = gson.fromJson(json, type);
        } catch (Exception e) {
            otorgos = null;
            e.printStackTrace();
        }
    }

    /**
     * Alterna la selección de un objeto Otorgo.
     *
     * @param oto El objeto Otorgo para alternar la selección.
     */
    public void toggleSelected(Otorgo oto) {
        if (otorgos_seleccionados.contains(oto)) {
            otorgos_seleccionados.remove(oto);
        } else {
            otorgos_seleccionados.add(oto);
        }
    }

    /**
     * Crea un nuevo objeto Otorgo.
     */
    public void crear() {
        if (premio.equals("No hay premios")) {
            FacesContext.getCurrentInstance().addMessage("otorgo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay premios para ese festival"));
            return;
        }
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL crear_otorgo('%s', '%s', '%s', '%s');", festival, certamen, premio, cip));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("otorgo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear Otorgo"));
            e.printStackTrace();
        }
        refrescar_pagina();
    }


    /**
     * Establece el estado de acción y eliminación como verdadero.
     */
    public void eliminar() {
        esAccion = true;
        esEliminar = true;
    }

    /**
     * Establece el estado de acción y edición como verdadero.
     */
    public void editar() {
        esAccion = true;
        esEditable = true;
    }

    /**
     * Establece el estado de acción y edición como falso.
     */
    public void guardar_editado() {
        esAccion = false;
        esEditable = false;
    }

    /**
     * Actualiza un objeto Otorgo.
     *
     * @param oto El objeto Otorgo a actualizar.
     */
    public void actualizar(Otorgo oto) {
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_otorgo('%s','%s','%s','%s');", oto.getFestival(), oto.getCertamen(), oto.getCip(), oto.getPremio()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("otorgo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al actualizar otorgo"));
            e.printStackTrace();
        }
    }

    /**
     * Confirma la eliminación de los objetos Otorgo seleccionados.
     */
    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Otorgo o : otorgos_seleccionados) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_otorgo('%s');", o.getFestival()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("otorgo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar otorgo"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }


    /**
     * Cancela la eliminación de los objetos Otorgo seleccionados y restablece la lista de objetos seleccionados.
     */
    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        otorgos_seleccionados = new ArrayList<>();
    }

    /**
     * Refresca la página redirigiendo a la vista de Otorgo.
     */
    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/otorgo.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera un archivo PDF con los datos de los objetos Otorgo.
     *
     * @return Un array de bytes que representa el archivo PDF generado.
     */
    public byte[] generar_pdf() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfPTable table = new PdfPTable(4);
            header(table);
            if (otorgos != null) {
                for (Otorgo o : otorgos) {
                    agregar_rows(table, o);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage("otorgo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
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
     * Agrega el encabezado a la tabla PDF con los nombres de las columnas.
     *
     * @param table La tabla PDF a la que se agregará el encabezado.
     */
    public void header(PdfPTable table) {
        String[] headers = {"Festival", "Certamen", "Premio", "CIP"};
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
     * Agrega una fila a la tabla PDF con los datos de un objeto Otorgo.
     *
     * @param table La tabla PDF a la que se agregará la fila.
     * @param o     El objeto Otorgo del que se tomarán los datos para la fila.
     */
    public void agregar_rows(PdfPTable table, Otorgo o) {
        table.addCell(o.getFestival());
        table.addCell(String.valueOf(o.getCertamen()));
        table.addCell(o.getPremio());
        table.addCell(o.getCip());
    }

    /**
     * Descarga un archivo PDF con los datos de los objetos Otorgo.
     */
    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "otorgos.pdf");
    }

    /**
     * Obtiene la lista de objetos Otorgo seleccionados.
     *
     * @return La lista de objetos Otorgo seleccionados.
     */
    public List<Otorgo> getOtorgos_seleccionados() {
        return otorgos_seleccionados;
    }

    /**
     * Establece la lista de objetos Otorgo seleccionados.
     *
     * @param otorgos_seleccionados La lista de objetos Otorgo seleccionados a establecer.
     */
    public void setOtorgos_seleccionados(List<Otorgo> otorgos_seleccionados) {
        this.otorgos_seleccionados = otorgos_seleccionados;
    }

    /**
     * Obtiene la lista de objetos Otorgo.
     *
     * @return La lista de objetos Otorgo.
     */
    public List<Otorgo> getOtorgos() {
        return otorgos;
    }

    /**
     * Establece la lista de objetos Otorgo.
     *
     * @param otorgos La lista de objetos Otorgo a establecer.
     */
    public void setOtorgos(List<Otorgo> otorgos) {
        this.otorgos = otorgos;
    }

    /**
     * Indica si la interfaz de usuario es editable.
     *
     * @return true si la interfaz de usuario es editable, de lo contrario, false.
     */
    public boolean isEsEditable() {
        return esEditable;
    }


    /**
     * Establece si la interfaz de usuario es editable.
     *
     * @param esEditable true si la interfaz de usuario es editable, de lo contrario, false.
     */
    public void setEsEditable(boolean esEditable) {
        this.esEditable = esEditable;
    }

    /**
     * Indica si la interfaz de usuario tiene la opción de eliminar.
     *
     * @return true si la interfaz de usuario tiene la opción de eliminar, de lo contrario, false.
     */
    public boolean isEsEliminar() {
        return esEliminar;
    }

    /**
     * Establece si la interfaz de usuario tiene la opción de eliminar.
     *
     * @param esEliminar true si la interfaz de usuario tiene la opción de eliminar, de lo contrario, false.
     */
    public void setEsEliminar(boolean esEliminar) {
        this.esEliminar = esEliminar;
    }

    /**
     * Indica si la interfaz de usuario está en modo de acción.
     *
     * @return true si la interfaz de usuario está en modo de acción, de lo contrario, false.
     */
    public boolean isEsAccion() {
        return esAccion;
    }

    /**
     * Establece si la interfaz de usuario está en modo de acción.
     *
     * @param esAccion true si la interfaz de usuario está en modo de acción, de lo contrario, false.
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
     * Obtiene el número del certamen.
     *
     * @return El número del certamen.
     */
    public int getCertamen() {
        return certamen;
    }

    /**
     * Establece el número del certamen.
     *
     * @param certamen El número del certamen a establecer.
     */
    public void setCertamen(int certamen) {
        this.certamen = certamen;
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
     * Obtiene el CIP.
     *
     * @return El CIP.
     */
    public String getCip() {
        return cip;
    }

    /**
     * Establece el CIP.
     *
     * @param cip El CIP a establecer.
     */
    public void setCip(String cip) {
        this.cip = cip;
    }

}
