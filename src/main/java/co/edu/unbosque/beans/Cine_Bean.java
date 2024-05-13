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

    public List<Cine> getCinesSeleccionados() {
        return cinesSeleccionados;
    }

    public void setCinesSeleccionados(List<Cine> cinesSeleccionados) {
        this.cinesSeleccionados = cinesSeleccionados;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direcccion) {
        this.direccion = direcccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCine() {
        return cine;
    }

    public void setCine(String cine) {
        this.cine = cine;
    }

    public List<Cine> getCines() {
        return cines;
    }

    public void setCines(List<Cine> cines) {
        this.cines = cines;
    }

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Cine>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_cine();");
            cines = gson.fromJson(json, type);
        } catch (Exception e) {
            cines = null;
            e.printStackTrace();
        }
    }

    public boolean isEsEditable() {
        return esEditable;
    }

    public void setEsEditable(boolean esEditable) {
        this.esEditable = esEditable;
    }

    public boolean isEsEliminar() {
        return esEliminar;
    }

    public void setEsEliminar(boolean esEliminar) {
        this.esEliminar = esEliminar;
    }

    public boolean isEsAccion() {
        return esAccion;
    }

    public void setEsAccion(boolean esAccion) {
        this.esAccion = esAccion;
    }

    public void toggleSelected(Cine cin) {
        if (cinesSeleccionados.contains(cin)) {
            cinesSeleccionados.remove(cin);
        } else {
            cinesSeleccionados.add(cin);
        }
    }

    public void crear() {
        try {
            if (cine.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cine inválido"));
                return;
            }
            if (cine.length() > 25) {
                FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cine demasiado extenso"));
                return;
            }
            if (ciudad.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ciudad inválida"));
                return;
            }
            if (ciudad.length() > 25) {
                FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ciudad demasiado extensa"));
                return;
            }
            if (direccion.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dirección inválida"));
                return;
            }
            if (direccion.length() > 60) {
                FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dirección demasiado extensa"));
                return;
            }
            Funciones_SQL.llamar_metodo(String.format("CALL crear_cine('%s', '%s', '%s');", cine, ciudad, direccion));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Certamen inválida"));
            return;
        }
        refrescar_pagina();
    }

    public void eliminar() {
        esAccion = true;
        esEliminar = true;
    }

    public void editar() {
        esAccion = true;
        esEditable = true;
    }

    public void guardar_editado() {
        esAccion = false;
        esEditable = false;
    }

    public void actualizar(Cine cin) {
        try {
            if (cin.getCine().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cine inválido"));
                return;
            }
            if (cin.getCine().length() > 25) {
                FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cine demasiado extenso"));
                return;
            }
            if (cin.getCiudad_cine().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ciudad inválida"));
                return;
            }
            if (cin.getCiudad_cine().length() > 25) {
                FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ciudad demasiado extensa"));
                return;
            }
            if (cin.getDireccion_cine().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dirección inválida"));
                return;
            }
            if (cin.getDireccion_cine().length() > 60) {
                FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dirección demasiado extensa"));
                return;
            }
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_cine('%s','%s', '%s');", cin.getCine(), cin.getCiudad_cine(), cin.getDireccion_cine()));

        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("cine", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cine inválida"));
            e.printStackTrace();
        }

    }

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

    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        cinesSeleccionados = new ArrayList<>();
    }

    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/cine.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public void agregar_rows(PdfPTable table, Cine c) {
        table.addCell(c.getCine());
        table.addCell(c.getCiudad_cine());
        table.addCell(c.getDireccion_cine());
    }

    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "cines.pdf");
    }

    public List<String> getCines_st() {
        cines_st = new ArrayList<>();
        for (Cine c : cines) {
            cines_st.add(c.getCine());
        }
        return cines_st;
    }

    public void setCines_st(List<String> cines_st) {
        this.cines_st = cines_st;
    }
}
