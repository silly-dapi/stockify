package main;

import view.LoginView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Menjalankan GUI di Event Dispatch Thread (EDT) agar thread-safe
        SwingUtilities.invokeLater(() -> {
            LoginView loginPage = new LoginView();
            loginPage.setVisible(true);
        });
    }
}