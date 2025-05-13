package com.esprit.tests;

import com.esprit.entities.hanine.Materiels;
import com.esprit.services.hanine.Materielservices;
import com.esprit.utils.MyDataBase;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Materielservices ms = new Materielservices();
        try {
            ms.ajouter(new Materiels("Drone DJI", ":C/downloads/pictures/", "Drone", "Drone pour agriculture de précision", 12000.0, "2024-03-25", true, 5));
            //ms.supprimer(6);
            //Materiels m1 = new Materiels(5, "Tracteur X9", "Materiel de traction", "Tracteur", "Tracteur pour grandes cultures", 45000.00, "2024-02-15", true, 3);
            //ms.modifier(m1);
            System.out.println(ms.getAll());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}