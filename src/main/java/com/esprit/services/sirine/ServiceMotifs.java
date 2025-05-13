package com.esprit.services.sirine;

import com.esprit.entities.sirine.Motifs;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceMotifs {
    private Connection connection;

    public ServiceMotifs() {
        connection = MyDataBase.getInstance().getConnection();
        if (connection == null) {
            throw new IllegalStateException("La connexion à la base de données n'a pas pu être établie.");
        }
    }

    public void ajouter(Motifs motif) throws SQLException {
        String req = "INSERT INTO motifs (nom, description) VALUES (?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, motif.getNom());
            pst.setString(2, motif.getDescription());
            System.out.println("Exécution de la requête : " + req + " avec nom=" + motif.getNom() + ", description=" + motif.getDescription());
            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                motif.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL dans ajouter (Motifs) : " + e.getMessage());
            throw e;
        }
    }

    public void modifier(Motifs motif) throws SQLException {
        String req = "UPDATE motifs SET nom = ?, description = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, motif.getNom());
            pst.setString(2, motif.getDescription());
            pst.setInt(3, motif.getId());
            System.out.println("Exécution de la requête : " + req + " avec id=" + motif.getId());
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun motif trouvé avec l'ID " + motif.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL dans modifier (Motifs) : " + e.getMessage());
            throw e;
        }
    }

    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM motifs WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, id);
            System.out.println("Exécution de la requête : " + req + " avec id=" + id);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun motif trouvé avec l'ID " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL dans supprimer (Motifs) : " + e.getMessage());
            throw e;
        }
    }

    public List<Motifs> afficher() throws SQLException {
        List<Motifs> list = new ArrayList<>();
        String req = "SELECT * FROM motifs";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            System.out.println("Exécution de la requête : " + req);
            while (rs.next()) {
                Motifs m = new Motifs(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description")
                );
                list.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL dans afficher (Motifs) : " + e.getMessage());
            throw e;
        }
        return list;
    }

    public List<String> getMotifOptions() {
        List<String> options = new ArrayList<>();
        options.add("Arnaque");
        options.add("Harcèlement");
        options.add("Contenu approprié");
        options.add("Autres");
        return options;
    }

    public void supprimer(String nom) {

    }
}