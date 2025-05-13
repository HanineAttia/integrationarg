package com.esprit.controlleurs.aya;

import com.esprit.entities.aya.Personne;
import com.esprit.services.aya.Servicepersonne;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static com.esprit.controlleurs.aya.Logincontroller.personneactuelle;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class Modifierprofilcontrolleur {

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

    private Personne personneModif;
    public void initData(Personne personne) {
        this.personneModif = personne;

        nomField.setText(personne.getNom());
        prenomField.setText(personne.getPrenom());

        // Conversion date string → LocalDate
        dateField.setValue(LocalDate.parse(personne.getDate())); // Assure-toi que le format est ISO
        roleBox.setItems(FXCollections.observableArrayList("Agriculteur", "Ingénieur", "Utilisateur simple"));
        roleBox.setValue(personne.getRole());
        emailField.setText(personne.getEmail());
        motDePasseField.setText(personne.getMotDePasse());
    }

    @FXML
    void annulerModification(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profil.fxml"));
            Parent root = loader.load();

            Profilcontrolleur controller = loader.getController();
            controller.setPersonne(personneactuelle); // Pour réafficher les données dans le profil

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Profil utilisateur");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void validerModification(ActionEvent event) {
        if (personneModif == null) return; // sécurité

// Mise à jour des valeurs depuis les champs
        personneModif.setNom(nomField.getText());
        personneModif.setPrenom(prenomField.getText());
        personneModif.setDate(dateField.getValue().toString());
        personneModif.setRole(roleBox.getValue());
        personneModif.setEmail(emailField.getText());
        personneModif.setMotDePasse(motDePasseField.getText());

// Mise à jour dans la base
        try {
            Servicepersonne service = new Servicepersonne();
            service.modifier(personneModif);
            System.out.println("Profil mis à jour avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }
        personneactuelle=personneModif;


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profil.fxml"));
            Parent root = loader.load();

            Profilcontrolleur controller = loader.getController();
            controller.setPersonne(personneModif); // Pour réafficher les données dans le profil

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Profil utilisateur");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }}

}