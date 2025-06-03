package src.com.minierp.dao;

import src.com.minierp.dao.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueDAO {

    public List<String[]> getHistoriqueByClientId(int customerId) {
        List<String[]> historique = new ArrayList<>();

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

    public boolean deleteCommande(int orderId) {
    String deleteOrderlines = "DELETE FROM orderlines WHERE orderid = ?";
    String deleteOrder = "DELETE FROM orders WHERE orderid = ?";

    try (Connection conn = DatabaseManager.getConnection()) {
        conn.setAutoCommit(false);

        try (PreparedStatement stmt1 = conn.prepareStatement(deleteOrderlines);
             PreparedStatement stmt2 = conn.prepareStatement(deleteOrder)) {

            stmt1.setInt(1, orderId);
            stmt1.executeUpdate();

            stmt2.setInt(1, orderId);
            stmt2.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
            return false;
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

}
