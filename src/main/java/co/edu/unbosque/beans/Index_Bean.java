package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Pelicula;
import co.edu.unbosque.services.Funciones_SQL;
import co.edu.unbosque.services.Posters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

@Named("index")
@ViewScoped
public class Index_Bean implements Serializable {
    private List<Pelicula> peliculas;
    private int activeIndex = 0;
    private List<ResponsiveOption> responsiveOptions1;

    @PostConstruct
    public void init() {
        responsiveOptions1 = new ArrayList<>();
        responsiveOptions1.add(new ResponsiveOption("1024px", 5));
        responsiveOptions1.add(new ResponsiveOption("768px", 3));
        responsiveOptions1.add(new ResponsiveOption("560px", 1));
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

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public List<ResponsiveOption> getResponsiveOptions1() {
        return responsiveOptions1;
    }

    public void setResponsiveOptions1(List<ResponsiveOption> responsiveOptions1) {
        this.responsiveOptions1 = responsiveOptions1;
    }

    public void changeActiveIndex() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        this.activeIndex = Integer.valueOf(params.get("index"));
    }

    public String obtener_poster(String id, String value) {
        return Posters.obtener_poster(id, value);
    }
}
