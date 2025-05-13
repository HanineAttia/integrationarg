package com.esprit.services.sirine;

import com.esprit.utils.MyDataBase;
import com.esprit.entities.sirine.Reclamation;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRec {
    private Connection connection;

    public ServiceRec() throws SQLException {
        connection = MyDataBase.getInstance().getConnection();
        if (connection == null) {
            throw new IllegalStateException("La connexion à la base de données n'a pas pu être établie.");
        }
    }

    public void ajouter(Reclamation reclamation) throws SQLException {
        String req = "INSERT INTO reclamation (id_user, nomMotif, contenu, statut) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reclamation.getIdUser());
            pst.setString(2, reclamation.getNomMotif());
            pst.setString(3, reclamation.getContenu());
            pst.setString(4, reclamation.getStatut());
            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                reclamation.setId(rs.getInt(1));
            }
        }
    }

    public void modifier(Reclamation reclamation) throws SQLException {
        String req = "UPDATE reclamation SET id_user = ?, nomMotif = ?, contenu = ?, statut = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, reclamation.getIdUser());
            pst.setString(2, reclamation.getNomMotif());
            pst.setString(3, reclamation.getContenu());
            pst.setString(4, reclamation.getStatut());
            pst.setInt(5, reclamation.getId());
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucune réclamation trouvée avec l'ID " + reclamation.getId());
            }
        }
    }

    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM reclamation WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucune réclamation trouvée avec l'ID " + id);
            }
        }
    }

    public List<Reclamation> afficher() throws SQLException {
        List<Reclamation> list = new ArrayList<>();
        String req = "SELECT * FROM reclamation";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Reclamation r = new Reclamation(
                        rs.getInt("id"),
                        rs.getInt("id_user"),
                        rs.getString("nomMotif"),
                        rs.getString("contenu"),
                        rs.getString("statut")
                );
                list.add(r);
            }
        }
        return list;
    }

    public List<String> getAllMotifs() throws SQLException {
        return new ServiceMotifs().getMotifOptions();
    }

    public void supprimer(String nom) {
    }
}
