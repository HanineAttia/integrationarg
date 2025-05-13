package com.esprit.services.asma;

import com.esprit.entities.asma.Commande;

import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeService {
    public boolean creerCommande(int idUtilisateur, int idPanier, double total) {
        try {
            Connection conn = MyDataBase.getInstance().getConnection();
            String sql = "INSERT INTO commandes (id_utilisateur, id_panier, date_commande, total, statut) VALUES (?, ?, NOW(), ?, 'PAYEE')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idPanier);
            stmt.setDouble(3, total);
            int rows = stmt.executeUpdate();
            System.out.println("✅ Commande créée avec succès (panier ID = " + idPanier + ")");
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("❌ Erreur création commande : " + e.getMessage());
            return false;
        }
    }
    public List<Commande> getCommandesByUser(int idUtilisateur) {
        List<Commande> commandes = new ArrayList<>();
        try {
            Connection conn = MyDataBase.getInstance().getConnection();
            String sql = "SELECT * FROM commandes WHERE id_utilisateur = ? ORDER BY date_commande DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUtilisateur);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Commande c = new Commande(
                        rs.getInt("id_commande"),
                        rs.getInt("id_utilisateur"),
                        rs.getInt("id_panier"),
                        rs.getTimestamp("date_commande").toLocalDateTime(),
                        rs.getDouble("total"),
                        rs.getString("statut")
                );
                commandes.add(c);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur chargement commandes : " + e.getMessage());
        }
        return commandes;
    }
}
