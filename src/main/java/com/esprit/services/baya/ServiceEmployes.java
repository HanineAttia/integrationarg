package com.esprit.services.baya;

import com.esprit.entities.baya.Employes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEmployes {
    private Connection connection;

    // Constructeur avec connexion à la base de données
    public ServiceEmployes(Connection connection) {
        this.connection = connection;
    }

    public ServiceEmployes() {
    }

    // Ajouter un employé
    public void ajouter(Employes employe) throws SQLException {
        String query = "INSERT INTO employes (nom, prenom, type, email, age, idVisite) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, employe.getNom());
            pst.setString(2, employe.getPrenom());
            pst.setString(3, employe.getType());
            pst.setString(4, employe.getEmail());
            pst.setInt(5, employe.getAge());
            pst.setInt(6, employe.getIdVisite());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("Employé ajouté avec succès ! ID : " + id);
                }
            }
        }
    }

    // Modifier un employé
    public void modifier(Employes employe) throws SQLException {
        String query = "UPDATE employes SET nom=?, prenom=?, type=?, email=?, age=?, idVisite=? WHERE idEmploye=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, employe.getNom());
            pst.setString(2, employe.getPrenom());
            pst.setString(3, employe.getType());
            pst.setString(4, employe.getEmail());
            pst.setInt(5, employe.getAge());
            pst.setInt(6, employe.getIdVisite());
            pst.setInt(7, employe.getIdEmploye());

            pst.executeUpdate();
        }
    }

    // Supprimer un employé
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM employes WHERE idEmploye=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    // Récupérer un employé par ID
    public Employes getEmployeById(int id) throws SQLException {
        String query = "SELECT * FROM employes WHERE idEmploye=?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Employes(
                        rs.getInt("idEmploye"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("type"),
                        rs.getString("email"),
                        rs.getInt("age"),
                        rs.getInt("idVisite")
                );
            }
        }
        return null;
    }

    // Récupérer tous les employés
    public List<Employes> getAll() throws SQLException {
        List<Employes> list = new ArrayList<>();
        String query = "SELECT * FROM employes";
        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(new Employes(
                        rs.getInt("idEmploye"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("type"),
                        rs.getString("email"),
                        rs.getInt("age"),
                        rs.getInt("idVisite")
                ));
            }
        }
        return list;
    }
}
