package co.edu.unbosque.videospeli;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named("tm")
@ViewScoped
public class beanTM implements Serializable {
    private String message = "Hello, world!";

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
