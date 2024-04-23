package co.edu.unbosque.beans;

import co.edu.unbosque.persistence.Festival;
import co.edu.unbosque.services.Funciones_SQL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.util.List;

@Named("festival")
@RequestScoped
public class Festival_Bean {
    private List<Festival> festivales;

    @PostConstruct
        public void init(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Festival> festivales = mapper.readValue(Funciones_SQL.llamar_metodo_json("SELECT obtenerFestivales();"), new TypeReference<List<Festival>>() {});
        } catch (JsonProcessingException e) {
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
