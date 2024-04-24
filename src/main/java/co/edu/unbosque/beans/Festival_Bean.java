package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Festival;
import co.edu.unbosque.services.Funciones_SQL;
import co.edu.unbosque.services.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

@Named("festival")
@RequestScoped
public class Festival_Bean {
    private List<Festival> festivales;

    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Type type = new TypeToken<List<Festival>>() {}.getType();
        try {
            String json = Funciones_SQL.llamar_metodo_json("SELECT obtenerFestivales();");
            festivales = gson.fromJson(json, type);
        }catch (Exception e){
            festivales = null;
            e.printStackTrace();
        }
    }

    public List<Festival> getFestivales() {
        return festivales;
    }

    public void setFestivales(List<Festival> festivales) {
        this.festivales = festivales;
    }
}
