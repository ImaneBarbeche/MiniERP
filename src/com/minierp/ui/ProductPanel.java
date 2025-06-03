package src.com.minierp.ui;

import src.com.minierp.dao.ProductDAO;
import src.com.minierp.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProductPanel extends JPanel {
    private JTable productTable;
    private JComboBox<String> categoryComboBox;
    private JButton loadButton;
    private JButton alertesButton;

    public ProductPanel() {
        setLayout(new BorderLayout());

        // Haut : filtres
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryComboBox = new JComboBox<>(new String[] { "Toutes", "1", "2", "3" });
        loadButton = new JButton("Charger Produits");
        alertesButton = new JButton("Produits à reconstituer");

        topPanel.add(new JLabel("Catégorie :"));
        topPanel.add(categoryComboBox);
        topPanel.add(loadButton);
        topPanel.add(alertesButton);
        

        alertesButton.addActionListener(e -> {
            ProductDAO dao = new ProductDAO();
            List<Product> produitsCritiques = dao.getProduitsÀReconstituer();

            String[] columns = { "ID", "Titre", "Acteur", "Prix", "Catégorie" };
            String[][] data = new String[produitsCritiques.size()][5];

            for (int i = 0; i < produitsCritiques.size(); i++) {
                Product p = produitsCritiques.get(i);
                data[i][0] = String.valueOf(p.getProductId());
                data[i][1] = p.getTitle();
                data[i][2] = p.getActor();
                data[i][3] = String.valueOf(p.getPrice());
                data[i][4] = String.valueOf(p.getCategory());
            }

            productTable.setModel(new javax.swing.table.DefaultTableModel(data, columns));
        });

        // Centre : tableau
        productTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(productTable);

        // Layout global
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Action du bouton
        loadButton.addActionListener(e -> loadProducts());
    }

    private void loadProducts() {
        ProductDAO dao = new ProductDAO();
        List<Product> products;
        String selectedCategory = (String) categoryComboBox.getSelectedItem();

        if (selectedCategory.equals("Toutes")) {
            products = dao.getAllProducts();
        } else {
            try {
                int catId = Integer.parseInt(selectedCategory);
                products = dao.getProductsByCategory(catId);
            } catch (NumberFormatException e) {
                products = new ArrayList<>(); // fallback vide
            }
        }

        String[] columns = { "ID", "Titre", "Acteur", "Prix", "Catégorie" };
        String[][] data = new String[products.size()][columns.length];

        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            data[i][0] = String.valueOf(p.getProductId());
            data[i][1] = p.getTitle();
            data[i][2] = p.getActor();
            data[i][3] = String.format("%.2f", p.getPrice());
            data[i][4] = String.valueOf(p.getCategory());
        }

        productTable.setModel(new DefaultTableModel(data, columns));
    }
}
