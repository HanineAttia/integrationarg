package com.esprit.entities.asma;

import com.esprit.entities.samar.Produit;

public class PanierProduit {
    private int idPanier;
    private Produit produit;
    private int quantite;

    public PanierProduit(int idPanier, Produit produit, int quantite) {
        this.idPanier = idPanier;
        this.produit = produit;
        this.quantite = quantite;
    }

    public int getIdPanier() {
        return idPanier;
    }

    public Produit getProduit() {
        return produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public double getPrixUnitaire() {
        return produit.getPrixUnitaire();
    }

    public double getTotal() {
        return produit.getPrixUnitaire() * quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}