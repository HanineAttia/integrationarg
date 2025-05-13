package com.esprit.entities.asma;

import java.time.LocalDateTime;
public class Commande {
    private int idCommande;
    private int idUtilisateur;
    private int idPanier;
    private LocalDateTime dateCommande;
    private double total;
    private String statut;

    public Commande(int idCommande, int idUtilisateur, int idPanier, LocalDateTime dateCommande, double total, String statut) {
        this.idCommande = idCommande;
        this.idUtilisateur = idUtilisateur;
        this.idPanier = idPanier;
        this.dateCommande = dateCommande;
        this.total = total;
        this.statut = statut;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public int getIdPanier() {
        return idPanier;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public double getTotal() {
        return total;
    }

    public String getStatut() {
        return statut;
    }
}
