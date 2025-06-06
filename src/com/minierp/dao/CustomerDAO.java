package src.com.minierp.dao;

import src.com.minierp.model.Customer;

// Imports des classes JDBC
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement; // Pour la fonction new_customer
import java.util.ArrayList;
import java.util.List;

/**
 * DAO dédié à la gestion des clients.
 * Fournit des méthodes pour récupérer et ajouter des clients à la base.
 */
public class CustomerDAO {

    /**
     * Récupère l'ensemble des clients enregistrés dans la base de données.
     *
     * @return une liste de tous les clients.
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        // Utilise DatabaseManager.getConnection() du même package
        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SELECT customerid, firstname, lastname, email, username FROM customers")) {
            while (rs.next()) {
                Customer c = new Customer(); // Crée un objet Customer
                // ... (setters pour Customer) ...
                c.setCustomerId(rs.getInt("customerid"));
                c.setFirstname(rs.getString("firstname"));
                c.setLastname(rs.getString("lastname"));
                c.setEmail(rs.getString("email"));
                c.setUsername(rs.getString("username"));
                customers.add(c); // ajoute le client à la liste
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

     /**
     * Ajoute un nouveau client à la base en appelant une fonction PostgreSQL nommée "new_customer".
     * La fonction attend 19 paramètres. Certains sont ici simulés par des valeurs fixes à des fins de démonstration.
     *
     * @param c Le client à ajouter (avec prénom, nom, email et username définis au minimum).
     * @return l'identifiant généré pour le nouveau client, ou 0 si échec.
     */
    public int addCustomerUsingFunction(Customer c) {
        String sql = "SELECT * FROM new_customer(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, c.getFirstname());
            stmt.setString(2, c.getLastname());
            stmt.setString(3, "Address1"); // données fictives
            stmt.setString(4, "Address2");
            stmt.setString(5, "City");
            stmt.setString(6, "State");
            stmt.setInt(7, 12345); // code postal
            stmt.setString(8, "Country");
            stmt.setInt(9, 1);
            stmt.setString(10, c.getEmail());
            stmt.setString(11, "0000000000"); // téléphone fictif
            stmt.setInt(12, 1);
            stmt.setString(13, "4111111111111111"); // carte fictive
            stmt.setString(14, "12/25");
            stmt.setString(15, c.getUsername());
            stmt.setString(16, "password");
            stmt.setInt(17, 30); // age fictif
            stmt.setInt(18, 50000); // salaire fictif
            stmt.setString(19, "M"); // genre fictif

            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1); // Récupère l'ID retourné par la fonction
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // en cas d'erreur
    }
}