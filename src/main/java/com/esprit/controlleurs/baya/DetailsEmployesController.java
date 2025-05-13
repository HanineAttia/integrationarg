package com.esprit.controlleurs.baya;


import com.esprit.entities.baya.Employes;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DetailsEmployesController {

    @FXML
    private Label idEmployeLabel;
    @FXML
    private Label nomLabel;
    @FXML
    private Label prenomLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label idVisiteLabel;

    private Employes employe;
    private EmployesController employesController;

    public void setEmploye(Employes employe) {
        this.employe = employe;
        idEmployeLabel.setText(String.valueOf(employe.getIdEmploye()));
        nomLabel.setText(employe.getNom());
        prenomLabel.setText(employe.getPrenom());
        typeLabel.setText(employe.getType());
        emailLabel.setText(employe.getEmail());
        ageLabel.setText(String.valueOf(employe.getAge()));
        idVisiteLabel.setText(String.valueOf(employe.getIdVisite()));
    }

    public void setEmployesController(EmployesController employesController) {
        this.employesController = employesController;
    }

    @FXML
    private void modifierEmploye() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEmploye.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Modifier un Employé");
            stage.initModality(Modality.APPLICATION_MODAL);

            // Passer l'employé et ce contrôleur
            ModifierEmployeController controller = loader.getController();
            controller.setEmploye(employe);
            controller.setDetailsEmployesController(this);

            stage.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors de l'ouverture de la fenêtre de modification.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void retour() {
        // Rafraîchir la table dans EmployesController
        if (employesController != null) {
            employesController.rafraichirTableau();
        }
        Stage stage = (Stage) idEmployeLabel.getScene().getWindow();
        stage.close();
    }

    public void rafraichirDetails() {
        setEmploye(employe); // Rafraîchir les données affichées
    }
}