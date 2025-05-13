package com.esprit.entities.sirine;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EntiteGlobale {
    private IntegerProperty id;
    private StringProperty type;
    private StringProperty nom;
    private StringProperty contenu;
    private StringProperty statut;
    private StringProperty idReclamation;

    public EntiteGlobale(int id, String type, String nom, String contenu, String statut, String idReclamation) {
        this.id = new SimpleIntegerProperty(id);
        this.type = new SimpleStringProperty(type);
        this.nom = new SimpleStringProperty(nom);
        this.contenu = new SimpleStringProperty(contenu);
        this.statut = new SimpleStringProperty(statut);
        this.idReclamation = new SimpleStringProperty(idReclamation);
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getType() { return type.get(); }
    public void setType(String type) { this.type.set(type); }
    public StringProperty typeProperty() { return type; }

    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }
    public StringProperty nomProperty() { return nom; }

    public String getContenu() { return contenu.get(); }
    public void setContenu(String contenu) { this.contenu.set(contenu); }
    public StringProperty contenuProperty() { return contenu; }

    public String getStatut() { return statut.get(); }
    public void setStatut(String statut) { this.statut.set(statut); }
    public StringProperty statutProperty() { return statut; }

    public String getIdReclamation() { return idReclamation.get(); }
    public void setIdReclamation(String idReclamation) { this.idReclamation.set(idReclamation); }
    public StringProperty idReclamationProperty() { return idReclamation; }
}