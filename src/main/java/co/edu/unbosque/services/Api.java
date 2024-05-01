package co.edu.unbosque.services;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Api {
    public String getPelicula(String id) {
        try {
            URL url = new URL("http://www.omdbapi.com/?i=" + id + "&apikey=b559dcfa");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JsonElement jsonElement = JsonParser.parseString(response.toString());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String result = jsonObject.get("Title").getAsString() + "¡" + jsonObject.get("Year").getAsString() + jsonObject.get("runtime").getAsString().replace(" min", "") + "!" + jsonObject.get("Production").getAsString();
            con.disconnect();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
