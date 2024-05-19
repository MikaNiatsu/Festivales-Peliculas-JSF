package co.edu.unbosque.services;

import com.google.gson.Gson;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;
import org.primefaces.shaded.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * La clase Posters maneja la lectura, escritura y actualización de un archivo JSON que contiene la información de los posters de películas.
 */
public class Posters {
    private static Map<String, String> posters;

    /**
     * Lee el archivo JSON que contiene los posters y carga su contenido en un mapa.
     */
    public static void leer_json() {
        posters = new HashMap<>();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String filePath = externalContext.getRealPath("/util/poster.json");
        if (!new File(filePath).exists())
            try {
                FileUtils.writeStringToFile(new File(filePath), "{}", "UTF-8");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    /**
     * Agrega un nuevo poster al mapa y actualiza el archivo JSON.
     *
     * @param key   La clave del poster.
     * @param value El valor del poster (URL del poster).
     */
    public static void agregar(String key, String value) {
        leer_json();
        posters.put(key, value);
        actualizar();
    }

    /**
     * Obtiene el URL del poster de una película. Si el poster no está en el mapa, consulta la API OMDB para obtener el URL y lo agrega al mapa.
     *
     * @param key   La clave del poster.
     * @param value El título de la película.
     * @return El URL del poster.
     */
    public static String obtener_poster(String key, String value) {
        leer_json();
        if (!posters.containsKey(key)) {
            OkHttpClient client = new OkHttpClient();
            value = value.replaceAll("'", "%27");
            String url = "http://www.omdbapi.com/?t=" + value + "&apikey=b559dcfa";
            url = url.replace(" ", "+");
            url = url.replaceAll("[\\s\\t\\r\\n]", "");
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
                assert response.body() != null;
                String json = response.body().string();
                JSONObject jsonObject = new JSONObject(json);
                if (jsonObject.get("Response").equals("True")) {
                    String poster = (String) jsonObject.get("Poster");
                    poster = poster.replace("N/A", "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Image_not_available.png/640px-Image_not_available.png");
                    agregar(key, poster);
                    return poster;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return posters.getOrDefault(key, "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Image_not_available.png/640px-Image_not_available.png");
    }

    /**
     * Elimina un poster del mapa y actualiza el archivo JSON.
     *
     * @param key La clave del poster a eliminar.
     */
    public static void borrar(String key) {
        leer_json();
        posters.remove(key);
        actualizar();
    }

    /**
     * Actualiza el archivo JSON con el contenido del mapa de posters.
     */
    public static void actualizar() {
        Gson gson = new Gson();
        String json = gson.toJson(posters);
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String filePath = externalContext.getRealPath("/util/poster.json");
        if (!new File(filePath).exists())
            try {
                FileUtils.writeStringToFile(new File(filePath), "{}", "UTF-8");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        try {
            FileUtils.writeStringToFile(new File(filePath), json, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
