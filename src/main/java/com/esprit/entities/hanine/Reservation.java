package com.esprit.entities.hanine;

public class Reservation {
    private int idReservation;
    private int idMateriel;
    private int idTechnicien;
    private String dateDebut;
    private String dateFin;
    private String dateReservation;
    private String statut;

    public Reservation() {}

    public Reservation(int idMateriel, int idTechnicien, String dateDebut, String dateFin, String dateReservation, String statut) {
        this.idMateriel = idMateriel;
        this.idTechnicien = idTechnicien;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.dateReservation = dateReservation;
        this.statut = statut;
    }

    public Reservation(int idReservation, int idMateriel, int idTechnicien, String dateDebut, String dateFin, String dateReservation, String statut) {
        this.idReservation = idReservation;
        this.idMateriel = idMateriel;
        this.idTechnicien = idTechnicien;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.dateReservation = dateReservation;
        this.statut = statut;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdMateriel() {
        return idMateriel;
    }

    public void setIdMateriel(int idMateriel) {
        this.idMateriel = idMateriel;
    }

    public int getIdTechnicien() {
        return idTechnicien;
    }

    public void setIdTechnicien(int idTechnicien) {
        this.idTechnicien = idTechnicien;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(String dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Reservation [idReservation=" + idReservation + ", idMateriel=" + idMateriel +
                ", idTechnicien=" + idTechnicien + ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin + ", dateReservation=" + dateReservation +
                ", statut=" + statut + "]";
    }
}