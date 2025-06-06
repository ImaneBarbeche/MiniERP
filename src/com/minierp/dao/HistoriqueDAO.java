package src.com.minierp.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO dédié à la consultation et la suppression de l'historique des commandes.
 */

public class HistoriqueDAO {

    /**
     * Récupère l'historique des commandes passées par un client donné.
     * Pour chaque ligne de commande, renvoie l'identifiant, la date, le titre du
     * produit,
     * la quantité, le prix unitaire, et le total.
     *
     * @param customerId L'identifiant du client concerné.
     * @return Une liste de tableaux de chaînes de caractères, représentant chaque
     *         ligne de l'historique.
     */

    public List<String[]> getHistoriqueByClientId(int customerId) {
        List<String[]> historique = new ArrayList<>();

        // Requête SQL multi-join pour obtenir toutes les lignes de commande du client
        String sql = """
                    SELECT o.orderid, o.orderdate, p.title, ol.quantity, p.price,
                           (ol.quantity * p.price) AS total
                    FROM orders o
                    JOIN orderlines ol ON o.orderid = ol.orderid
                    JOIN products p ON ol.prod_id = p.prod_id
                    WHERE o.customerid = ?
                    ORDER BY o.orderdate DESC, o.orderid;
                """;

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            // Pour chaque ligne, créer un tableau de chaînes avec les 6 informations clés
            while (rs.next()) {
                String[] row = new String[6];
                row[0] = String.valueOf(rs.getInt("orderid"));
                row[1] = rs.getDate("orderdate").toString();
                row[2] = rs.getString("title");
                row[3] = String.valueOf(rs.getInt("quantity"));
                row[4] = String.format("%.2f", rs.getDouble("price"));
                row[5] = String.format("%.2f", rs.getDouble("total"));
                historique.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historique;
    }

     /**
     * Supprime une commande donnée (et ses lignes associées) de la base de données.
     * Cette opération est effectuée dans une transaction pour garantir la cohérence.
     *
     * @param orderId L'identifiant de la commande à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean deleteCommande(int orderId) {
        String deleteOrderlines = "DELETE FROM orderlines WHERE orderid = ?";
        String deleteOrder = "DELETE FROM orders WHERE orderid = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Démarrage de la transaction

            try (PreparedStatement stmt1 = conn.prepareStatement(deleteOrderlines);
                    PreparedStatement stmt2 = conn.prepareStatement(deleteOrder)) {

                stmt1.setInt(1, orderId);
                stmt1.executeUpdate();

                stmt2.setInt(1, orderId);
                stmt2.executeUpdate();

                conn.commit(); // Validation de la transaction
                return true;

            } catch (SQLException e) {
                conn.rollback(); // En cas d'erreur, annulation de la transaction
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
