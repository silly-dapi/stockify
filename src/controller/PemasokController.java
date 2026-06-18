package controller;

import config.Konfigurasi;
import model.PemasokModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PemasokController {
    private Connection conn;

    public PemasokController() {
        this.conn = Konfigurasi.getConnection();
    }

    public List<PemasokModel> getAllPemasok() {
        List<PemasokModel> list = new ArrayList<>();
        String sql = "SELECT * FROM pemasok";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new PemasokModel(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("alamat"),
                        rs.getString("telepon")));
            }
        } catch (SQLException e) {
            System.err.println("Error Ambil Pemasok: " + e.getMessage());
        }
        return list;
    }

    public boolean insertPemasok(PemasokModel pemasok) {
        String sql = "INSERT INTO pemasok(nama, alamat, telepon) VALUES(?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pemasok.getNama());
            ps.setString(2, pemasok.getAlamat());
            ps.setString(3, pemasok.getTelepon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Tambah Pemasok: " + e.getMessage());
            return false;
        }
    }

    public int getTotalSupplier() {
        String sql = "SELECT COUNT(*) FROM pemasok";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error Hitung Total Pemasok: " + e.getMessage());
        }
        return 0;
    }

    public boolean updatePemasok(PemasokModel pemasok) {
        String sql = "UPDATE pemasok SET nama=?, alamat=?, telepon=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pemasok.getNama());
            ps.setString(2, pemasok.getAlamat());
            ps.setString(3, pemasok.getTelepon());
            ps.setInt(4, pemasok.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Pemasok: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePemasok(int id) {
        String sql = "DELETE FROM pemasok WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Hapus Pemasok: " + e.getMessage());
            return false;
        }
    }
}