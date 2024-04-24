package co.edu.unbosque.services;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Funciones_SQL {
    private static final String URL = "jdbc:mysql://45.169.100.82:3306/nanotsky_sq_peliculas";
    private static final String USER = "nanotsky_peliculas";
    private static final String PASSWORD = "4c[jRSt9y~ak";
    private static Connection con;

    public static void conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = java.sql.DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.err.println("Error al conectar con la base de datos");
            e.printStackTrace();
        }
    }
    public static String llamar_metodo_json(String sql) throws SQLException {
        conexion();
        try {
            Statement cs = con.createStatement();
            ResultSet rs = cs.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error al ejecutar la consulta");
            e.printStackTrace();
        } finally {
            con.close();
        }
        return null;
    }
}
