package src.com.minierp.dao;

import src.com.minierp.model.Product;

import java.sql.*; // Importe tout de java.sql
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public List<Product> getProductsByCategory(int categoryId) {
    List<Product> products = new ArrayList<>();
    String query = "SELECT prod_id, title, actor, price, category FROM products WHERE category = ?";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, categoryId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Product p = new Product();
            p.setProductId(rs.getInt("prod_id"));
            p.setTitle(rs.getString("title"));
            p.setActor(rs.getString("actor"));
            p.setPrice(rs.getDouble("price"));
            p.setCategory(rs.getInt("category"));
            products.add(p);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}

    public List<Product> getAllProducts() {
    List<Product> products = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT prod_id, title, actor, price, category FROM products")) {
        while (rs.next()) {
            Product p = new Product();
            p.setProductId(rs.getInt("prod_id"));
            p.setTitle(rs.getString("title"));
            p.setActor(rs.getString("actor"));
            p.setPrice(rs.getDouble("price"));
            p.setCategory(rs.getInt("category"));
            products.add(p);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}

public List<Product> getProduitsÀReconstituer() {
    List<Product> produitsCritiques = new ArrayList<>();

    String sql = "SELECT p.prod_id, p.title, p.actor, p.price, p.category " +
                 "FROM products p JOIN inventory i ON p.prod_id = i.prod_id " +
                 "WHERE i.quan_in_stock < i.reorder";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Product p = new Product();
            p.setProductId(rs.getInt("prod_id"));
            p.setTitle(rs.getString("title"));
            p.setActor(rs.getString("actor"));
            p.setPrice(rs.getDouble("price"));
            p.setCategory(rs.getInt("category"));
            produitsCritiques.add(p);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return produitsCritiques;
}

}