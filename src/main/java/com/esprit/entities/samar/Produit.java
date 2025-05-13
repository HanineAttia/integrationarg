package com.esprit.entities.samar;

public class Produit {
    private int idProduit;
    private String nom;
    private float prixUnitaire;
    private int idCategorie;

    // Constructeur complet
    public Produit(int idProduit, String nom, float prixUnitaire, int idCategorie) {
        this.idProduit = idProduit;
        this.nom = nom;
        this.prixUnitaire = prixUnitaire;
        this.idCategorie = idCategorie;
    }

    // Constructeur sans id (pour insertion)
    public Produit(String nom, float prixUnitaire, int idCategorie) {
        this.nom = nom;
        this.prixUnitaire = prixUnitaire;
        this.idCategorie = idCategorie;
    }

    // Constructeur vide
    public Produit() {}

    // Getters et Setters
    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public float getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(float prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }
}
