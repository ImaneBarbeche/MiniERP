package src.com.minierp;

// Importe la MainFrame si elle est dans un sous-package ui
import src.com.minierp.ui.MainFrame;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Démarrer l'interface graphique
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}