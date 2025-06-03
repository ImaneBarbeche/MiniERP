package src.com.minierp.ui;

import src.com.minierp.dao.CustomerDAO;
import src.com.minierp.model.Customer;

// Pour JFrame
// Pour mettre la JTable dans un conteneur scrollable
// Pour démarrer l'UI Swing correctement
import javax.swing.*;

// Pour le layout manager
import java.awt.*;
// import java.awt.event.*;
import java.util.List;

public class MainFrame extends JFrame {
    private JTable customerTable;
    private JButton loadButton;
    private JButton addCustomerButton;

    public MainFrame() {
        setTitle("Mini ERP - Gestion Clients");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- Création du TabbedPane ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Onglet CLIENTS ---
        JPanel clientPanel = new JPanel(new BorderLayout());

        // Initialiser customerTable
        customerTable = new JTable();

        // --- Panneau pour les boutons du haut ---
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        loadButton = new JButton("Charger Clients");
        addCustomerButton = new JButton("Ajouter Client");

        // --- ActionListener pour loadButton ---
        loadButton.addActionListener(e -> {
            CustomerDAO dao = new CustomerDAO(); // Crée une instance de CustomerDAO
            List<Customer> customers = dao.getAllCustomers(); // Appelle la méthode du DAO

            // Préparation des données pour la JTable
            String[][] data = new String[customers.size()][3]; // 3 colonnes : ID, Nom, Email
            for (int i = 0; i < customers.size(); i++) {
                Customer c = customers.get(i);
                data[i][0] = String.valueOf(c.getCustomerId());
                data[i][1] = c.getFirstname() + " " + c.getLastname();
                data[i][2] = c.getEmail();
            }
            String[] columns = { "ID", "Nom", "Email" }; // Noms des colonnes pour l'affichage
            customerTable.setModel(new javax.swing.table.DefaultTableModel(data, columns));
        });

        // --- ActionListener pour addCustomerButton ---
        addCustomerButton.addActionListener(e -> { // <--- ACTION LISTENER POUR LE NOUVEAU BOUTON
            // 'this' réfère à l'instance actuelle de MainFrame, qui est un Frame
            AddCustomerForm dialog = new AddCustomerForm(this);
            dialog.setVisible(true); // Affiche la boîte de dialogue
            // Après que la dialogue est fermée, si un client a été ajouté,
        });

        // Ajouter les boutons au topButtonPanel
        topButtonPanel.add(loadButton);
        topButtonPanel.add(addCustomerButton);
        clientPanel.add(topButtonPanel, BorderLayout.NORTH);
        clientPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        tabbedPane.addTab("Clients", clientPanel); // ← onglet Clients
        tabbedPane.addTab("Produits", new ProductPanel()); // ← onglet Produits ici !
        tabbedPane.addTab("Commandes", new CommandePanel()); // ← nouvel onglet 🎉
        tabbedPane.addTab("Historique", new HistoriquePanel());

        add(tabbedPane, BorderLayout.CENTER);
        // --- Table ---

    }

    public static void main(String[] args) {
        // Démarrer l'interface graphique dans le bon thread
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

}
