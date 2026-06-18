package view;

import controller.BarangController;
import model.BarangModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class BarangView extends JFrame {
    private JTextField txtSearch, txtNama, txtStok, txtHargaBeli, txtHargaJual;
    private JButton btnTambah, btnUpdate, btnHapus, btnKembali;
    private JTable tableBarang;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    private BarangController barangController;
    private int selectedId = -1; // Menyimpan ID barang yang sedang dipilih di tabel

    // Palet Warna Sesuai Gambar Mockup
    private final Color COLOR_BG = new Color(15, 23, 36);         // Dark Navy
    private final Color COLOR_FIELD_BG = new Color(20, 29, 43);   // Input Field BG
    private final Color COLOR_TEXT = new Color(220, 225, 235);     // Putih Keabuan
    private final Color COLOR_HINT = new Color(90, 105, 120);     // Placeholder Abu
    private final Color COLOR_ACCENT = new Color(0, 180, 216);     // Cyan Line
    
    private final Color COLOR_GREEN = new Color(40, 167, 69);     // Tambah
    private final Color COLOR_ORANGE = new Color(247, 148, 29);   // Update
    private final Color COLOR_RED = new Color(220, 53, 69);       // Hapus
    private final Color COLOR_BLUE_HEADER = new Color(26, 82, 189); // Header JTable

    public BarangView() {
        barangController = new BarangController();
        initUI();
        loadDataToTable();
    }

    private void initUI() {
        setTitle("Kelola Barang - STOCKIFY");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setLayout(null); // Menggunakan Absolute Layout agar presisi seperti gambar
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(mainPanel);

        // 1. Judul Atas
        JLabel lblTitle = new JLabel("DATA BARANG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(COLOR_TEXT);
        lblTitle.setBounds(15, 15, 800, 40);
        mainPanel.add(lblTitle);

        // 2. Search Field
        txtSearch = new JTextField("Cari nama barang...");
        setupField(txtSearch, "Cari nama barang...");
        txtSearch.setBounds(15, 75, 800, 30);
        mainPanel.add(txtSearch);

        // Trigger Pencarian Real-time saat mengetik
        txtSearch.addCaretListener(e -> {
            String text = txtSearch.getText();
            if (text.equals("Cari nama barang...") || text.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1)); // Filter pada kolom indeks 1 (Nama Barang)
            }
        });

        // 3. Input Fields Form (Nama, Stok, Harga Beli, Harga Jual)
        txtNama = new JTextField("Nama Barang");
        setupField(txtNama, "Nama Barang");
        txtNama.setBounds(15, 115, 240, 30);
        mainPanel.add(txtNama);

        txtStok = new JTextField("Stok");
        setupField(txtStok, "Stok");
        txtStok.setBounds(265, 115, 235, 30);
        mainPanel.add(txtStok);

        txtHargaBeli = new JTextField("Harga Beli");
        setupField(txtHargaBeli, "Harga Beli");
        txtHargaBeli.setBounds(510, 115, 145, 30);
        mainPanel.add(txtHargaBeli);

        txtHargaJual = new JTextField("Harga Jual");
        setupField(txtHargaJual, "Harga Jual");
        txtHargaJual.setBounds(670, 115, 145, 30);
        mainPanel.add(txtHargaJual);

        // 4. Tombol Aksi (Tambah, Update, Hapus)
        btnTambah = createButton("Tambah", COLOR_GREEN);
        btnTambah.setBounds(15, 155, 320, 35);
        mainPanel.add(btnTambah);

        btnUpdate = createButton("Update", COLOR_ORANGE);
        btnUpdate.setBounds(345, 155, 260, 35);
        mainPanel.add(btnUpdate);

        btnHapus = createButton("Hapus", COLOR_RED);
        btnHapus.setBounds(615, 155, 200, 35);
        mainPanel.add(btnHapus);

        // 5. JTable Komponen Data
        String[] columns = {"ID", "Nama Barang", "Stok", "Harga Beli", "Harga Jual"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mencegah sel tabel diedit langsung dengan ketikan text
            }
        };
        
        tableBarang = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        tableBarang.setRowSorter(rowSorter);
        
        // Kustomisasi Tampilan Tabel agar menyatu dengan Tema Dark
        tableBarang.setRowHeight(28);
        tableBarang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableBarang.getTableHeader().setBackground(COLOR_BLUE_HEADER);
        tableBarang.getTableHeader().setForeground(Color.WHITE);
        tableBarang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableBarang.setGridColor(new Color(220, 220, 220));

        JScrollPane scrollPane = new JScrollPane(tableBarang);
        scrollPane.setBounds(15, 205, 800, 300);
        mainPanel.add(scrollPane);

        // Tombol Kembali ke Dashboard Utama
        btnKembali = createButton("← Kembali ke Dashboard", COLOR_FIELD_BG);
        btnKembali.setBounds(15, 515, 200, 30);
        mainPanel.add(btnKembali);

        // --- BINDING EVENT HANDLER ---
        
        // Klik Baris Tabel mengisi form diatasnya
        tableBarang.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableBarang.getSelectedRow();
                if (selectedRow != -1) {
                    // Mengonversi indeks baris view ke model asli jika sedang di-filter/search
                    int modelRow = tableBarang.convertRowIndexToModel(selectedRow);
                    
                    selectedId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
                    
                    txtNama.setText(tableModel.getValueAt(modelRow, 1).toString());
                    txtNama.setForeground(COLOR_TEXT);
                    
                    txtStok.setText(tableModel.getValueAt(modelRow, 2).toString());
                    txtStok.setForeground(COLOR_TEXT);
                    
                    txtHargaBeli.setText(tableModel.getValueAt(modelRow, 3).toString());
                    txtHargaBeli.setForeground(COLOR_TEXT);
                    
                    txtHargaJual.setText(tableModel.getValueAt(modelRow, 4).toString());
                    txtHargaJual.setForeground(COLOR_TEXT);
                }
            }
        });

        // Event Tombol Tambah
        btnTambah.addActionListener(e -> aksiTambah());

        // Event Tombol Update
        btnUpdate.addActionListener(e -> aksiUpdate());

        // Event Tombol Hapus
        btnHapus.addActionListener(e -> aksiHapus());

        // Event Tombol Kembali
        btnKembali.addActionListener(e -> {
            new DashboardView().setVisible(true);
            this.dispose();
        });
    }

    // Ambil Data dari Controller dan render ke JTable
    private void loadDataToTable() {
        tableModel.setRowCount(0); // Bersihkan isi tabel lama
        List<BarangModel> listBarang = barangController.getAllBarang();
        for (BarangModel b : listBarang) {
            tableModel.addRow(new Object[]{
                b.getId(),
                b.getNama(),
                b.getStok(),
                // Format decimal manual jika tidak ingin angka scientific notation (ex: 8e+06) muncul di layar
                String.format("%.0f", b.getHargaBeli()), 
                String.format("%.0f", b.getHargaJual())
            });
        }
    }

    private void resetForm() {
        selectedId = -1;
        tableBarang.clearSelection();
        
        txtNama.setText("Nama Barang"); txtNama.setForeground(COLOR_HINT);
        txtStok.setText("Stok"); txtStok.setForeground(COLOR_HINT);
        txtHargaBeli.setText("Harga Beli"); txtHargaBeli.setForeground(COLOR_HINT);
        txtHargaJual.setText("Harga Jual"); txtHargaJual.setForeground(COLOR_HINT);
    }

    private void aksiTambah() {
        String nama = txtNama.getText();
        String stokStr = txtStok.getText();
        String beliStr = txtHargaBeli.getText();
        String jualStr = txtHargaJual.getText();

        if (nama.equals("Nama Barang") || stokStr.equals("Stok") || beliStr.equals("Harga Beli") || jualStr.equals("Harga Jual")) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi seluruh field data barang!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BarangModel b = new BarangModel(0, nama, Integer.parseInt(stokStr), Double.parseDouble(beliStr), Double.parseDouble(jualStr));
            if (barangController.insertBarang(b)) {
                JOptionPane.showMessageDialog(this, "Data Barang berhasil ditambahkan!");
                loadDataToTable();
                resetForm();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Stok dan Harga harus berupa nominal angka!", "Input Salah", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aksiUpdate() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris data barang di dalam tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BarangModel b = new BarangModel(
                selectedId,
                txtNama.getText(),
                Integer.parseInt(txtStok.getText()),
                Double.parseDouble(txtHargaBeli.getText()),
                Double.parseDouble(txtHargaJual.getText())
            );

            if (barangController.updateBarang(b)) {
                JOptionPane.showMessageDialog(this, "Data Barang berhasil diperbarui!");
                loadDataToTable();
                resetForm();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Kalkulasi angka stok/harga tidak valid!", "Input Salah", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aksiHapus() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris data barang yang ingin dihapus pada tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah anda yakin ingin menghapus data barang ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            if (barangController.deleteBarang(selectedId)) {
                JOptionPane.showMessageDialog(this, "Data Barang berhasil dihapus!");
                loadDataToTable();
                resetForm();
            }
        }
    }

    // Helper Styling untuk Input Text Field + Efek Hover/Focus Garis Cyan
    private void setupField(JTextField field, String hint) {
        field.setBackground(COLOR_FIELD_BG);
        field.setForeground(COLOR_HINT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setCaretColor(COLOR_ACCENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 50, 70)),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));

        field.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!field.hasFocus()) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACCENT),
                        BorderFactory.createEmptyBorder(2, 5, 1, 5)
                    ));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (!field.hasFocus()) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 50, 70)),
                        BorderFactory.createEmptyBorder(2, 5, 2, 5)
                    ));
                }
            }
        });

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACCENT),
                    BorderFactory.createEmptyBorder(2, 5, 1, 5)
                ));
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(COLOR_TEXT);
                }
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 50, 70)),
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)
                ));
                if (field.getText().isEmpty()) {
                    field.setText(hint);
                    field.setForeground(COLOR_HINT);
                }
            }
        });
    }

    // Helper membuat JButton custom warna tanpa border default
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }
}