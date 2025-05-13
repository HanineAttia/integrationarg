package com.esprit.entities.hanine;

public class Materiels {
    private int id;
    private String name;
    private String description;
    private String imageurl;
    private String categorie;
    private double price;
    private String dateajout;
    private boolean disponibility;
    private int quantite;

    public Materiels() {}

    public Materiels(int id, String name, String description, String imageurl, String categorie, double price, String dateajout, boolean disponibility, int quantite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageurl = imageurl;
        this.categorie = categorie;
        this.price = price;
        this.dateajout = dateajout;
        this.disponibility = disponibility;
        this.quantite = quantite;
    }

    public Materiels(String name, String imageurl, String categorie, String description, double price, String dateajout, boolean disponibility, int quantite) {
        this.name = name;
        this.description = description;
        this.imageurl = imageurl;
        this.categorie = categorie;
        this.price = price;
        this.dateajout = dateajout;
        this.disponibility = disponibility;
        this.quantite = quantite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDateajout() {
        return dateajout;
    }

    public void setDateajout(String dateajout) {
        this.dateajout = dateajout;
    }

    public boolean isDisponibility() {
        return disponibility;
    }

    public void setDisponibility(boolean disponibility) {
        this.disponibility = disponibility;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "Materiels [id=" + id + ", name=" + name + ", description=" + description + ", imageurl=" + imageurl +
                ", categorie=" + categorie + ", price=" + price + ", dateajout=" + dateajout +
                ", disponibility=" + disponibility + ", quantite=" + quantite + "]";
    }
}