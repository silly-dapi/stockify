package view;

import controller.PemasokController;
import model.PemasokModel;

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

public class SupplierView extends JFrame {
    private JTextField txtSearch, txtNama, txtAlamat, txtTelepon;
    private JButton btnTambah, btnUpdate, btnHapus, btnKembali;
    private JTable tableSupplier;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    private PemasokController pemasokController;
    private int selectedId = -1; // Menyimpan ID supplier yang sedang diklik

    // Tema Warna Dark Mode sesuai Mockup
    private final Color COLOR_BG = new Color(15, 23, 36);
    private final Color COLOR_FIELD_BG = new Color(20, 29, 43);
    private final Color COLOR_TEXT = new Color(220, 225, 235);
    private final Color COLOR_HINT = new Color(90, 105, 120);
    private final Color COLOR_ACCENT = new Color(0, 180, 216);
    
    private final Color COLOR_GREEN = new Color(40, 167, 69);
    private final Color COLOR_ORANGE = new Color(247, 148, 29);
    private final Color COLOR_RED = new Color(220, 53, 69);
    private final Color COLOR_BLUE_HEADER = new Color(26, 82, 189);

    public SupplierView() {
        pemasokController = new PemasokController();
        initUI();
        loadDataToTable();
    }

    private void initUI() {
        setTitle("Data Supplier - STOCKIFY");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setLayout(null);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(mainPanel);

        // 1. Judul Menu
        JLabel lblTitle = new JLabel("DATA SUPPLIER", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(COLOR_TEXT);
        lblTitle.setBounds(15, 15, 800, 40);
        mainPanel.add(lblTitle);

        // 2. Search Field Real-time
        txtSearch = new JTextField("Cari supplier...");
        setupField(txtSearch, "Cari supplier...");
        txtSearch.setBounds(15, 75, 800, 30);
        mainPanel.add(txtSearch);

        txtSearch.addCaretListener(e -> {
            String text = txtSearch.getText();
            if (text.equals("Cari supplier...") || text.trim().isEmpty()) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1)); // Filter berdasar kolom Nama Supplier (Indeks 1)
            }
        });

        // 3. Form Input Data
        txtNama = new JTextField("Nama Supplier");
        setupField(txtNama, "Nama Supplier");
        txtNama.setBounds(15, 115, 320, 30);
        mainPanel.add(txtNama);

        txtAlamat = new JTextField("Alamat");
        setupField(txtAlamat, "Alamat");
        txtAlamat.setBounds(350, 115, 290, 30);
        mainPanel.add(txtAlamat);

        txtTelepon = new JTextField("Telepon");
        setupField(txtTelepon, "Telepon");
        txtTelepon.setBounds(655, 115, 160, 30);
        mainPanel.add(txtTelepon);

        // 4. Tombol CRUD
        btnTambah = createButton("Tambah", COLOR_GREEN);
        btnTambah.setBounds(15, 155, 320, 35);
        mainPanel.add(btnTambah);

        btnUpdate = createButton("Update", COLOR_ORANGE);
        btnUpdate.setBounds(345, 155, 260, 35);
        mainPanel.add(btnUpdate);

        btnHapus = createButton("Hapus", COLOR_RED);
        btnHapus.setBounds(615, 155, 200, 35);
        mainPanel.add(btnHapus);

        // 5. Komponen JTable
        String[] columns = {"ID", "Nama Supplier", "Alamat", "Telepon"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableSupplier = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        tableSupplier.setRowSorter(rowSorter);

        tableSupplier.setRowHeight(28);
        tableSupplier.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableSupplier.getTableHeader().setBackground(COLOR_BLUE_HEADER);
        tableSupplier.getTableHeader().setForeground(Color.WHITE);
        tableSupplier.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableSupplier.setGridColor(new Color(220, 220, 220));

        JScrollPane scrollPane = new JScrollPane(tableSupplier);
        scrollPane.setBounds(15, 205, 800, 300);
        mainPanel.add(scrollPane);

        // Tombol Kembali
        btnKembali = createButton("← Kembali ke Dashboard", COLOR_FIELD_BG);
        btnKembali.setBounds(15, 515, 200, 30);
        mainPanel.add(btnKembali);

        // --- BINDING EVENT HANDLER ---

        // Klik Baris JTable mengisi form input diatasnya
        tableSupplier.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableSupplier.getSelectedRow();
                if (selectedRow != -1) {
                    int modelRow = tableSupplier.convertRowIndexToModel(selectedRow);
                    selectedId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());

                    txtNama.setText(tableModel.getValueAt(modelRow, 1).toString());
                    txtNama.setForeground(COLOR_TEXT);

                    txtAlamat.setText(tableModel.getValueAt(modelRow, 2).toString());
                    txtAlamat.setForeground(COLOR_TEXT);

                    txtTelepon.setText(tableModel.getValueAt(modelRow, 3).toString());
                    txtTelepon.setForeground(COLOR_TEXT);
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

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        List<PemasokModel> list = pemasokController.getAllPemasok();
        for (PemasokModel p : list) {
            tableModel.addRow(new Object[]{p.getId(), p.getNama(), p.getAlamat(), p.getTelepon()});
        }
    }

    private void resetForm() {
        selectedId = -1;
        tableSupplier.clearSelection();
        txtNama.setText("Nama Supplier"); txtNama.setForeground(COLOR_HINT);
        txtAlamat.setText("Alamat"); txtAlamat.setForeground(COLOR_HINT);
        txtTelepon.setText("Telepon"); txtTelepon.setForeground(COLOR_HINT);
    }

    private void aksiTambah() {
        String nama = txtNama.getText();
        String alamat = txtAlamat.getText();
        String telepon = txtTelepon.getText();

        if (nama.equals("Nama Supplier") || alamat.equals("Alamat") || telepon.equals("Telepon")) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi seluruh kolom data supplier!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PemasokModel p = new PemasokModel(0, nama, alamat, telepon);
        if (pemasokController.insertPemasok(p)) {
            JOptionPane.showMessageDialog(this, "Data Supplier berhasil ditambahkan!");
            loadDataToTable();
            resetForm();
        }
    }

    private void aksiUpdate() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris supplier pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PemasokModel p = new PemasokModel(selectedId, txtNama.getText(), txtAlamat.getText(), txtTelepon.getText());
        if (pemasokController.updatePemasok(p)) {
            JOptionPane.showMessageDialog(this, "Data Supplier berhasil diperbarui!");
            loadDataToTable();
            resetForm();
        }
    }

    private void aksiHapus() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris supplier yang ingin dihapus pada tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah anda yakin ingin menghapus supplier ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            if (pemasokController.deletePemasok(selectedId)) {
                JOptionPane.showMessageDialog(this, "Data Supplier berhasil dihapus!");
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