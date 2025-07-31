package org.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQL {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=BQuito;encrypt=true;trustServerCertificate=true;";


    private static final String USER = "sa";
    private static final String PASSWORD = "P@ssw0rd";

    public static Connection conectar() throws SQLException {
        String sede = Config.sedeSeleccionada;
        if (sede == null || sede.isEmpty()) {
            throw new SQLException("La sede no ha sido seleccionada.");
        }

        String base = sede.equalsIgnoreCase("Quito") ? "BQuito" : "BGUAYAQUIL";

        String url = "jdbc:sqlserver://localhost:1433;databaseName=" + base +
                     ";encrypt=true;trustServerCertificate=true;";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(url, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC.", e);
        }
    }
    
}
