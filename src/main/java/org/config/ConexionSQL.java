package org.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQL {

    private static final String USER = "sa";
    private static final String PASSWORD = "P@ssw0rd";

    public static Connection conectar() throws SQLException {
        String sede = Config.sedeSeleccionada;
        if (sede == null || sede.isEmpty()) {
            throw new SQLException("La sede no ha sido seleccionada.");
        }

        String host;
        String base;

        if (sede.equalsIgnoreCase("Quito")) {
            host = "Localhost"; // ← IP del servidor donde está BQuito
            base = "BQuito1";
        } else {
            host = "localhost";
            base = "BGuayaquil1";
        }

        String url = "jdbc:sqlserver://" + host + ":1433;databaseName=" + base +
                ";encrypt=true;trustServerCertificate=true;";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(url, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC.", e);
        }
    }
}
