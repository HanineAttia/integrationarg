package com.esprit.services.samar;

import com.esprit.entities.samar.Categorie;
import com.esprit.services.ICrud;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCategorie implements ICrud<Categorie> {
    Connection connection;

    public ServiceCategorie() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Categorie categorie) throws SQLException {
        String req = "INSERT INTO categorie (nom, description) VALUES (?, ?)";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, categorie.getNom());
        ps.setString(2, categorie.getDescription());
        ps.executeUpdate();
        System.out.println("✅ Catégorie ajoutée !");
    }

    @Override
    public void modifier(Categorie categorie) throws SQLException {
        String req = "UPDATE categorie SET nom=?, description=? WHERE id_categorie=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, categorie.getNom());
        ps.setString(2, categorie.getDescription());
        ps.setInt(3, categorie.getIdCategorie());
        ps.executeUpdate();
        System.out.println("✅ Catégorie modifiée !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM categorie WHERE id_categorie=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("🗑 Catégorie supprimée !");
    }

    @Override
    public List<Categorie> getAll() throws SQLException {
        List<Categorie> categories = new ArrayList<>();
        String req = "SELECT * FROM categorie";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Categorie c = new Categorie();
            c.setIdCategorie(rs.getInt("id_categorie"));
            c.setNom(rs.getString("nom"));
            c.setDescription(rs.getString("description"));
            c.setDateCreation(rs.getDate("date_creation"));
            categories.add(c);
        }

        return categories;
    }
}
