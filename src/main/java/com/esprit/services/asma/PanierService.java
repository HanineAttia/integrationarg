package com.esprit.services.asma;

import com.esprit.entities.asma.PanierProduit;
import com.esprit.entities.samar.Produit;
import com.esprit.utils.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanierService {
    private List<PanierProduit> panierProduits;
    private int idPanier = 1;

    public PanierService() {
        panierProduits = new ArrayList<>();
    }

    public void ajouterProduit(Produit produit, int quantite) {
        try {
            Connection conn = MyDataBase.getInstance().getConnection();

            // Étape 1 : Vérifier si le panier EN_COURS existe
            String checkQuery = "SELECT id_panier FROM panier WHERE id_utilisateur = ? AND statut = 'EN_COURS'";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, 1);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                idPanier = rs.getInt("id_panier");
            } else {
                // Créer un nouveau panier
                String insertPanier = "INSERT INTO panier (id_utilisateur, date_creation, statut) VALUES (?, NOW(), 'EN_COURS')";
                PreparedStatement insertStmt = conn.prepareStatement(insertPanier, Statement.RETURN_GENERATED_KEYS);
                insertStmt.setInt(1, 1);
                insertStmt.executeUpdate();
                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idPanier = generatedKeys.getInt(1);
                }
            }

            // Étape 2 : Vérifier si le produit est déjà dans le panier
            String select = "SELECT quantite FROM panier_produits WHERE id_panier = ? AND id_produit = ?";
            PreparedStatement selectStmt = conn.prepareStatement(select);
            selectStmt.setInt(1, idPanier);
            selectStmt.setInt(2, produit.getIdProduit());
            ResultSet rs2 = selectStmt.executeQuery();

            if (rs2.next()) {
                int ancienneQte = rs2.getInt("quantite");
                int nouvelleQte = ancienneQte + quantite;
                String update = "UPDATE panier_produits SET quantite = ? WHERE id_panier = ? AND id_produit = ?";
                PreparedStatement updateStmt = conn.prepareStatement(update);
                updateStmt.setInt(1, nouvelleQte);
                updateStmt.setInt(2, idPanier);
                updateStmt.setInt(3, produit.getIdProduit());
                updateStmt.executeUpdate();
            } else {
                String insert = "INSERT INTO panier_produits (id_panier, id_produit, quantite) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insert);
                insertStmt.setInt(1, idPanier);
                insertStmt.setInt(2, produit.getIdProduit());
                insertStmt.setInt(3, quantite);
                insertStmt.executeUpdate();
            }

            // Ajouter aussi à la liste Java (UI)
            for (PanierProduit pp : panierProduits) {
                if (pp.getProduit().getIdProduit() == produit.getIdProduit()) {
                    pp.setQuantite(pp.getQuantite() + quantite);
                    return;
                }
            }
            panierProduits.add(new PanierProduit(idPanier, produit, quantite));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifierQuantite(Produit produit, int nouvelleQuantite) {
        try {
            Connection conn = MyDataBase.getInstance().getConnection();
            String updateQuery = "UPDATE panier_produits SET quantite = ? WHERE id_panier = ? AND id_produit = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, nouvelleQuantite);
            updateStmt.setInt(2, idPanier);
            updateStmt.setInt(3, produit.getIdProduit());
            int rows = updateStmt.executeUpdate();
            System.out.println("🟢 Quantité mise à jour dans la base (produit " + produit.getNom() + ") → " + nouvelleQuantite + " (lignes affectées: " + rows + ")");
        } catch (SQLException e) {
            System.out.println("❌ Erreur update quantité SQL : " + e.getMessage());
        }

        // Mettre à jour en mémoire
        for (PanierProduit pp : panierProduits) {
            if (pp.getProduit().getIdProduit() == produit.getIdProduit()) {
                pp.setQuantite(nouvelleQuantite);
                return;
            }
        }
    }

    public boolean payerPanier() {
        try {
            Connection conn = MyDataBase.getInstance().getConnection();

            // Changer le statut du panier
            String updatePanier = "UPDATE panier SET statut = 'PAYE' WHERE id_panier = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updatePanier);
            updateStmt.setInt(1, idPanier);
            updateStmt.executeUpdate();

            // Créer la commande
            CommandeService commandeService = new CommandeService();
            boolean success = commandeService.creerCommande(1, idPanier, calculerTotal());

            // Nettoyer
            panierProduits.clear();
            idPanier = -1; // force la recréation d’un nouveau panier au prochain ajout

            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void supprimerProduit(Produit produit) {
        try {
            Connection conn = MyDataBase.getInstance().getConnection();
            String deleteQuery = "DELETE FROM panier_produits WHERE id_panier = ? AND id_produit = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, idPanier);
            deleteStmt.setInt(2, produit.getIdProduit());
            int rows = deleteStmt.executeUpdate();
            System.out.println("🗑 Produit supprimé (produit " + produit.getNom() + ") (lignes supprimées: " + rows + ")");
        } catch (SQLException e) {
            System.out.println("❌ Erreur suppression SQL : " + e.getMessage());
        }

        // Supprimer aussi de la liste en mémoire
        panierProduits.removeIf(pp -> pp.getProduit().getIdProduit() == produit.getIdProduit());
    }

    public void viderPanier() {
        try {
            Connection conn = MyDataBase.getInstance().getConnection();
            String deleteQuery = "DELETE FROM panier_produits WHERE id_panier = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, idPanier);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Erreur vidage panier SQL : " + e.getMessage());
        }
        panierProduits.clear();
    }

    public List<PanierProduit> getContenuPanier() {
        return panierProduits;
    }

    public List<PanierProduit> chargerContenuPanierDepuisDB() {
        panierProduits.clear();
        try {
            Connection conn = MyDataBase.getInstance().getConnection();

            // 🔍 Étape 1 : rechercher le panier
            String checkQuery = "SELECT id_panier FROM panier WHERE id_utilisateur = ? AND statut = 'EN_COURS'";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, 1);
            ResultSet rsCheck = checkStmt.executeQuery();
            if (rsCheck.next()) {
                idPanier = rsCheck.getInt("id_panier");
                System.out.println("🔎 Panier EN_COURS trouvé : ID = " + idPanier);
            } else {
                System.out.println("❌ Aucun panier EN_COURS trouvé pour l'utilisateur ID=1.");
                return panierProduits;
            }

            // 🔍 Étape 2 : récupérer les produits
            String query = "SELECT pp.id_panier, p.id_produit, p.nom, p.prix_unitaire, p.id_categorie, pp.quantite " +
                    "FROM panier_produits pp JOIN produit p ON pp.id_produit = p.id_produit " +
                    "WHERE pp.id_panier = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idPanier);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produit produit = new Produit(
                        rs.getInt("id_produit"),
                        rs.getString("nom"),
                        rs.getFloat("prix_unitaire"),
                        rs.getInt("id_categorie")
                );

                PanierProduit pp = new PanierProduit(
                        rs.getInt("id_panier"),
                        produit,
                        rs.getInt("quantite")
                );

                panierProduits.add(pp);
                System.out.println("✅ Produit trouvé : " + produit.getNom() + " x" + pp.getQuantite());
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
        return panierProduits;
    }

    public double calculerTotal() {
        return panierProduits.stream().mapToDouble(PanierProduit::getTotal).sum();
    }
}