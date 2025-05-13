package com.esprit.entities.baya;

public class Employes {
    private int idEmploye;
    private String nom;
    private String prenom;
    private String type;
    private String email;
    private int age;
    private int idVisite;

    // Constructeur
    public Employes(int idEmploye, String nom, String prenom, String type, String email, int age, int idVisite) {
        this.idEmploye = idEmploye;
        this.nom = nom;
        this.prenom = prenom;
        this.type = type;
        this.email = email;
        this.age = age;
        this.idVisite = idVisite;
    }

    // Constructeur pour l'ajout (sans id)
    public Employes(String nom, String prenom, String type, String email, int age, int idVisite) {
        this.nom = nom;
        this.prenom = prenom;
        this.type = type;
        this.email = email;
        this.age = age;
        this.idVisite = idVisite;
    }
    // Getters et setters
    public int getIdEmploye() {
        return idEmploye;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getIdVisite() {
        return idVisite;
    }

    public void setIdVisite(int idVisite) {
        this.idVisite = idVisite;
    }
}
