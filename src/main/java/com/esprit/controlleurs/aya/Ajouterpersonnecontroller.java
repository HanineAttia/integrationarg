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
import java.sql.SQLException;
import java.time.LocalDate;

public class Ajouterpersonnecontroller {

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
    private ComboBox<?> roleBox;

    @FXML
    void ajouterPersonne(ActionEvent event) {
        Servicepersonne servicePersonne = new Servicepersonne();
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        LocalDate date = dateField.getValue();
        String role = (String) roleBox.getValue();
        String email = emailField.getText();
        String motDePasse = motDePasseField.getText();

        if (nom.isEmpty() || prenom.isEmpty() || date == null || role == null || email.isEmpty() || motDePasse.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        Personne personne = new Personne(nom, prenom, date.toString(), role, email, motDePasse);

        try {
            servicePersonne.ajouter(personne);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "La personne a été enregistrée avec succès.");
            clearFields();
            // Charger la vue du profil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            // Passer la personne au contrôleur
            /*Profilcontrolleur controller = loader.getController();
            controller.setPersonne(personne);*/

            // Afficher la nouvelle scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Une erreur est survenue : " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        dateField.setValue(null);
        roleBox.setValue(null);
        emailField.clear();
        motDePasseField.clear();
    }


}

