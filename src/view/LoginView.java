package view;

import controller.UserController;
import model.UserModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginView extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UserController userController;

    // Warna sesuai tema gambar
    private final Color COLOR_BG = new Color(15, 23, 36);        // Dark Navy
    private final Color COLOR_FIELD_BG = new Color(20, 29, 43);  // Sedikit lebih terang dari BG
    private final Color COLOR_TEXT = new Color(220, 225, 235);    // Putih keabuan
    private final Color COLOR_HINT = new Color(90, 105, 120);    // Abu-abu placeholder
    private final Color COLOR_ACCENT = new Color(0, 180, 216);    // Biru muda/cyan saat hover/focus
    private final Color COLOR_BUTTON = new Color(26, 82, 189);    // Biru tombol
    private final Color COLOR_BORDER_DEFAULT = new Color(40, 50, 70);

    // Border kustom (Hanya garis bawah tipis, mirip desain modern)
    private final Border borderDefault = BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER_DEFAULT),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    );
    private final Border borderHover = BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACCENT),
        BorderFactory.createEmptyBorder(5, 5, 4, 5)
    );

    public LoginView() {
        userController = new UserController();
        initUI();
    }

    private void initUI() {
        setTitle("Login STOCKIFY");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_BG);
        panel.setLayout(null); // Menggunakan null layout agar presisi seperti mockup
        add(panel);

        // Title Label
        JLabel lblTitle = new JLabel("LOGIN STOCKIFY", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT);
        lblTitle.setBounds(30, 30, 340, 40);
        panel.add(lblTitle);

        // Username Field
        txtUsername = new JTextField("Username");
        setupFieldStyle(txtUsername);
        txtUsername.setBounds(30, 100, 340, 35);
        panel.add(txtUsername);

        // Password Field
        txtPassword = new JPasswordField("Password");
        txtPassword.setEchoChar((char) 0); // Matikan masking saat berupa placeholder
        setupFieldStyle(txtPassword);
        txtPassword.setBounds(30, 150, 340, 35);
        panel.add(txtPassword);

        // Login Button
        btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(COLOR_BUTTON);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setBounds(30, 210, 340, 40);
        panel.add(btnLogin);

        // Event Listener untuk tombol login
        btnLogin.addActionListener(e -> prosesLogin());
    }

    /**
     * Mengatur style, placeholder, hover, dan focus efek pada textfield
     */
    private void setupFieldStyle(JTextField field) {
        field.setBackground(COLOR_FIELD_BG);
        field.setForeground(COLOR_HINT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(borderDefault);
        field.setCaretColor(COLOR_ACCENT);

        final String hint = (field instanceof JPasswordField) ? "Password" : "Username";

        // Efek Hover (Mouse Masuk/Keluar) & Focus (Aktif mengetik)
        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!field.hasFocus()) field.setBorder(borderHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!field.hasFocus()) field.setBorder(borderDefault);
            }
        });

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(borderHover);
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(COLOR_TEXT);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('•'); // Aktifkan masking password
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(borderDefault);
                if (field.getText().isEmpty()) {
                    field.setText(hint);
                    field.setForeground(COLOR_HINT);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0); // Matikan masking
                    }
                }
            }
        });
    }

    private void prosesLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        // Validasi jika masih teks placeholder
        if (username.equals("Username") || password.equals("Password")) {
            JOptionPane.showMessageDialog(this, "Username dan Password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Panggil Controller untuk mencocokkan ke database
        UserModel user = userController.login(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat Datang " + user.getUsername());

            DashboardView dashboard = new DashboardView();
            dashboard.setVisible(true);
            
            this.dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}