package com.esprit.entities.sirine;

public class Reponse {
    private int id;
    private int idReclamation;
    private String contenu;

    public Reponse() {}

    public Reponse(int id, int idReclamation, String contenu) {
        this.id = id;
        this.idReclamation = idReclamation;
        this.contenu = contenu;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdReclamation() { return idReclamation; }
    public void setIdReclamation(int idReclamation) { this.idReclamation = idReclamation; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
}