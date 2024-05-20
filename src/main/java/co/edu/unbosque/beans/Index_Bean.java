package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Pelicula;
import co.edu.unbosque.services.Funciones_SQL;
import co.edu.unbosque.services.Posters;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.model.ResponsiveOption;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Named("index")
@ViewScoped
public class Index_Bean implements Serializable {
    private List<Pelicula> peliculas;
    private int activeIndex = 0;
    private List<ResponsiveOption> responsiveOptions1;
    private String nombre = "";
    private String pelicula = "";
    private String premio = "";
    private String certamen = "";
    private String festival = "";
    private String mensaje;
    private JsonArray jsonArray;
    private List<String> mensajes = new ArrayList<>();
    private int indiceActual = 0;

    /**
     * Inicializa el bean.
     */
    @PostConstruct
    public void init() {
        responsiveOptions1 = new ArrayList<>();
        responsiveOptions1.add(new ResponsiveOption("1024px", 5));
        responsiveOptions1.add(new ResponsiveOption("768px", 3));
        responsiveOptions1.add(new ResponsiveOption("560px", 1));
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<List<Pelicula>>() {
        }.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("CALL obtener_peliculas();");
            peliculas = gson.fromJson(json, type);
            jsonArray = JsonParser.parseString(Funciones_SQL.llamar_metodo_json("CALL obtener_informe();")).getAsJsonArray();
        } catch (Exception e) {
            peliculas = null;
            throw new RuntimeException(e);
        }
        actualizarMensaje();
    }

    /**
     * Devuelve el valor del campo mensaje.
     *
     * @return el valor de mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el valor del campo mensaje.
     *
     * @param mensaje el nuevo valor para el campo mensaje
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Actualiza el mensaje basado en los valores de las variables de instancia.
     * <p>
     * Este método concatena los valores de las variables de instancia `nombre`, `pelicula`, `premio`, `certamen`
     * y `festival` en una sola cadena de texto. La cadena resultante se almacena en la variable de instancia `mensaje`.
     * <p>
     * Si alguna de las variables de instancia no está vacía, se agrega el etiqueta correspondiente y su valor al mensaje
     * de cadena. Las etiquetas están en español y están precedidas por el nombre correspondiente de la variable de instancia.
     * <p>
     * La cadena resultante se trunca para eliminar cualquier espacio en blanco inicial o final.
     */
    public void actualizarMensaje() {
        StringBuilder sb = new StringBuilder();
        for (JsonElement element : jsonArray) {
            sb.setLength(0); // Limpiar el StringBuilder
            if (element.getAsJsonObject().has("nombre_persona") && !element.getAsJsonObject().get("nombre_persona").isJsonNull()) {
                sb.append("<strong>").append(element.getAsJsonObject().get("nombre_persona").getAsString() + "</strong>");
            }
            if (element.getAsJsonObject().has("titulo_p") && !element.getAsJsonObject().get("titulo_p").isJsonNull()) {
                sb.append(" participó en la película ").append("<strong>").append(element.getAsJsonObject().get("titulo_p").getAsString() + "</strong>");
            }
            if (element.getAsJsonObject().has("premio") && !element.getAsJsonObject().get("premio").isJsonNull()) {
                sb.append(" con el premio ").append("<strong>").append(element.getAsJsonObject().get("premio").getAsString() + "</strong>");
            }
            if (element.getAsJsonObject().has("certamen") && !element.getAsJsonObject().get("certamen").isJsonNull()) {
                sb.append(" en el certamen de ").append("<strong>").append(element.getAsJsonObject().get("certamen").getAsString() + "</strong>");
            }
            if (element.getAsJsonObject().has("festival") && !element.getAsJsonObject().get("festival").isJsonNull()) {
                sb.append(" en el festival ").append("<strong>").append(element.getAsJsonObject().get("festival").getAsString() + "</strong>");
            }
            mensajes.add(sb.toString().trim());
        }
    }

    /**
     * Obtiene el mensaje actual de la lista de mensajes. Si la lista está vacía, devuelve una cadena vacía.
     * El mensaje actual se obtiene en el índice actual y luego se actualiza el índice actual.
     *
     * @return El mensaje actual de la lista de mensajes.
     */
    public String getMensajeActual() {
        if (mensajes.isEmpty()) {
            return "";
        }
        String mensajeActual = mensajes.get(indiceActual);
        indiceActual = ((indiceActual + 1) + new Random().nextInt(3)) % mensajes.size();
        return mensajeActual;
    }

    /**
     * Obtiene la lista de películas.
     *
     * @return La lista de películas.
     */
    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    /**
     * Establece la lista de películas.
     *
     * @param peliculas La lista de películas a establecer.
     */
    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = peliculas;
    }

    /**
     * Obtiene el índice activo.
     *
     * @return El índice activo.
     */
    public int getActiveIndex() {
        return activeIndex;
    }

    /**
     * Establece el índice activo.
     *
     * @param activeIndex El índice activo a establecer.
     */
    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    /**
     * Obtiene las opciones de respuesta responsivas.
     *
     * @return Las opciones de respuesta responsivas.
     */
    public List<ResponsiveOption> getResponsiveOptions1() {
        return responsiveOptions1;
    }

    /**
     * Establece las opciones de respuesta responsivas.
     *
     * @param responsiveOptions1 Las opciones de respuesta responsivas a establecer.
     */
    public void setResponsiveOptions1(List<ResponsiveOption> responsiveOptions1) {
        this.responsiveOptions1 = responsiveOptions1;
    }

    /**
     * Cambia el índice activo.
     */
    public void changeActiveIndex() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        this.activeIndex = Integer.valueOf(params.get("index"));
    }

    /**
     * Obtiene el póster de una película.
     *
     * @param id    El ID de la película.
     * @param value El valor del póster.
     * @return El póster de la película.
     */
    public String obtener_poster(String id, String value) {
        return Posters.obtener_poster(id, value);
    }

    /**
     * Devuelve el valor de la propiedad "nombre".
     *
     * @return el valor de la propiedad "nombre"
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el valor de la propiedad "nombre".
     *
     * @param nombre el nuevo valor de la propiedad "nombre"
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el valor de la propiedad "pelicula".
     *
     * @return el valor de la propiedad "pelicula"
     */
    public String getPelicula() {
        return pelicula;
    }

    /**
     * Establece el valor de la propiedad "pelicula".
     *
     * @param pelicula el nuevo valor de la propiedad "pelicula"
     */
    public void setPelicula(String pelicula) {
        this.pelicula = pelicula;
    }

    /**
     * Devuelve el valor de la propiedad "premio".
     *
     * @return el valor de la propiedad "premio"
     */
    public String getPremio() {
        return premio;
    }

    /**
     * Establece el valor de la propiedad "premio".
     *
     * @param premio el nuevo valor de la propiedad "premio"
     */
    public void setPremio(String premio) {
        this.premio = premio;
    }

    /**
     * Devuelve el valor de la propiedad "certamen".
     *
     * @return el valor de la propiedad "certamen"
     */
    public String getCertamen() {
        return certamen;
    }

    /**
     * Establece el valor de la propiedad "certamen".
     *
     * @param certamen el nuevo valor de la propiedad "certamen"
     */
    public void setCertamen(String certamen) {
        this.certamen = certamen;
    }

    /**
     * Devuelve el valor de la propiedad "festival".
     *
     * @return el valor de la propiedad "festival"
     */
    public String getFestival() {
        return festival;
    }

    /**
     * Establece el valor de la propiedad "festival".
     *
     * @param festival el nuevo valor de la propiedad "festival"
     */
    public void setFestival(String festival) {
        this.festival = festival;
    }
}
