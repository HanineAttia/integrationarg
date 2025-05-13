package com.esprit.services.aya;

import com.esprit.entities.aya.Personne;
import com.esprit.services.ICrud;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Servicepersonne implements ICrud<Personne> {
    Connection connection;
    public Servicepersonne(){
        connection= MyDataBase.getInstance().getConnection();

    }

    @Override
    public void ajouter(Personne personne) throws SQLException {
        String req="insert into personne (nom,prenom,date,role,email,motDePasse,photo)"+
                "values('"+personne.getNom()+"','"+personne.getPrenom()+"','"+personne.getDate()+"','"+personne.getRole()+"','"+personne.getEmail()+"','"+personne.getMotDePasse()+"','"+personne.getPhoto()+"')";

        Statement statement=connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("personne ajoute");


    }

    @Override
    public void modifier(Personne personne) throws SQLException {
        String req = "UPDATE personne SET nom = ?, prenom = ?, date = ?, role = ?, email = ?, motDePasse = ?, photo = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, personne.getNom());
            preparedStatement.setString(2, personne.getPrenom());
            preparedStatement.setString(3, personne.getDate());
            preparedStatement.setString(4, personne.getRole());
            preparedStatement.setString(5, personne.getEmail());
            preparedStatement.setString(6, personne.getMotDePasse());
            preparedStatement.setString(7, personne.getPhoto());
            preparedStatement.setInt(8, personne.getId());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Mise à jour réussie !");
            } else {
                System.out.println("Aucune ligne mise à jour (ID introuvable).");
            }
        }

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM personne WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Personne supprimée avec succès !");
            } else {
                System.out.println("Aucune personne trouvée avec cet ID.");
            }
        }

    }

    @Override
    public List<Personne> getAll() throws SQLException {
        List<Personne> personnes = new ArrayList<>();
        String req = "SELECT * FROM personne";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Personne personne = new Personne();
            personne.setId(rs.getInt("id"));
            personne.setNom(rs.getString("nom"));
            personne.setPrenom(rs.getString("prenom"));
            personne.setDate(rs.getString("date"));
            personne.setRole(rs.getString("role"));
            personne.setEmail(rs.getString("email"));
            personne.setMotDePasse(rs.getString("motDePasse"));
            personne.setPhoto(rs.getString("photo"));

            personnes.add(personne);}
        return personnes;
    }
}
