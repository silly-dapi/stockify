package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Konfigurasi {
    private static final String URL = "jdbc:sqlite:stockify.db";
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(URL);
                System.out.println("Koneksi ke database Stockify berhasil!");
            } catch (ClassNotFoundException | SQLException e) {
                System.err.println("Gagal koneksi database: " + e.getMessage());
            }
        }
        return connection;
    }
}