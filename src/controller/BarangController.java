package controller;

import config.Konfigurasi;
import model.BarangModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangController {
    private Connection conn;

    public BarangController() {
        this.conn = Konfigurasi.getConnection();
    }

    public List<BarangModel> getAllBarang() {
        List<BarangModel> list = new ArrayList<>();
        String sql = "SELECT * FROM barang";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new BarangModel(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getInt("stok"),
                        rs.getDouble("harga_beli"),
                        rs.getDouble("harga_jual")));
            }
        } catch (SQLException e) {
            System.err.println("Error Ambil Barang: " + e.getMessage());
        }
        return list;
    }

    public boolean insertBarang(BarangModel barang) {
        String sql = "INSERT INTO barang(nama, stok, harga_beli, harga_jual) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barang.getNama());
            ps.setInt(2, barang.getStok());
            ps.setDouble(3, barang.getHargaBeli());
            ps.setDouble(4, barang.getHargaJual());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Tambah Barang: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBarang(BarangModel barang) {
        String sql = "UPDATE barang SET nama=?, stok=?, harga_beli=?, harga_jual=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barang.getNama());
            ps.setInt(2, barang.getStok());
            ps.setDouble(3, barang.getHargaBeli());
            ps.setDouble(4, barang.getHargaJual());
            ps.setInt(5, barang.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Barang: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBarang(int id) {
        String sql = "DELETE FROM barang WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Hapus Barang: " + e.getMessage());
            return false;
        }
    }

    public int getTotalJenisBarang() {
        String sql = "SELECT COUNT(*) FROM barang";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error Hitung Total Barang: " + e.getMessage());
        }
        return 0;
    }
}