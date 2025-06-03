package src.com.minierp.dao;

import java.sql.*;
import java.util.Map;

public class OrderDAO {

    public void createOrder(int customerId, Map<Integer, Integer> productQuantities) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // ➜ DÉBUT de transaction

            // 1. Insérer dans la table orders
            String orderSQL = "INSERT INTO orders (customerid, orderdate, netamount, tax, totalamount) VALUES (?, NOW(), ?, ?, ?) RETURNING orderid";
            double netAmount = 0.0;
            double taxRate = 0.2;

            // Calcul net à partir des produits
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

            // 2. Insérer dans la table orderlines
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
            // 3. Met à jour les stocks
            String updateStockSql = "UPDATE inventory SET quan_in_stock = quan_in_stock - ? WHERE prod_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateStockSql)) {
                for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
                    int productId = entry.getKey();
                    int quantityOrdered = entry.getValue();

                    updateStmt.setInt(1, quantityOrdered);
                    updateStmt.setInt(2, productId);
                    updateStmt.addBatch();
                }
                updateStmt.executeBatch();
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
                conn.close();
        }
    }
}
