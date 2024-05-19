package co.edu.unbosque.services;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * El adaptador LocalDateAdapter convierte objetos LocalDate de y hacia JSON utilizando un formato específico.
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Escribe un objeto LocalDate en formato JSON.
     * 
     * @param out   El escritor JSON.
     * @param value El objeto LocalDate a escribir.
     * @throws IOException Si ocurre un error de E/S.
     */
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }

    /**
     * Lee un objeto LocalDate desde JSON.
     * 
     * @param in El lector JSON.
     * @return El objeto LocalDate leído.
     * @throws IOException Si ocurre un error de E/S.
     */
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        if (in.peek() == null) {
            return null;
        }
        String dateStr = in.nextString();
        return LocalDate.parse(dateStr, formatter);
    }
}
