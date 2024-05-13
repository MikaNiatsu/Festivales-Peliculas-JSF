package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Personaje;
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

@Named("personaje")
@ViewScoped
public class Persona_Bean implements Serializable {
    private List<Personaje> personajes;
    private List<Personaje> personajes_seleccionadas = new ArrayList<>();
    private ArrayList<String> nombres = new ArrayList<>();
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String nacionalidad = "";
    private char sexo;
    private String nombre = "";

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Personaje>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_personaje();");
            personajes = gson.fromJson(json, type);

        } catch (Exception e) {
            personajes = null;
            e.printStackTrace();
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public void toggleSelected(Personaje per) {
        if (personajes_seleccionadas.contains(per)) {
            personajes_seleccionadas.remove(per);
        } else {
            personajes_seleccionadas.add(per);
        }
    }

    public void crear() {
        try {
            if (nacionalidad.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nacionalidad inválida"));
                return;
            }
            if (nombre.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nombre inválido"));
                return;
            }
            if (nacionalidad.length() > 15) {
                FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nacionalidad muy grande"));
                return;
            }
            if (nombre.length() > 15) {
                FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nombre muy grande"));
                return;
            }
            Funciones_SQL.llamar_metodo(String.format("CALL crear_personaje('%s', '%s', '%s');", nombre, nacionalidad, sexo));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Personaje inválida"));
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

    public void actualizar(Personaje per) {
        if (nacionalidad.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nacionalidad inválida"));
            return;
        }
        if (nacionalidad.length() > 15) {
            FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nacionalidad muy grande"));
            return;
        }
        if (nombre.length() > 15) {
            FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nombre muy grande"));
            return;
        }
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_personaje('%s','%s', '%s');", per.getNombre_persona(), per.getNacionalidad_persona(), per.getSexo_persona()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Personaje inválida"));
            e.printStackTrace();
        }
    }

    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Personaje p : personajes_seleccionadas) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_personaje('%s');", p.getNombre_persona()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Personaje inválida"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        personajes_seleccionadas = new ArrayList<>();
    }

    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/persona.xhtml");
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
            if (personajes == null) {
                FacesContext.getCurrentInstance().addMessage("personaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay registros para mostrar"));
                return null;
            } else {
                for (Personaje p : personajes) {
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

    public void agregar_rows(PdfPTable table, Personaje p) {
        table.addCell(p.getNombre_persona());
        table.addCell(p.getNacionalidad_persona());
        table.addCell(p.getSexo_persona() + "");
    }

    public void header(PdfPTable table) {
        String[] headers = {"Nombre", "Nacionalidad", "Sexo"};
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

    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "personajes.pdf");
    }

    public List<Personaje> getPersonajes() {
        return personajes;
    }

    public void setPersonajes(List<Personaje> personajes) {
        this.personajes = personajes;
    }

    public List<Personaje> getPersonajes_seleccionadas() {
        return personajes_seleccionadas;
    }

    public void setPersonajes_seleccionadas(List<Personaje> personajes_seleccionadas) {
        this.personajes_seleccionadas = personajes_seleccionadas;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public char getSexo() {
        return sexo;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }

    public ArrayList<String> getNombres() {
        nombres = new ArrayList<>();
        for (Personaje p : personajes) {
            nombres.add(p.getNombre_persona());
        }
        return nombres;
    }

    public void setNombres(ArrayList<String> nombres) {
        this.nombres = nombres;
    }
}