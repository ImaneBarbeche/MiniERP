package src.com.minierp.ui;

import src.com.minierp.dao.CustomerDAO;
import src.com.minierp.dao.HistoriqueDAO;
import src.com.minierp.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoriquePanel extends JPanel {

    private JComboBox<String> clientComboBox;
    private JButton loadButton;
    private JTable historiqueTable;
    private List<Customer> clients;
    private JButton deleteButton;

    public HistoriquePanel() {
        setLayout(new BorderLayout());

        // --- Haut : sélection client + bouton ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientComboBox = new JComboBox<>();
        loadButton = new JButton("Charger historique");

        topPanel.add(new JLabel("Client :"));
        topPanel.add(clientComboBox);
        topPanel.add(loadButton);
        deleteButton = new JButton("Supprimer commande");
        topPanel.add(deleteButton);

        deleteButton.addActionListener(e -> {
    int selectedRow = historiqueTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Sélectionnez une ligne de commande à supprimer.");
        return;
    }

    String orderIdStr = (String) historiqueTable.getValueAt(selectedRow, 0);
    int orderId = Integer.parseInt(orderIdStr);

    int confirm = JOptionPane.showConfirmDialog(this,
            "Confirmer la suppression de la commande #" + orderId + " ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = new HistoriqueDAO().deleteCommande(orderId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Commande supprimée.");
            loadHistorique(); // recharge les données après suppression
        } else {
            JOptionPane.showMessageDialog(this, "Échec de la suppression.");
        }
    }
});


        // --- Centre : tableau des commandes ---
        historiqueTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(historiqueTable);

        // --- Ajout des composants ---
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Chargement initial des clients ---
        loadClients();

        // --- Action bouton ---
        loadButton.addActionListener(e -> loadHistorique());
    }

    private void loadClients() {
        CustomerDAO dao = new CustomerDAO();
        clients = dao.getAllCustomers();
        for (Customer c : clients) {
            clientComboBox.addItem(c.getCustomerId() + " - " + c.getFirstname() + " " + c.getLastname());
        }
    }

    private void loadHistorique() {
        int selectedIndex = clientComboBox.getSelectedIndex();
        if (selectedIndex == -1)
            return;

        Customer selectedClient = clients.get(selectedIndex);
        int clientId = selectedClient.getCustomerId();

        HistoriqueDAO dao = new HistoriqueDAO();
        List<String[]> historique = dao.getHistoriqueByClientId(clientId);

        String[] columns = { "Commande ID", "Date", "Produit", "Quantité", "Prix unitaire", "Total" };
        String[][] data = new String[historique.size()][];

        for (int i = 0; i < historique.size(); i++) {
            data[i] = historique.get(i);
        }

        historiqueTable.setModel(new DefaultTableModel(data, columns));
    }

}
