package com.esprit.controlleurs.baya;


import com.esprit.entities.baya.Employes;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.esprit.services.baya.ServiceEmployes;
import com.esprit.utils.MyDataBase;

import java.sql.Connection;
import java.sql.SQLException;

public class ModifierEmployeController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField idVisiteField;

    private ServiceEmployes serviceEmployes;
    private Employes employe;
    private DetailsEmployesController detailsEmployesController;

    @FXML
    public void initialize() {
        Connection connection = MyDataBase.getInstance().getConnection();
        serviceEmployes = new ServiceEmployes(connection);
    }

    public void setEmploye(Employes employe) {
        this.employe = employe;
        nomField.setText(employe.getNom());
        prenomField.setText(employe.getPrenom());
        typeField.setText(employe.getType());
        emailField.setText(employe.getEmail());
        ageField.setText(String.valueOf(employe.getAge()));
        idVisiteField.setText(String.valueOf(employe.getIdVisite()));
    }

    public void setDetailsEmployesController(DetailsEmployesController detailsEmployesController) {
        this.detailsEmployesController = detailsEmployesController;
    }

    @FXML
    private void confirmerModification() {
        try {
            // Validation des champs
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String type = typeField.getText().trim();
            String email = emailField.getText().trim();
            String ageText = ageField.getText().trim();
            String idVisiteText = idVisiteField.getText().trim();

            if (nom.isEmpty() || prenom.isEmpty() || type.isEmpty() || email.isEmpty() || ageText.isEmpty() || idVisiteText.isEmpty()) {
                montrerErreur("Tous les champs sont requis.");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageText);
                if (age <= 0) {
                    montrerErreur("L'âge doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                montrerErreur("Veuillez entrer un âge valide.");
                return;
            }

            int idVisite;
            try {
                idVisite = Integer.parseInt(idVisiteText);
                if (idVisite <= 0) {
                    montrerErreur("L'ID de la visite doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                montrerErreur("Veuillez entrer un ID de visite valide.");
                return;
            }

            // Mise à jour de l'employé
            employe.setNom(nom);
            employe.setPrenom(prenom);
            employe.setType(type);
            employe.setEmail(email);
            employe.setAge(age);
            employe.setIdVisite(idVisite);

            serviceEmployes.modifier(employe);

            // Message de succès
            montrerInfo("Employé modifié avec succès !");

            // Rafraîchir les détails dans DetailsEmployesController
            if (detailsEmployesController != null) {
                detailsEmployesController.rafraichirDetails();
            }

            // Fermer la fenêtre
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            montrerErreur("Erreur lors de la modification de l'employé.");
            e.printStackTrace();
        }
    }

    @FXML
    private void annuler() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private void montrerErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void montrerInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}