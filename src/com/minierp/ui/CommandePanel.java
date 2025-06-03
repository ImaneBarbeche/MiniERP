package src.com.minierp.ui;

import src.com.minierp.dao.CustomerDAO;
import src.com.minierp.dao.OrderDAO;
import src.com.minierp.dao.ProductDAO;
import src.com.minierp.model.Customer;
import src.com.minierp.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class CommandePanel extends JPanel {
    private JComboBox<String> clientComboBox;
    private JTable productTable;
    private JButton submitButton;

    private List<Customer> clients;
    private List<Product> products;

    public CommandePanel() {
        setLayout(new BorderLayout());

        // --- Haut : sélection du client ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientComboBox = new JComboBox<>();
        topPanel.add(new JLabel("Client :"));
        topPanel.add(clientComboBox);

        // --- Centre : tableau des produits ---
        productTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(productTable);

        // --- Bas : bouton valider ---
        submitButton = new JButton("Valider la commande");

        // --- Ajout dans la vue ---
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);

        // --- Charger les données ---
        loadClients();
        loadProducts();

        submitButton.addActionListener(e -> {
    try {
        int customerId = getSelectedCustomerId();
        Map<Integer, Integer> productQuantities = getSelectedProductQuantities();

        if (productQuantities.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun produit sélectionné.");
            return;
        }

        OrderDAO dao = new OrderDAO();
        dao.createOrder(customerId, productQuantities);
        JOptionPane.showMessageDialog(this, "Commande enregistrée !");
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors de l’enregistrement : " + ex.getMessage());
    }
});

}
private int getSelectedCustomerId() {
    String selected = (String) clientComboBox.getSelectedItem(); // Ex: "3 - Jeanne Dupont"
    if (selected != null && selected.contains(" - ")) {
        return Integer.parseInt(selected.split(" - ")[0].trim());
    }
    return -1;
}

private Map<Integer, Integer> getSelectedProductQuantities() {
    Map<Integer, Integer> selected = new java.util.HashMap<>();
    
    for (int i = 0; i < productTable.getRowCount(); i++) {
        int productId = (int) productTable.getValueAt(i, 0);
        Object qteObj = productTable.getValueAt(i, 3);
        
        if (qteObj instanceof Integer) {
            int quantity = (Integer) qteObj;
            if (quantity > 0) {
                selected.put(productId, quantity);
            }
        }
    }
    
    return selected;
}
    private void loadClients() {
        CustomerDAO dao = new CustomerDAO();
        clients = dao.getAllCustomers();
        for (Customer c : clients) {
            clientComboBox.addItem(c.getCustomerId() + " - " + c.getFirstname() + " " + c.getLastname());
        }
    }

    private void loadProducts() {
        ProductDAO dao = new ProductDAO();
        products = dao.getAllProducts();

        String[] columns = { "ID", "Titre", "Prix", "Quantité" };
        Object[][] data = new Object[products.size()][4];

        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            data[i][0] = p.getProductId();
            data[i][1] = p.getTitle();
            data[i][2] = p.getPrice();
            data[i][3] = 0; // quantité par défaut
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Seule la colonne "Quantité" est éditable
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 3 ? Integer.class : String.class;
            }
        };

        productTable.setModel(model);
    }
}
