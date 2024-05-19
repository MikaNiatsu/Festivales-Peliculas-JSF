package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Sala;
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

@Named("sala")
@ViewScoped
public class Sala_Bean implements Serializable {

    private List<Sala> salas_seleccionadas = new ArrayList<>();
    private List<Sala> salas;
    private boolean esEditable = false;
    private boolean esEliminar = false;
    private boolean esAccion = false;
    private String cine = "";
    private int sala = 0;
    private int aforo = 0;
    private List<String> salas_st = new ArrayList<>();
    private List<String> cines = new ArrayList<>();

    /**
     * Inicializa la lista de salas al cargar el bean.
     * Utiliza Gson para deserializar la respuesta JSON de la base de datos en una lista de objetos Sala.
     * Registra un adaptador para manejar la deserialización de objetos LocalDate.
     */
    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Sala>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_sala();");
            salas = gson.fromJson(json, type);
        } catch (Exception e) {
            salas = null;
            e.printStackTrace();
        }
    }

    /**
     * Alterna la selección de una sala en la lista de salas seleccionadas.
     * Si la sala está seleccionada, se elimina de la lista; si no, se añade a la lista.
     *
     * @param sal La sala que se va a alternar en la selección.
     */
    public void toggleSelected(Sala sal) {
        if (salas_seleccionadas.contains(sal)) {
            salas_seleccionadas.remove(sal);
        } else {
            salas_seleccionadas.add(sal);
        }
    }

    /**
     * Crea una nueva sala.
     * Verifica que el número de sala y el aforo estén dentro de los rangos válidos.
     * Verifica que no exista ya una sala con el mismo número en el mismo cine.
     * Muestra mensajes de error si las validaciones fallan.
     * Realiza una llamada a la base de datos para crear la nueva sala.
     */
    public void crear() {
        if (sala < 1 || sala > 99) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Numero de sala no valido (entre 1 y 99)"));
            return;
        }
        if (aforo < 1 || aforo > 999) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Aforo no valido (entre 1 y 999)"));
            return;
        }
        for (Sala s : salas_seleccionadas) {
            if (s.getSala() == sala && s.getCine().equals(cine)) {
                FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una sala en ese cine con ese numero"));
                return;
            }
        }
        try {
            Funciones_SQL.llamar_metodo(String.format("CALL crear_sala('%s', '%s', '%s');", cine, sala, aforo));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear sala"));
            e.printStackTrace();
        }
        refrescar_pagina();
    }

    /**
     * Inicia la acción de eliminar una sala, estableciendo los indicadores correspondientes.
     * Establece esAccion y esEliminar a true.
     */
    public void eliminar() {
        esAccion = true;
        esEliminar = true;
    }

    /**
     * Inicia la acción de editar una sala, estableciendo los indicadores correspondientes.
     * Establece esAccion y esEditable a true.
     */
    public void editar() {
        esAccion = true;
        esEditable = true;
    }

    /**
     * Guarda los cambios realizados en la sala editada.
     * Establece esAccion y esEditable a false.
     */
    public void guardar_editado() {
        esAccion = false;
        esEditable = false;
    }

    /**
     * Actualiza la información de una sala específica.
     * Verifica que el número de sala y el aforo estén dentro de los rangos válidos.
     * Muestra mensajes de error si las validaciones fallan.
     * Realiza una llamada a la base de datos para actualizar la sala.
     *
     * @param sal La sala que se va a actualizar.
     */
    public void actualizar(Sala sal) {
        if (sal.getSala() < 1 || sal.getSala() > 99) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Numero de sala no valido (entre 1 y 99)"));
            return;
        }
        if (sal.getAforo() < 1 || sal.getAforo() > 999) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Aforo no valido (entre 1 y 999)"));
            return;
        }

        try {
            Funciones_SQL.llamar_metodo(String.format("CALL actualizar_sala('%s','%s','%s');", sal.getCine(), sal.getSala(), sal.getAforo()));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al actualizar sala"));
            e.printStackTrace();
        }
    }

    /**
     * Confirma la eliminación de las salas seleccionadas.
     * Establece esAccion y esEliminar a false.
     * Realiza una llamada a la base de datos para eliminar cada una de las salas seleccionadas.
     * Muestra mensajes de error si ocurre algún problema durante la eliminación.
     * Refresca la página después de la operación.
     */
    public void confirmar_eliminar() {
        esAccion = false;
        esEliminar = false;
        for (Sala s : salas_seleccionadas) {
            try {
                Funciones_SQL.llamar_metodo(String.format("CALL eliminar_sala('%s');", s.getCine()));
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("sala", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar sala"));
                e.printStackTrace();
            }
        }
        refrescar_pagina();
    }

    /**
     * Cancela la acción de eliminación de salas, restableciendo los indicadores y limpiando la lista de salas seleccionadas.
     * Establece esAccion y esEliminar a false.
     * Limpia la lista de salas_seleccionadas.
     */
    public void cancelar_eliminar() {
        esAccion = false;
        esEliminar = false;
        salas_seleccionadas = new ArrayList<>();
    }

    /**
     * Refresca la página actual redirigiendo al usuario a la misma página.
     * Si ocurre un error durante la redirección, se muestra un mensaje de error.
     */
    public void refrescar_pagina() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/sala.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera un archivo PDF con la información de las salas.
     * Crea un documento PDF con una tabla que contiene los datos de las salas.
     * Muestra un mensaje de error si no hay registros para mostrar.
     *
     * @return Un arreglo de bytes que representa el contenido del PDF generado.
     */
    public byte[] generar_pdf() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            PdfPTable table = new PdfPTable(3);
            header(table);
            if (salas != null) {
                for (Sala s : salas) {
                    agregar_rows(table, s);
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
     * Agrega los encabezados de columna a la tabla del PDF.
     * Define los encabezados "Cine", "Sala" y "Aforo" y los agrega a la tabla con estilo.
     *
     * @param table La tabla a la que se agregarán los encabezados.
     */
    public void header(PdfPTable table) {
        String[] headers = {"Cine", "Sala", "Aforo"};
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
     * Agrega una fila a la tabla del PDF con los datos de una sala específica.
     *
     * @param table La tabla a la que se agregarán las filas.
     * @param s     La sala cuyos datos se agregarán como una fila.
     */
    public void agregar_rows(PdfPTable table, Sala s) {
        table.addCell(s.getCine());
        table.addCell(String.valueOf(s.getSala()));
        table.addCell(String.valueOf(s.getAforo()));
    }

    /**
     * Descarga un archivo PDF con la información de las salas.
     * Utiliza la clase Descargador para generar y descargar el PDF.
     */
    public void descargar_pdf() {
        Descargador.descargar_pdf(generar_pdf(), "salas.pdf");
    }

    /**
     * Devuelve la lista de salas seleccionadas.
     *
     * @return La lista de salas seleccionadas.
     */
    public List<Sala> getSalas_seleccionadas() {
        return salas_seleccionadas;
    }

    /**
     * Establece la lista de salas seleccionadas.
     *
     * @param salas_seleccionadas La nueva lista de salas seleccionadas.
     */
    public void setSalas_seleccionadas(List<Sala> salas_seleccionadas) {
        this.salas_seleccionadas = salas_seleccionadas;
    }

    /**
     * Devuelve la lista de todas las salas.
     *
     * @return La lista de todas las salas.
     */
    public List<Sala> getSalas() {
        return salas;
    }

    /**
     * Establece la lista de todas las salas.
     *
     * @param salas La nueva lista de todas las salas.
     */
    public void setSalas(List<Sala> salas) {
        this.salas = salas;
    }

    /**
     * Indica si la información de las salas es editable.
     *
     * @return true si es editable, false en caso contrario.
     */
    public boolean isEsEditable() {
        return esEditable;
    }

    /**
     * Establece si la información de las salas es editable.
     *
     * @param esEditable true si es editable, false en caso contrario.
     */
    public void setEsEditable(boolean esEditable) {
        this.esEditable = esEditable;
    }

    /**
     * Indica si la acción actual es de eliminación.
     *
     * @return true si es de eliminación, false en caso contrario.
     */
    public boolean isEsEliminar() {
        return esEliminar;
    }

    /**
     * Establece si la acción actual es de eliminación.
     *
     * @param esEliminar true si es de eliminación, false en caso contrario.
     */
    public void setEsEliminar(boolean esEliminar) {
        this.esEliminar = esEliminar;
    }

    /**
     * Indica si hay alguna acción en curso.
     *
     * @return true si hay alguna acción en curso, false en caso contrario.
     */
    public boolean isEsAccion() {
        return esAccion;
    }

    /**
     * Establece si hay alguna acción en curso.
     *
     * @param esAccion true si hay alguna acción en curso, false en caso contrario.
     */
    public void setEsAccion(boolean esAccion) {
        this.esAccion = esAccion;
    }

    /**
     * Devuelve el nombre del cine.
     *
     * @return El nombre del cine.
     */
    public String getCine() {
        return cine;
    }

    /**
     * Establece el nombre del cine.
     *
     * @param cine El nuevo nombre del cine.
     */
    public void setCine(String cine) {
        this.cine = cine;
    }

    /**
     * Devuelve el número de la sala.
     *
     * @return El número de la sala.
     */
    public int getSala() {
        return sala;
    }

    /**
     * Establece el número de la sala.
     *
     * @param sala El nuevo número de la sala.
     */
    public void setSala(int sala) {
        this.sala = sala;
    }

    /**
     * Devuelve el aforo de la sala.
     *
     * @return El aforo de la sala.
     */
    public int getAforo() {
        return aforo;
    }

    /**
     * Establece el aforo de la sala.
     *
     * @param aforo El nuevo aforo de la sala.
     */
    public void setAforo(int aforo) {
        this.aforo = aforo;
    }

    /**
     * Devuelve una lista de los números de sala como cadenas de texto.
     *
     * @return Una lista de los números de sala.
     */
    public List<String> getSalas_st() {
        salas_st = new ArrayList<>();
        for (Sala s : salas) {
            salas_st.add(String.valueOf(s.getSala()));
        }
        return salas_st;
    }

    /**
     * Establece una lista de los números de sala como cadenas de texto.
     *
     * @param salas_st La nueva lista de números de sala.
     */
    public void setSalas_st(List<String> salas_st) {
        this.salas_st = salas_st;
    }

    /**
     * Recupera una lista de representaciones de cadena de los números de sala (room) para un nombre de cine (cinema) dado.
     *
     * @param cine el nombre del cine
     * @return una lista de representaciones de cadena de los números de sala para el cine dado
     */
    public List<String> getSalas_Cine(String cine) {
        salas_st = new ArrayList<>();
        for (Sala s : salas) {
            if (s.getCine().equals(cine))
                salas_st.add(String.valueOf(s.getSala()));
        }
        return salas_st;
    }

    /**
     * Devuelve una lista de los nombres de cines.
     *
     * @return Una lista de los nombres de cines.
     */
    public List<String> getCines() {
        cines = new ArrayList<>();
        for (Sala s : salas) {
            cines.add(s.getCine());
        }
        return cines;
    }

    /**
     * Establece una lista de los nombres de cines.
     *
     * @param cines La nueva lista de nombres de cines.
     */
    public void setCines(List<String> cines) {
        this.cines = cines;
    }
}
