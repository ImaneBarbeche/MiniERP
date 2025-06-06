package src.com.minierp.dao;

import java.sql.*;
import java.util.Map;

/**
 * DAO responsable de la création des commandes client.
 * Effectue l’insertion des commandes, des lignes de commande, et la mise à jour des stocks.
 */
public class OrderDAO {

    /**
     * Crée une commande pour un client donné à partir d’un panier de produits (quantité par produit).
     * Cette opération effectue plusieurs étapes en une seule transaction :
     * - Calcul du montant
     * - Insertion de la commande
     * - Insertion des lignes de commande
     * - Mise à jour du stock
     *
     * @param customerId         L'identifiant du client passant commande.
     * @param productQuantities  Une map contenant les identifiants de produits et leur quantité.
     * @throws SQLException si une erreur survient pendant la transaction.
     */
    public void createOrder(int customerId, Map<Integer, Integer> productQuantities) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // ➜ DÉBUT de transaction

            // Étape 1 : Préparation du montant net de la commande
            double netAmount = 0.0;
            double taxRate = 0.2;

            // Calcule le total HT en récupérant le prix de chaque produit
            for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
                int productId = entry.getKey();
                int quantity = entry.getValue();

                PreparedStatement priceStmt = conn.prepareStatement("SELECT price FROM products WHERE prod_id = ?");
                priceStmt.setInt(1, productId);
                ResultSet rs = priceStmt.executeQuery();
                if (rs.next()) {
                    double price = rs.getDouble("price");
                    netAmount += price * quantity;
                }
                rs.close();
                priceStmt.close();
            }
            double tax = netAmount * taxRate;
            double total = netAmount + tax;
            
            // Étape 2 : Insertion dans la table orders
            String orderSQL = "INSERT INTO orders (customerid, orderdate, netamount, tax, totalamount) VALUES (?, NOW(), ?, ?, ?) RETURNING orderid";
            
            PreparedStatement orderStmt = conn.prepareStatement(orderSQL);
            orderStmt.setInt(1, customerId);
            orderStmt.setDouble(2, netAmount);
            orderStmt.setDouble(3, tax);
            orderStmt.setDouble(4, total);

            ResultSet orderRs = orderStmt.executeQuery();
            int orderId = -1;
            if (orderRs.next()) {
                orderId = orderRs.getInt("orderid");
            }
            orderRs.close();
            orderStmt.close();

             // Étape 3 : Insertion des lignes de commande
            for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
                int productId = entry.getKey();
                int quantity = entry.getValue();

                PreparedStatement insertLine = conn.prepareStatement(
                        "INSERT INTO orderlines (orderid, prod_id, quantity) VALUES (?, ?, ?)");
                insertLine.setInt(1, orderId);
                insertLine.setInt(2, productId);
                insertLine.setInt(3, quantity);
                insertLine.executeUpdate();
                insertLine.close();
            }
            // Étape 4 : Mise à jour du stock dans inventory
            String updateStockSql = "UPDATE inventory SET quan_in_stock = quan_in_stock - ? WHERE prod_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateStockSql)) {
                for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
                    int productId = entry.getKey();
                    int quantityOrdered = entry.getValue();

                    updateStmt.setInt(1, quantityOrdered);
                    updateStmt.setInt(2, productId);
                    updateStmt.addBatch(); // Ajoute à la liste des requêtes batch
                }
                updateStmt.executeBatch(); // Exécute toutes les mises à jour de stock
            }

            conn.commit(); // ✅ OK tout s’est bien passé
            System.out.println("Commande enregistrée avec succès !");
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // 💥 en cas de problème, on annule tout
            }
            throw e;
        } finally {
            if (conn != null)
                conn.close(); // Fermeture de la connexion
        }
    }
}
