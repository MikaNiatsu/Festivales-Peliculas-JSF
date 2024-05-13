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

    public void toggleSelected(Proyeccion pro) {
        if (proyecciones_seleccionadas.contains(pro)) {
            proyecciones_seleccionadas.remove(pro);
        } else {
            proyecciones_seleccionadas.add(pro);
        }
    }

    public void crear() {
        if (sala < 1 || sala > 99) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Numero de sala no valido (entre 1 y 99)"));
            return;
        }
        if (dias_estreno < 1 || dias_estreno > 365) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dias no valido (entre 1 y 365)"));
            return;
        }
        if (espectadores < 0 || espectadores > 999999) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Espectadores no valido (entre 0 y 999999)"));
            return;
        }
        if (recaudacion < 0 || recaudacion > 99999999) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Recaudacion no valido (entre 0 y 99999999)"));
            return;
        }
        for (Proyeccion s : proyecciones_seleccionadas) {
            if (s.getSala() == sala && s.getCine().equals(cine) && s.getFecha_estreno().equals(LocalDate.parse(fecha))) {
                FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una proyeccion en ese cine con ese numero de sala, ese dia"));
                return;
            }
        }
        try {
            LocalDate parsedDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha no valida"));
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

    public void actualizar(Proyeccion pro) {
        if (pro.getSala() < 1 || pro.getSala() > 99) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Numero de sala no valido (entre 1 y 99)"));
            return;
        }
        if (pro.getDias_estreno() < 1 || pro.getDias_estreno() > 365) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dias no valido (entre 1 y 365)"));
            return;
        }
        if (pro.getEspectadores() < 0 || pro.getEspectadores() > 999999) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Espectadores no valido (entre 0 y 999999)"));
            return;
        }
        if (pro.getRecaudacion() < 0 || pro.getRecaudacion() > 99999999) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Recaudacion no valido (entre 0 y 99999999)"));
            return;
        }
        if (pro.getFecha_estreno() == null) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha no valida"));
            return;
        }
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_proyeccion('%s','%s','%s' ,'%s','%s','%s','%s');", pro.getCine(), pro.getSala(), pro.getCip(), pro.getFecha_estreno(), pro.getDias_estreno(), pro.getEspectadores(), pro.getRecaudacion()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("proyeccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al actualizar proyeccion"));
            e.printStackTrace();
        }

    }

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

    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        proyecciones_seleccionadas = new ArrayList<>();
    }

    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/proyeccion.xhtml");
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
            PdfPTable table = new PdfPTable(7);
            header(table);
            if (proyecciones != null)
                for (Proyeccion s : proyecciones) {
                    agregar_rows(table, s);
                }
            else {
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

    public void agregar_rows(PdfPTable table, Proyeccion s) {
        table.addCell(s.getCine());
        table.addCell(s.getCip() + "");
        table.addCell(s.getSala() + "");
        table.addCell(s.getRecaudacion() + "");
        table.addCell(s.getEspectadores() + "");
        table.addCell(s.getFecha_estreno() + "");
        table.addCell(s.getDias_estreno() + "");

    }

    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "proyecciones.pdf");
    }

    public List<Proyeccion> getProyecciones_seleccionadas() {
        return proyecciones_seleccionadas;
    }

    public void setProyecciones_seleccionadas(List<Proyeccion> proyecciones_seleccionadas) {
        this.proyecciones_seleccionadas = proyecciones_seleccionadas;
    }

    public List<Proyeccion> getProyecciones() {
        return proyecciones;
    }

    public void setProyecciones(List<Proyeccion> proyecciones) {
        this.proyecciones = proyecciones;
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

    public String getCine() {
        return cine;
    }

    public void setCine(String cine) {
        this.cine = cine;
    }

    public int getSala() {
        return sala;
    }

    public void setSala(int sala) {
        this.sala = sala;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getDias_estreno() {
        return dias_estreno;
    }

    public void setDias_estreno(int dias_estreno) {
        this.dias_estreno = dias_estreno;
    }

    public int getEspectadores() {
        return espectadores;
    }

    public void setEspectadores(int espectadores) {
        this.espectadores = espectadores;
    }

    public int getRecaudacion() {
        return recaudacion;
    }

    public void setRecaudacion(int recaudacion) {
        this.recaudacion = recaudacion;
    }
}
