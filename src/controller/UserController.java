package controller;

import config.Konfigurasi;
import model.UserModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    private Connection conn;

    public UserController() {
        this.conn = Konfigurasi.getConnection();
    }

    public UserModel login(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserModel(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error Login: " + e.getMessage());
        }
        return null; // Login gagal
    }
}