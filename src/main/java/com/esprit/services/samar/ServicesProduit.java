package com.esprit.services.samar;

import com.esprit.entities.samar.Produit;
import com.esprit.services.ICrud;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicesProduit implements ICrud<Produit> {
    private Connection connection;

    public ServicesProduit() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Produit produit) throws SQLException {
        String req = "INSERT INTO produit (nom, prix_unitaire, id_categorie) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, produit.getNom());
        ps.setFloat(2, produit.getPrixUnitaire());
        ps.setInt(3, produit.getIdCategorie());
        ps.executeUpdate();
        System.out.println("✅ Produit ajouté !");
    }

    @Override
    public void modifier(Produit produit) throws SQLException {
        String req = "UPDATE produit SET nom=?, prix_unitaire=?, id_categorie=? WHERE id_produit=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, produit.getNom());
        ps.setFloat(2, produit.getPrixUnitaire());
        ps.setInt(3, produit.getIdCategorie());
        ps.setInt(4, produit.getIdProduit());
        ps.executeUpdate();
        System.out.println("✅ Produit modifié !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM produit WHERE id_produit=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("🗑 Produit supprimé !");
    }

    @Override
    public List<Produit> getAll() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT * FROM produit";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Produit p = new Produit();
            p.setIdProduit(rs.getInt("id_produit"));
            p.setNom(rs.getString("nom"));
            p.setPrixUnitaire(rs.getFloat("prix_unitaire"));
            p.setIdCategorie(rs.getInt("id_categorie"));
            produits.add(p);
        }

        return produits;
    }
}
