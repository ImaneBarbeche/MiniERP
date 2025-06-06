package src.com.minierp.dao;

import java.sql.DriverManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Classe utilitaire chargée de gérer la connexion à la base de données
 * PostgreSQL.
 * Fournit une méthode statique pour obtenir une connexion à la base 'ERP'.
 */
public class DatabaseManager {
    // Informations de connexion à la base PostgreSQL
    private static final String URL = "jdbc:postgresql://localhost:5432/ERP";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    /**
     * Établit une connexion à la base de données ERP.
     *
     * @return une instance Connection utilisable pour les requêtes SQL.
     * @throws SQLException si la connexion échoue.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Méthode de test autonome.
     * Tente une connexion et exécute une requête simple sur la table 'customers'.
     * Utile pour vérifier que la base est bien installée et accessible.
     */
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            System.out.println("Tentative de connexion à la base de données '" + URL + "'...");
            conn = getConnection(); // Utilise la méthode de cette classe
            System.out.println("Connexion établie avec succès !");

            System.out.println("\nTest de la requête SELECT customerid, firstname, lastname FROM customers LIMIT 5...");
            stmt = conn.createStatement();
            // Adapte les noms de colonnes si ceux de ta table "customers" sont différents
            rs = stmt.executeQuery("SELECT customerid, firstname, lastname FROM customers LIMIT 5");

            System.out.println("Résultats (5 premiers clients) :");
            int count = 0;
            while (rs.next()) {
                count++;
                // Adapte les noms de colonnes à ta table "customers" exacte
                System.out.println("Client: " + rs.getInt("customerid") + " - " +
                        rs.getString("firstname") + " " + rs.getString("lastname"));
            }
            if (count == 0) {
                System.out.println(
                        "Aucun client trouvé dans la table 'customers'. Vérifiez que la table existe et contient des données, ou que le script ERP.sql a bien été exécuté.");
            }
            System.out.println("Test de la requête terminé.");

        } catch (SQLException e) {
            System.err.println("ERREUR lors de la connexion ou de l'exécution de la requête :");
            System.err.println("Message: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Vérifiez vos paramètres de connexion dans DatabaseManager.java,");
            System.err.println("que le serveur PostgreSQL est démarré, que la base 'ERP' existe,");
            System.err.println("et que la table 'customers' (avec les colonnes attendues) existe.");
            e.printStackTrace(); // Affiche la trace complète pour plus de détails
        } finally {
            // Libération des ressources dans tous les cas
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                /* ignoré */ }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                /* ignoré */ }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                /* ignoré */ }
            System.out.println("Ressources JDBC fermées (si applicables).");
        }
        System.out.println("Fin du test DatabaseManager.");
    }
}
