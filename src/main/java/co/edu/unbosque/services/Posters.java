package co.edu.unbosque.services;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Posters {
    private static final String FILENAME = "src/main/java/co/edu/unbosque/persistence/util/poster.json";
    private static Map<String, String> posters;

    public static void Cargar() {

    }

    public static Map<String, String> leer_json(String jsonFilePath) {
        Map<String, String> posters = new HashMap<>();
        try {
            if (Files.exists(Paths.get(jsonFilePath))) {
                Gson gson = new Gson();
                BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath));
                Type type = new com.google.gson.reflect.TypeToken<Map<String, String>>() {
                }.getType();
                posters = gson.fromJson(reader, type);
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posters;
    }

    public static void agregar(String key, String value) {
        posters.put(key, value);
        actualizar();
    }

    public static String obtener_poster(String key) {
        return posters.get(key);
    }

    public static void borrar(String key) {
        posters.remove(key);
        actualizar();
    }

    public static void actualizar() {
        Gson gson = new Gson();
        String json = gson.toJson(posters);
        try {
            Files.write(Paths.get(FILENAME), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
