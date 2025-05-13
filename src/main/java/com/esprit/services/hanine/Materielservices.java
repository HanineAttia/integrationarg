package com.esprit.services.hanine;

import com.esprit.entities.hanine.Materiels;
import com.esprit.services.ICrud;
import com.esprit.utils.MyDataBase;
import java.sql.Date;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Materielservices implements ICrud<Materiels> {

    Connection con;

    public Materielservices() {
        con = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Materiels materiels) throws SQLException {
        String sql = "INSERT INTO `materiels_agricoles` (id_materiel, nom, categorie, imageurl, description, prix, date_ajout, disponible, quantite) VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedstatement = con.prepareStatement(sql);
        preparedstatement.setInt(1, materiels.getId());
        preparedstatement.setString(2, materiels.getName());
        preparedstatement.setString(3, materiels.getCategorie());
        preparedstatement.setString(4, materiels.getImageurl());
        preparedstatement.setString(5, materiels.getDescription());
        preparedstatement.setDouble(6, materiels.getPrice());
        Date sqldate = Date.valueOf(materiels.getDateajout());
        preparedstatement.setDate(7, sqldate);
        preparedstatement.setBoolean(8, materiels.isDisponibility());
        preparedstatement.setInt(9, materiels.getQuantite());
        preparedstatement.executeUpdate();
        System.out.println("Materiel ajouté");
    }

    @Override
    public void modifier(Materiels materiels) throws SQLException {
        String sql = "UPDATE materiels_agricoles SET nom=?, categorie=?, imageurl=?, description=?, prix=?, date_ajout=?, disponible=?, quantite=? WHERE id_materiel=?";
        PreparedStatement preparedstatement = con.prepareStatement(sql);
        preparedstatement.setString(1, materiels.getName());
        preparedstatement.setString(2, materiels.getCategorie());
        preparedstatement.setString(3, materiels.getImageurl());
        preparedstatement.setString(4, materiels.getDescription());
        preparedstatement.setDouble(5, materiels.getPrice());
        Date sqldate = Date.valueOf(materiels.getDateajout());
        preparedstatement.setDate(6, sqldate);
        preparedstatement.setBoolean(7, materiels.isDisponibility());
        preparedstatement.setInt(8, materiels.getQuantite());
        preparedstatement.setInt(9, materiels.getId());
        preparedstatement.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM materiels_agricoles WHERE id_materiel = ?";
        PreparedStatement preparedstatement = con.prepareStatement(sql);
        preparedstatement.setInt(1, id);
        preparedstatement.executeUpdate();
        System.out.println("Materiel supprimé");
    }

    @Override
    public List<Materiels> getAll() throws SQLException {
        List<Materiels> materiallist = new ArrayList<>();
        String requete = "SELECT * FROM `materiels_agricoles`";
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery(requete);
        while (resultSet.next()) {
            Materiels materiels = new Materiels();
            materiels.setId(resultSet.getInt("id_materiel"));
            materiels.setName(resultSet.getString("nom"));
            materiels.setImageurl(resultSet.getString("imageurl"));
            materiels.setCategorie(resultSet.getString("categorie"));
            materiels.setDescription(resultSet.getString("description"));
            materiels.setPrice(resultSet.getDouble("prix"));
            materiels.setDateajout(resultSet.getString("date_ajout"));
            materiels.setDisponibility(resultSet.getBoolean("disponible"));
            materiels.setQuantite(resultSet.getInt("quantite"));
            materiallist.add(materiels);
        }
        return materiallist;
    }
}