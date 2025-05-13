package com.esprit.services.baya;

import com.esprit.entities.baya.Agriculteur;
import com.esprit.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ServiceAgriculteur {
    private final Connection connection;

    public ServiceAgriculteur() {
        this.connection = MyDataBase.getInstance().getConnection(); // Assure-toi que MyDB fonctionne correctement
    }

    public void ajouterAgriculteur(Agriculteur agriculteur) {
        String query = "INSERT INTO agriculteur (idVisite, telephone) VALUES (?, ?)"; // Changer email -> telephone

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, agriculteur.getIdVisite());
            pst.setString(2, agriculteur.getTelephone()); // Utiliser telephone à la place de email

            int rowsInserted = pst.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Agriculteur ajouté avec succès.");
            } else {
                System.out.println("Échec de l'ajout de l'agriculteur.");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'agriculteur : " + e.getMessage());
        }
    }
    public Map<Integer, Integer> getVisiteDemandeeStats() {
        Map<Integer, Integer> stats = new HashMap<>();
        String sql = "SELECT idvisite, COUNT(*) as total FROM agriculteur GROUP BY idvisite ORDER BY total DESC";

        try (Connection conn = MyDataBase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idVisite = rs.getInt("idvisite");
                int total = rs.getInt("total");
                stats.put(idVisite, total);
            }

        } catch (SQLException ex) {
            System.out.println("Erreur stats visites: " + ex.getMessage());
        }

        return stats;
    }
}
