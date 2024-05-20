package co.edu.unbosque.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * La clase Funciones_SQL proporciona métodos para ejecutar consultas SQL en la base de datos.
 */
public class Funciones_SQL {
    private static final String URL = "jdbc:mysql://45.169.100.82:3306/nanotsky_sq_peliculas";
    private static final String USER = "nanotsky_peliculas";
    private static final String PASSWORD = "4c[jRSt9y~ak";
    // static final String URL = "jdbc:mysql://192.168.56.127:3306/sq_peliculas";
    // private static final String USER = "remoto";
    // private static final String PASSWORD = "123456";
    private static Connection con;

    /**
     * Establece una conexión con la base de datos.
     */
    public static void conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = java.sql.DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.err.println("Error al conectar con la base de datos");
            e.printStackTrace();
        }
    }

    /**
     * Ejecuta una consulta SQL que devuelve un resultado en formato JSON.
     *
     * @param sql La consulta SQL a ejecutar.
     * @return El resultado de la consulta en formato JSON.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
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

    /**
     * Ejecuta una consulta SQL que no devuelve un resultado.
     *
     * @param sql La consulta SQL a ejecutar.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    public static void llamar_metodo(String sql) throws SQLException {
        conexion();
        try {
            Statement cs = con.createStatement();
            cs.executeUpdate(sql);
        } catch (Exception e) {
            System.err.println("Error al ejecutar la consulta");
            e.printStackTrace();
        } finally {
            con.close();
        }
    }
}
