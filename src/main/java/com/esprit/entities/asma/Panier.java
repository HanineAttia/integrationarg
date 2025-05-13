package com.esprit.entities.asma;

import java.time.LocalDateTime;
public class Panier {
    private int idPanier;
    private int idUtilisateur;
    private LocalDateTime dateCreation;
    private String statut;

    public Panier(int idPanier, int idUtilisateur, LocalDateTime dateCreation, String statut) {
        this.idPanier = idPanier;
        this.idUtilisateur = idUtilisateur;
        this.dateCreation = dateCreation;
        this.statut = statut;
    }

    public Panier(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
        this.dateCreation = LocalDateTime.now();
        this.statut = "EN_COURS";
    }

    public int getIdPanier() {
        return idPanier;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public String getStatut() {
        return statut;
    }

    public void setIdPanier(int idPanier) {
        this.idPanier = idPanier;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

}
