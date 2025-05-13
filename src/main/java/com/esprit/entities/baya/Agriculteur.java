package com.esprit.entities.baya;

public class Agriculteur {
    private int idAgriculteur;
    private int idVisite;
    private String telephone;

    public Agriculteur() {}

    public Agriculteur(int idVisite, String telephone) {
        this.idVisite = idVisite;
        this.telephone = telephone;
    }

    public int getIdAgriculteur() {
        return idAgriculteur;
    }

    public void setIdAgriculteur(int idAgriculteur) {
        this.idAgriculteur = idAgriculteur;
    }

    public int getIdVisite() {
        return idVisite;
    }

    public void setIdVisite(int idVisite) {
        this.idVisite = idVisite;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
