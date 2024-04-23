package co.edu.unbosque.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import com.fasterxml.jackson.databind.JsonNode;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

@Singleton
@Startup
public class Funciones_SQL {
    private static final String URL = "jdbc:mysql://45.169.100.82:3306/nanotsky_sq_peliculas";
    private static final String USER = "nanotsky";
    private static final String PASSWORD = "4c[jRSt9y~ak";
    private static Connection con;
    @PostConstruct
    public void init() {
        conexion();
    }
    @PreDestroy
    public void destroy() {
        try {
            con.close();
            System.out.println("Conexión cerrada");
        } catch (Exception e) {
            System.err.println("Error al cerrar la base de datos");
            e.printStackTrace();
        }
    }
    public static void conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = java.sql.DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa");
        } catch (Exception e) {
            System.err.println("Error al conectar con la base de datos");
            e.printStackTrace();
        }
    }
    public static String llamar_metodo_json(String sql) {
        try {
            conexion();
            CallableStatement cs = con.prepareCall(sql);
            ResultSet rs = cs.executeQuery();
            return rs.getString(1);
        } catch (Exception e) {
            System.err.println("Error al ejecutar la consulta");
            e.printStackTrace();
        }
        return null;
    }
}
