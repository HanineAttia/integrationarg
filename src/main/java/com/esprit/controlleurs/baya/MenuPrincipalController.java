package com.esprit.controlleurs.baya;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuPrincipalController {

    public void goToEmployes(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Employes.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Gestion des Employés");
    }

    public void goToVisites(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Visite.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Gestion des Visites");
    }

    // Méthode pour le bouton Retour
    public void goBack(ActionEvent event) throws IOException {
        // Charger la scène de ChoixUser.fxml
        Parent root = FXMLLoader.load(getClass().getResource("/ChoixUser.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Choix de l'Utilisateur");
    }
}