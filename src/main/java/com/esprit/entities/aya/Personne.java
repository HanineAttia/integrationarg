package com.esprit.entities.aya;

public class Personne {
    private int id;
    private String nom;
    private String prenom;
    private String date ;
    private String role;
    private String email;
    private String motDePasse;
    private String photo;


    public Personne(){

    }
    public Personne(int id, String nom, String prenom,String date , String role, String email, String motDePasse, String photo) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.date = date;
        this.role = role;
        this.email = email;
        this.motDePasse = motDePasse;
        this.photo= photo;


    }
    public Personne(String nom, String prenom, String date, String role, String email, String motDePasse, String photo) {
        this.nom = nom;
        this.prenom = prenom;
        this.date = date;
        this.role = role;
        this.email = email;
        this.motDePasse = motDePasse;
        this.photo = photo;

    }
    public Personne(String nom, String prenom, String date, String role, String email, String motDePasse) {

        this.nom = nom;
        this.prenom = prenom;
        this.date = date;
        this.role = role;
        this.email = email;
        this.motDePasse = motDePasse;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getMotDePasse() {
        return motDePasse;
    }
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo= photo;
    }

    @Override
    public String toString() {
        return "Personne{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", date='" + date + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", motDePasse='" + motDePasse + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
