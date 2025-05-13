package com.esprit.entities.samar;
import java.sql.Date;
public class Categorie {
    private int idCategorie;
    private String nom;
    private String description;
    private Date dateCreation;

    // Constructeurs
    public Categorie() {
    }

    public Categorie(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public Categorie(int idCategorie, String nom, String description, Date dateCreation) {
        this.idCategorie = idCategorie;
        this.nom = nom;
        this.description = description;
        this.dateCreation = dateCreation;
    }

    // Getters et Setters
    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

}
