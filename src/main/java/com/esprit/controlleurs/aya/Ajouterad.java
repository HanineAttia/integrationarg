package com.esprit.controlleurs.aya;

import com.esprit.entities.aya.Personne;
import com.esprit.services.aya.Servicepersonne;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class Ajouterad {

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField motDePasseField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private ComboBox<String> roleBox;


    private final Servicepersonne servicepersonne = new Servicepersonne();


    @FXML
    void ajouterPersonne(ActionEvent event) {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String date = dateField.getValue() != null ? dateField.getValue().toString() : null;
        String role = roleBox.getValue();
        String email = emailField.getText();
        String motDePasse = motDePasseField.getText();

        if (nom.isEmpty() || prenom.isEmpty() || date == null || role == null || email.isEmpty() || motDePasse.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Tous les champs doivent être remplis.");
            return;
        }

        Personne p = new Personne(nom, prenom, date, role, email, motDePasse, "default.jpg"); // Ajoutez un nom d’image si nécessaire

        try {
            servicepersonne.ajouter(p);
            showAlert(Alert.AlertType.INFORMATION, "Utilisateur ajouté avec succès !");
            clearFields();

            // Retour à affichage.fxml après ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichage.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Affichage");
            currentStage.show();

            // Accéder au contrôleur de affichage.fxml et charger les données dans le tableau
            Affichercontroller controller = loader.getController();
            controller.loadPersonnes(); // Rafraîchit la liste des utilisateurs
            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout en base : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        dateField.setValue(null);
        roleBox.getSelectionModel().clearSelection();
        emailField.clear();
        motDePasseField.clear();


    }





    @FXML
    void annulerAjout(ActionEvent event) {
        // Effacer les champs de saisie
        clearFields();

        // Revenir à l'écran principal (affichage.fxml)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichage.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et la mettre à jour
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Affichage");
            currentStage.show();

            // Accéder au contrôleur et charger les données si nécessaire
            Affichercontroller afficherController = loader.getController();
            afficherController.loadPersonnes();  // Rafraîchir les données affichées
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'exception si nécessaire
        }

    }

}