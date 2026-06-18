package view;

import controller.BarangController;
import controller.PemasokController;
import controller.TransaksiController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardView extends JFrame {
    // Memanggil semua controller terkait untuk mengambil data data return-nya
    private BarangController barangController;
    private PemasokController pemasokController;
    private TransaksiController transaksiController;

    // Definisikan Palet Warna agar Serasi dengan Halaman Login
    private final Color COLOR_BG = new Color(15, 23, 36);         // Dark Navy
    private final Color COLOR_CARD_BG = new Color(24, 34, 54);    // Lebih terang dari BG utama
    private final Color COLOR_TEXT = new Color(220, 225, 235);     // Putih Keabuan
    private final Color COLOR_BUTTON = new Color(26, 82, 189);     // Biru Tombol Navigasi

    public DashboardView() {
        // Inisialisasi object controller
        barangController = new BarangController();
        pemasokController = new PemasokController();
        transaksiController = new TransaksiController();
        
        initUI();
    }

    private void initUI() {
        setTitle("STOCKIFY");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Container Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        add(mainPanel);

        // 1. Bagian Header Judul Atas
        JLabel lblTitle = new JLabel("STOCKIFY", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_TEXT);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // 2. Bagian Tengah (Kumpulan Card Informasi Statistik)
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(COLOR_BG);
        centerPanel.setLayout(new GridLayout(4, 1, 0, 15)); // 4 baris ke bawah, jarak antar baris 15px

        // Mengambil nilai kembalian (return) dari masing-masing controller secara langsung
        int totalBarang = barangController.getTotalJenisBarang();
        int totalSupplier = pemasokController.getTotalSupplier();
        int stokMasuk = transaksiController.getTotalStokBerdasarkanJenis("MASUK");
        int stokKeluar = transaksiController.getTotalStokBerdasarkanJenis("KELUAR");

        // Membuat & Memasang Info Cards ke dalam Panel Tengah
        centerPanel.add(createCardPanel("Total Barang : " + totalBarang));
        centerPanel.add(createCardPanel("Total Supplier : " + totalSupplier));
        centerPanel.add(createCardPanel("Stok Masuk : " + stokMasuk));
        centerPanel.add(createCardPanel("Stok Keluar : " + stokKeluar));
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 3. Bagian Bawah (Kumpulan Tombol Navigasi Menu)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(COLOR_BG);
        bottomPanel.setLayout(new GridLayout(3, 1, 0, 10)); // 3 baris tombol, jarak antar tombol 10px

        JButton btnKelolaBarang = createNavButton("Kelola Barang");
        JButton btnSupplier = createNavButton("Supplier");
        JButton btnTransaksi = createNavButton("Transaksi");

        bottomPanel.add(btnKelolaBarang);
        bottomPanel.add(btnSupplier);
        bottomPanel.add(btnTransaksi);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Integrasi Aksi Navigasi Tombol Menggunakan Lambda Expression (Tanpa @Override manual)
        // Cari baris ini di dalam DashboardView.java Anda:
        btnKelolaBarang.addActionListener(e -> {
            BarangView barangPage = new BarangView();
            barangPage.setVisible(true);
    
            this.dispose(); 
        });

        btnSupplier.addActionListener(e -> {
            SupplierView supplierPage = new SupplierView();
            supplierPage.setVisible(true);
    
            this.dispose();
        });

        btnTransaksi.addActionListener(e -> {
            TransaksiView transaksiPage = new TransaksiView();
            transaksiPage.setVisible(true);
    
            this.dispose();
        });
    }

    /**
     * Helper Method untuk men-styling Panel Card agar seragam sesuai mockup gambar
     */
    private JPanel createCardPanel(String text) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(new EmptyBorder(0, 20, 0, 20)); // Margin teks bagian kiri-kanan dalam card

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(COLOR_TEXT);
        card.add(label, BorderLayout.CENTER);

        return card;
    }

    /**
     * Helper Method untuk men-styling tombol navigasi menu utama bawah
     */
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(COLOR_BUTTON);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40)); // Tinggi tombol 40px
        return button;
    }
}