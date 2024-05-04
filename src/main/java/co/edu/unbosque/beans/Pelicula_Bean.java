package co.edu.unbosque.beans;
import co.edu.unbosque.persistence.Pelicula;
import co.edu.unbosque.services.Posters;
import co.edu.unbosque.services.Descargador;
import co.edu.unbosque.services.Funciones_SQL;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public String getTitulo_p() {
        return titulo_p;
    }

    public void setTitulo_p(String titulo_p) {
        this.titulo_p = titulo_p;
    }

    public int getAno_produccion() {
        return ano_produccion;
    }

    public void setAno_produccion(int ano_produccion) {
        this.ano_produccion = ano_produccion;
    }

    public String getTitulo_s() {
        return titulo_s;
    }

    public void setTitulo_s(String titulo_s) {
        this.titulo_s = titulo_s;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    private List<Pelicula> peliculasSeleccionadas = new ArrayList<>();

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .create();
        Type type = new TypeToken<List<Pelicula>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_peliculas();");
            peliculas = gson.fromJson(json, type);
        } catch (Exception e) {
            peliculas = null;
            throw new RuntimeException(e);
        }
    }

    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = peliculas;
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

    public int getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(int presupuesto) {
        this.presupuesto = presupuesto;
    }

    public List<Pelicula> getPeliculasSeleccionadas() {
        return peliculasSeleccionadas;
    }

    public void setPeliculasSeleccionadas(List<Pelicula> peliculasSeleccionadas) {
        this.peliculasSeleccionadas = peliculasSeleccionadas;
    }

    public void crear() {

        try {
            if (titulo_p == null || titulo_p.isEmpty() || titulo_p.length() > 44) {
                FacesContext.getCurrentInstance().addMessage("titulo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Titulo inválido"));
                return;
            }
            if (ano_produccion <= 0 || (ano_produccion + "").length() > 4) {
                FacesContext.getCurrentInstance().addMessage("ano_produccion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ano inválido"));
                return;
            }
            if (titulo_s == null || titulo_s.isEmpty() || titulo_s.length() > 44) {
                FacesContext.getCurrentInstance().addMessage("titulo_s", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Titulo inválido"));
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
            if (duracion <= 0 || (duracion + "").length() > 3) {
                FacesContext.getCurrentInstance().addMessage("duracion", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Duración inválida"));
                return;
            }
            for (Pelicula p : peliculas) {
                if (p.getTitulo_p().equals(titulo_p)) {
                    FacesContext.getCurrentInstance().addMessage("titulo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Pelicula ya existente"));
                    return;
                }
            }
            String cip = (Integer.parseInt(Objects.requireNonNull(Funciones_SQL.llamar_metodo_json("CALL id_alto_pelicula();"))) + 1) + "";
            titulo_p = titulo_p.replace("'", "''").replace("\"", "''");
            titulo_s = titulo_s.replace("'", "''").replace("\"", "''");
            nacionalidad = nacionalidad.replace("'", "''").replace("\"", "''");
            Funciones_SQL.llamar_metodo(String.format("CALL crear_pelicula('%s','%s', %s, '%s', '%s', %s, %s);", cip, titulo_p, ano_produccion, titulo_s, nacionalidad, presupuesto, duracion));
            if (!url.isEmpty() && !url.equals(" "))
                Posters.agregar(cip, url);
            refrescar_pagina();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String obtener_poster(String id, String value) {
        return Posters.obtener_poster(id, value);
    }

    public void toggleSelected(Pelicula pel) {
        if (peliculasSeleccionadas.contains(pel)) {
            peliculasSeleccionadas.remove(pel);
        } else {
            peliculasSeleccionadas.add(pel);
        }
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
    public String titulo_pelicula(Pelicula pel) {
        return pel.getTitulo_p().replace("'", " ").replace("\"", " ");
    }
    public void actualizar_pelicula(Pelicula pel) {
        try {
            if (pel.getTitulo_p() == null || pel.getTitulo_p().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("titulo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Titulo inválido"));
                return;
            }
            if (pel.getTitulo_p().length() > 44) {
                FacesContext.getCurrentInstance().addMessage("titulo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Titulo demasiado extenso  "));
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
                FacesContext.getCurrentInstance().addMessage("titulo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Titulo demasiado extenso  "));
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

    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        peliculasSeleccionadas = new ArrayList<>();
    }

    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/pelicula.xhtml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] generar_pdf(List<Pelicula> peliculas) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfPTable table = new PdfPTable(7);
            header(table);
            for (Pelicula p : peliculas) {
                agregar_rows(table, p);
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        return outputStream.toByteArray();
    }

    public void header(PdfPTable table) {
        String[] headers = {"CIP", "Tiulo Original", "Ano de Producción", "Titulo Español", "Nacionalidad", "Presupuesto", "Duración"};
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

    public void agregar_rows(PdfPTable table, Pelicula p) {
        table.addCell(p.getCip());
        table.addCell(p.getTitulo_p());
        table.addCell(p.getAno_produccion() + "");
        table.addCell(p.getTitulo_s());
        table.addCell(p.getNacionalidad());
        table.addCell(p.getPresupuesto() + "");
        table.addCell(p.getDuracion() + "");
    }

    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(peliculas), "peliculas.pdf");
    }
}
