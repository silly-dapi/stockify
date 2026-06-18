package controller;

import config.Konfigurasi;
import model.TransaksiModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiController {
    private Connection conn;

    public TransaksiController() {
        this.conn = Konfigurasi.getConnection();
    }

    public List<TransaksiModel> getAllTransaksi() {
        List<TransaksiModel> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new TransaksiModel(
                        rs.getInt("id"),
                        rs.getString("nama_barang"),
                        rs.getString("jenis"),
                        rs.getInt("jumlah"),
                        rs.getString("tanggal")));
            }
        } catch (SQLException e) {
            System.err.println("Error Ambil Transaksi: " + e.getMessage());
        }
        return list;
    }

    public boolean insertTransaksi(TransaksiModel t) {
        String sql = "INSERT INTO transaksi(nama_barang, jenis, jumlah, tanggal) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNamaBarang());
            ps.setString(2, t.getJenis());
            ps.setInt(3, t.getJumlah());
            ps.setString(4, t.getTanggal());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Tambah Transaksi: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTransaksi(TransaksiModel t) {
        String sql = "UPDATE transaksi SET nama_barang=?, jenis=?, jumlah=?, tanggal=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNamaBarang());
            ps.setString(2, t.getJenis());
            ps.setInt(3, t.getJumlah());
            ps.setString(4, t.getTanggal());
            ps.setInt(5, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Transaksi: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTransaksi(int id) {
        String sql = "DELETE FROM transaksi WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Hapus Transaksi: " + e.getMessage());
            return false;
        }
    }

    public int getTotalStokBerdasarkanJenis(String jenis) {
        String sql = "SELECT SUM(jumlah) FROM transaksi WHERE jenis = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, jenis);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error Hitung Statistik Transaksi: " + e.getMessage());
        }
        return 0;
    }
}