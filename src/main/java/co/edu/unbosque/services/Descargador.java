package co.edu.unbosque.services;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * La clase Descargador proporciona un método estático para descargar archivos PDF.
 */
public class Descargador {

    /**
     * Descarga un archivo PDF con el contenido especificado y el nombre de archivo proporcionado.
     * 
     * @param contenido      El contenido del archivo PDF como un arreglo de bytes.
     * @param nombreArchivo  El nombre del archivo PDF.
     */
    public static void descargar_pdf(byte[] contenido, String nombreArchivo) {
        if (contenido == null) {
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

        response.reset();
        response.setContentType("application/pdf");
        response.setContentLength(contenido.length);
        response.setHeader("Content-disposition", "attachment; filename=" + nombreArchivo);

        try (InputStream input = new ByteArrayInputStream(contenido)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                response.getOutputStream().write(buffer, 0, length);
            }
            response.getOutputStream().flush();
            facesContext.responseComplete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
