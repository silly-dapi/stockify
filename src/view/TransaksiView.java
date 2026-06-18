package view;

import controller.BarangController;
import controller.TransaksiController;
import model.BarangModel;
import model.TransaksiModel;

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

public class TransaksiView extends JFrame {
    private JTextField txtSearch, txtJumlah, txtTanggal;
    private JComboBox<String> cbBarang, cbJenis;
    private JButton btnTambah, btnUpdate, btnHapus, btnKembali;
    private JTable tableTransaksi;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private TransaksiController transaksiController;
    private BarangController barangController;
    private int selectedId = -1;

    // Definisikan Palet Tema Warna Dark Mode
    private final Color COLOR_BG = new Color(15, 23, 36);
    private final Color COLOR_FIELD_BG = new Color(20, 29, 43);
    private final Color COLOR_TEXT = new Color(220, 225, 235);
    private final Color COLOR_HINT = new Color(90, 105, 120);
    private final Color COLOR_ACCENT = new Color(0, 180, 216);

    private final Color COLOR_GREEN = new Color(40, 167, 69);
    private final Color COLOR_ORANGE = new Color(247, 148, 29);
    private final Color COLOR_RED = new Color(220, 53, 69);
    private final Color COLOR_BLUE_HEADER = new Color(26, 82, 189);

    public TransaksiView() {
        transaksiController = new TransaksiController();
        barangController = new BarangController();
        initUI();
        loadBarangToComboBox();
        loadDataToTable();
    }

    private void initUI() {
        setTitle("Kelola Transaksi - STOCKIFY");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setLayout(null);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(mainPanel);

        // 1. Judul Menu Atas
        JLabel lblTitle = new JLabel("DATA TRANSAKSI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(COLOR_TEXT);
        lblTitle.setBounds(15, 15, 800, 40);
        mainPanel.add(lblTitle);

        // 2. Search Field Real-time
        txtSearch = new JTextField("Cari transaksi...");
        setupField(txtSearch, "Cari transaksi...");
        txtSearch.setBounds(15, 75, 800, 30);
        mainPanel.add(txtSearch);

        txtSearch.addCaretListener(e -> {
            String text = txtSearch.getText();
            if (text.equals("Cari transaksi...") || text.trim().isEmpty()) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1)); // Menyaring berdasar kolom Barang (Indeks 1)
            }
        });

        // 3. Dropdown Menu (JComboBox) Pilihan Barang & Jenis Transaksi
        cbBarang = new JComboBox<>();
        cbBarang.setBackground(COLOR_FIELD_BG);
        cbBarang.setForeground(COLOR_TEXT);
        cbBarang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbBarang.setBounds(15, 115, 200, 30);
        mainPanel.add(cbBarang);

        String[] jenisOpsi = {"MASUK", "KELUAR"};
        cbJenis = new JComboBox<>(jenisOpsi);
        cbJenis.setBackground(COLOR_FIELD_BG);
        cbJenis.setForeground(COLOR_TEXT);
        cbJenis.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbJenis.setBounds(225, 115, 90, 30);
        mainPanel.add(cbJenis);

        // 4. Input Text Fields Jumlah & Tanggal
        txtJumlah = new JTextField("Jumlah");
        setupField(txtJumlah, "Jumlah");
        txtJumlah.setBounds(325, 115, 230, 30);
        mainPanel.add(txtJumlah);

        txtTanggal = new JTextField("Tanggal");
        setupField(txtTanggal, "Tanggal");
        txtTanggal.setBounds(565, 115, 250, 30);
        mainPanel.add(txtTanggal);

        // 5. Tombol Manipulasi Data
        btnTambah = createButton("Tambah", COLOR_GREEN);
        btnTambah.setBounds(15, 155, 320, 35);
        mainPanel.add(btnTambah);

        btnUpdate = createButton("Update", COLOR_ORANGE);
        btnUpdate.setBounds(345, 155, 260, 35);
        mainPanel.add(btnUpdate);

        btnHapus = createButton("Hapus", COLOR_RED);
        btnHapus.setBounds(615, 155, 200, 35);
        mainPanel.add(btnHapus);

        // 6. Komponen JTable Riwayat Transaksi
        String[] columns = {"ID", "Barang", "Jenis", "Jumlah", "Tanggal"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableTransaksi = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        tableTransaksi.setRowSorter(rowSorter);

        tableTransaksi.setRowHeight(28);
        tableTransaksi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableTransaksi.getTableHeader().setBackground(COLOR_BLUE_HEADER);
        tableTransaksi.getTableHeader().setForeground(Color.WHITE);
        tableTransaksi.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableTransaksi.setGridColor(new Color(220, 220, 220));

        JScrollPane scrollPane = new JScrollPane(tableTransaksi);
        scrollPane.setBounds(15, 205, 800, 300);
        mainPanel.add(scrollPane);

        // Tombol Navigasi Keluar
        btnKembali = createButton("← Kembali ke Dashboard", COLOR_FIELD_BG);
        btnKembali.setBounds(15, 515, 200, 30);
        mainPanel.add(btnKembali);

        // --- BINDING EVENT HANDLER ---

        // Mengisi Form secara otomatis dari baris JTable yang diklik user
        tableTransaksi.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableTransaksi.getSelectedRow();
                if (selectedRow != -1) {
                    int modelRow = tableTransaksi.convertRowIndexToModel(selectedRow);
                    selectedId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());

                    String namaBarang = tableModel.getValueAt(modelRow, 1).toString();
                    cbBarang.setSelectedItem(namaBarang);

                    String jenis = tableModel.getValueAt(modelRow, 2).toString();
                    cbJenis.setSelectedItem(jenis);

                    txtJumlah.setText(tableModel.getValueAt(modelRow, 3).toString());
                    txtJumlah.setForeground(COLOR_TEXT);

                    txtTanggal.setText(tableModel.getValueAt(modelRow, 4).toString());
                    txtTanggal.setForeground(COLOR_TEXT);
                }
            }
        });

        btnTambah.addActionListener(e -> aksiTambah());
        btnUpdate.addActionListener(e -> aksiUpdate());
        btnHapus.addActionListener(e -> aksiHapus());

        btnKembali.addActionListener(e -> {
            new DashboardView().setVisible(true);
            this.dispose();
        });
    }

    // Mengambil opsi produk terdaftar untuk disuntikkan ke dalam ComboBox
    private void loadBarangToComboBox() {
        cbBarang.removeAllItems();
        List<BarangModel> listBarang = barangController.getAllBarang();
        for (BarangModel b : listBarang) {
            cbBarang.addItem(b.getNama());
        }
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        List<TransaksiModel> list = transaksiController.getAllTransaksi();
        for (TransaksiModel t : list) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getNamaBarang(),
                t.getJenis(),
                t.getJumlah(),
                t.getTanggal()
            });
        }
    }

    private void resetForm() {
        selectedId = -1;
        tableTransaksi.clearSelection();
        if (cbBarang.getItemCount() > 0) cbBarang.setSelectedIndex(0);
        cbJenis.setSelectedIndex(0);
        txtJumlah.setText("Jumlah"); txtJumlah.setForeground(COLOR_HINT);
        txtTanggal.setText("Tanggal"); txtTanggal.setForeground(COLOR_HINT);
    }

    private void aksiTambah() {
        if (cbBarang.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Daftar barang kosong. Silakan kelola data barang terlebih dahulu!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String namaBarang = cbBarang.getSelectedItem().toString();
        String jenis = cbJenis.getSelectedItem().toString();
        String jumlahStr = txtJumlah.getText();
        String tanggal = txtTanggal.getText();

        if (jumlahStr.equals("Jumlah") || tanggal.equals("Tanggal")) {
            JOptionPane.showMessageDialog(this, "Mohon tentukan nominal kuantitas jumlah dan tanggal stok!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int jumlah = Integer.parseInt(jumlahStr);
            TransaksiModel t = new TransaksiModel(0, namaBarang, jenis, jumlah, tanggal);
            if (transaksiController.insertTransaksi(t)) {
                JOptionPane.showMessageDialog(this, "Aktivitas transaksi mutasi stok berhasil dicatat!");
                loadDataToTable();
                resetForm();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Kuantitas jumlah barang harus berupa angka bulat!", "Input Salah", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aksiUpdate() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris riwayat transaksi pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            TransaksiModel t = new TransaksiModel(
                selectedId,
                cbBarang.getSelectedItem().toString(),
                cbJenis.getSelectedItem().toString(),
                Integer.parseInt(txtJumlah.getText()),
                txtTanggal.getText()
            );

            if (transaksiController.updateTransaksi(t)) {
                JOptionPane.showMessageDialog(this, "Catatan nota riwayat transaksi berhasil diubah!");
                loadDataToTable();
                resetForm();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format kuantitas jumlah tidak valid!", "Input Salah", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aksiHapus() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris transaksi yang ingin dihapus pada tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus arsip riwayat transaksi ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            if (transaksiController.deleteTransaksi(selectedId)) {
                JOptionPane.showMessageDialog(this, "Data Transaksi berhasil dibersihkan!");
                loadDataToTable();
                resetForm();
            }
        }
    }

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