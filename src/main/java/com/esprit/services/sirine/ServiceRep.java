package com.esprit.services.sirine;

import com.esprit.entities.sirine.Reponse;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRep {
    private Connection connection;

    public ServiceRep() {
        connection = MyDataBase.getInstance().getConnection();
        if (connection == null) {
            throw new IllegalStateException("La connexion à la base de données n'a pas pu être établie.");
        }
    }

    // Ajouter une réponse et mettre à jour le statut de la réclamation
    public void ajouter(Reponse reponse) throws SQLException {
        String reqReponse = "INSERT INTO reponse (id_reclamation, contenu) VALUES (?, ?)";
        String reqStatut = "UPDATE reclamation SET statut = 'traité' WHERE id = ?";

        try (PreparedStatement pstReponse = connection.prepareStatement(reqReponse, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstStatut = connection.prepareStatement(reqStatut)) {

            // Ajouter la réponse
            pstReponse.setInt(1, reponse.getIdReclamation());
            pstReponse.setString(2, reponse.getContenu());
            pstReponse.executeUpdate();

            // Mise à jour du statut de la réclamation à "traité"
            pstStatut.setInt(1, reponse.getIdReclamation());
            pstStatut.executeUpdate();

            // Récupérer l'ID de la réponse générée
            ResultSet rs = pstReponse.getGeneratedKeys();
            if (rs.next()) {
                reponse.setId(rs.getInt(1)); // Récupérer l'ID généré pour la réponse
            }

        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'ajout de la réponse et de la mise à jour du statut", e);
        }
    }

    // Modifier une réponse existante
    public void modifier(Reponse reponse) throws SQLException {
        String req = "UPDATE reponse SET id_reclamation = ?, contenu = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, reponse.getIdReclamation());
            pst.setString(2, reponse.getContenu());
            pst.setInt(3, reponse.getId());
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucune réponse trouvée avec l'ID " + reponse.getId());
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la modification de la réponse", e);
        }
    }

    // Supprimer une réponse
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM reponse WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucune réponse trouvée avec l'ID " + id);
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la suppression de la réponse", e);
        }
    }

    // Afficher toutes les réponses
    public List<Reponse> afficher() throws SQLException {
        List<Reponse> list = new ArrayList<>();
        String req = "SELECT * FROM reponse";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Reponse r = new Reponse(
                        rs.getInt("id"),
                        rs.getInt("id_reclamation"),
                        rs.getString("contenu")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'affichage des réponses", e);
        }
        return list;
    }

    public void supprimer(String nom) {
    }
}
