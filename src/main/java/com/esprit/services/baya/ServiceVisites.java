package com.esprit.services.baya;

import com.esprit.entities.baya.Visite;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServiceVisites {
    private Connection connection;

    // Constructeur avec connexion à la base de données
    public ServiceVisites(Connection connection) {
        this.connection = connection;
    }
    public ServiceVisites() {
        this.connection = MyDataBase.getInstance().getConnection();
    }


    // Ajouter une visite
    public void ajouter(Visite visite) throws SQLException {
        String query = "INSERT INTO visite (nomVisite, description, dureeVisite, statut) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, visite.getNomVisite());
            pst.setString(2, visite.getDescription());
            pst.setFloat(3, visite.getDureeVisite());
            pst.setString(4, visite.getStatut());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("Visite ajoutée avec succès ! ID : " + id);
                }
            }
        }
    }

    // Modifier une visite
    public void modifier(Visite visite) throws SQLException {
        String query = "UPDATE visite SET nomVisite=?, description=?, dureeVisite=?, statut=? WHERE idVisite=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, visite.getNomVisite());
            pst.setString(2, visite.getDescription());
            pst.setFloat(3, visite.getDureeVisite());
            pst.setString(4, visite.getStatut());
            pst.setInt(5, visite.getIdVisite());

            pst.executeUpdate();
        }
    }

    // Supprimer une visite
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM visite WHERE idVisite=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    // Récupérer une visite par ID
    public Visite getVisiteById(int id) throws SQLException {
        String query = "SELECT * FROM visite WHERE idVisite=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Visite(
                        rs.getInt("idVisite"),
                        rs.getString("nomVisite"),
                        rs.getString("description"),
                        rs.getFloat("dureeVisite"),
                        rs.getString("statut")
                );
            }
        }
        return null;
    }

    // Récupérer toutes les visites
    public List<Visite> getAll() throws SQLException {
        List<Visite> list = new ArrayList<>();
        String query = "SELECT * FROM visite";
        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(new Visite(
                        rs.getInt("idVisite"),
                        rs.getString("nomVisite"),
                        rs.getString("description"),
                        rs.getFloat("dureeVisite"),
                        rs.getString("statut")
                ));
            }
        }
        return list;
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
