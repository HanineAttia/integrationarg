package com.esprit.entities.baya;

public class Visite {
    private int idVisite;
    private String nomVisite;
    private String description;
    private float dureeVisite;
    private String statut;

    // Constructeur
    public Visite(int idVisite, String nomVisite, String description, float dureeVisite, String statut) {
        this.idVisite = idVisite;
        this.nomVisite = nomVisite;
        this.description = description;
        this.dureeVisite = dureeVisite;
        this.statut = statut;
    }
    public Visite(int idVisite, String nomVisite, String description, String dureeVisite) {
        this.idVisite = idVisite;
        this.nomVisite = nomVisite;
        this.description = description;
        this.dureeVisite = Float.parseFloat(dureeVisite.replace("h", "").replace(",", "."));
        this.statut = "disponible"; // ou une valeur par défaut si tu veux
    }


    // Constructeur pour l'ajout (sans id)
    public Visite(String nomVisite, String description, float dureeVisite) {
        this.nomVisite = nomVisite;
        this.description = description;
        this.dureeVisite = dureeVisite;
        this.statut = "disponible"; // Valeur par défaut
    }


    // Getters et setters
    public int getIdVisite() {
        return idVisite;
    }

    public String getNomVisite() {
        return nomVisite;
    }

    public void setNomVisite(String nomVisite) {
        this.nomVisite = nomVisite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getDureeVisite() {
        return dureeVisite;
    }

    public void setDureeVisite(float dureeVisite) {
        this.dureeVisite = dureeVisite;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
