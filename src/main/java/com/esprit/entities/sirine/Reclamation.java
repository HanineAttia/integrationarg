package com.esprit.entities.sirine;

public class Reclamation {
    private int id;
    private int idUser;
    private String nomMotif;
    private String contenu;
    private String statut;

    public Reclamation() {}

    public Reclamation(int id, int idUser, String nomMotif, String contenu, String statut) {
        this.id = id;
        this.idUser = idUser;
        this.nomMotif = nomMotif;
        this.contenu = contenu;
        this.statut = statut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public String getNomMotif() { return nomMotif; }
    public void setNomMotif(String nomMotif) { this.nomMotif = nomMotif; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return nomMotif;
    }
}
