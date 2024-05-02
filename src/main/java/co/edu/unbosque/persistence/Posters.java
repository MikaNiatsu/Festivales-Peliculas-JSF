package co.edu.unbosque.persistence;

import com.google.gson.Gson;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Posters {
    private static Map<String, String> posters;


    public static void leer_json() {
        posters = new HashMap<>();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String filePath = externalContext.getRealPath("/util/poster.json");
        try {
            String jsonString = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type type = new com.google.gson.reflect.TypeToken<Map<String, String>>() {
            }.getType();
            posters = gson.fromJson(jsonString, type);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void agregar(String key, String value) {
        leer_json();
        posters.put(key, value);
        actualizar();
    }

    public static String obtener_poster(String key) {
        leer_json();
        return posters.getOrDefault(key, "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Image_not_available.png/640px-Image_not_available.png");
    }

    public static void borrar(String key) {
        leer_json();
        posters.remove(key);
        actualizar();
    }


    public static void actualizar() {
        Gson gson = new Gson();
        String json = gson.toJson(posters);
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String filePath = externalContext.getRealPath("/util/poster.json");
        try {
            FileUtils.writeStringToFile(new File(filePath), json, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


