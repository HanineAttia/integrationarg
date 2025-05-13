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

public class AjouterEmployeController {

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
    private ChoixUserController.EmployesController employesController;

    @FXML
    public void initialize() {
        Connection connection = MyDataBase.getInstance().getConnection();
        serviceEmployes = new ServiceEmployes(connection);
    }

    public void setEmployesController(ChoixUserController.EmployesController employesController) {
        this.employesController = employesController;
    }

    @FXML
    private void ajouterEmploye() {
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

            // Création et ajout de l'employé
            Employes employe = new Employes(nom, prenom, type, email, age, idVisite);
            serviceEmployes.ajouter(employe);

            // Message de succès
            montrerInfo("Employé ajouté avec succès !");

            // Rafraîchir la table dans EmployesController
            if (employesController != null) {
                employesController.rafraichirTableau();
            }

            // Fermer la fenêtre
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            montrerErreur("Erreur lors de l'ajout de l'employé.");
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