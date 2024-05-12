package co.edu.unbosque.services;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@FacesConverter("localDateConverter")
public class LocalDateConverter implements Converter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("fecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha inválida poniendo fecha temporal a 1999-01-01"));
            return LocalDate.parse("1999-01-01", formatter);
        }
        try {
            return LocalDate.parse(value, formatter);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("fecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha inválida poniendo fecha temporal a 1999-01-01"));
            return LocalDate.parse("1999-01-01", formatter);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            FacesContext.getCurrentInstance().addMessage("fecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha inválida poniendo fecha temporal a 1999-01-01"));
            return "1999-01-01";
        }
        try {
            return ((LocalDate) value).format(formatter);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("fecha", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha inválida poniendo fecha temporal a 1999-01-01"));
            return "1999-01-01";
        }
    }

}
