package com.esprit.controlleurs.baya;


import com.esprit.entities.baya.Visite;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.esprit.services.baya.ServiceVisites;
import com.esprit.utils.MyDataBase;

import java.sql.Connection;
import java.sql.SQLException;

public class ModifierVisiteController {

    @FXML
    private TextField nomVisiteField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField dureeVisiteField;
    @FXML
    private TextField statutField;

    private ServiceVisites serviceVisites;
    private Visite visite;
    private VisiteController visiteController;

    @FXML
    public void initialize() {
        Connection connection = MyDataBase.getInstance().getConnection();
        serviceVisites = new ServiceVisites(connection);
    }

    public void setVisite(Visite visite) {
        this.visite = visite;
        nomVisiteField.setText(visite.getNomVisite());
        descriptionField.setText(visite.getDescription());
        dureeVisiteField.setText(String.valueOf(visite.getDureeVisite()));
        statutField.setText(visite.getStatut());
    }

    public void setVisiteController(VisiteController visiteController) {
        this.visiteController = visiteController;
    }

    @FXML
    private void modifierVisite() {
        try {
            // Validation des champs
            String nomVisite = nomVisiteField.getText().trim();
            String description = descriptionField.getText().trim();
            String dureeVisiteText = dureeVisiteField.getText().trim();
            String statut = statutField.getText().trim();

            if (nomVisite.isEmpty()) {
                showError("Le nom de la visite est requis.");
                return;
            }
            if (dureeVisiteText.isEmpty()) {
                showError("La durée de la visite est requise.");
                return;
            }
            if (statut.isEmpty()) {
                showError("Le statut est requis.");
                return;
            }

            float dureeVisite;
            try {
                dureeVisite = Float.parseFloat(dureeVisiteText);
                if (dureeVisite <= 0) {
                    showError("La durée doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Veuillez entrer une durée valide.");
                return;
            }

            // Mise à jour de la visite
            visite.setNomVisite(nomVisite);
            visite.setDescription(description);
            visite.setDureeVisite(dureeVisite);
            visite.setStatut(statut);

            serviceVisites.modifier(visite);

            // Message de succès
            showInfo("Modification avec succès !");

            // Rafraîchir la table dans VisiteController
            if (visiteController != null) {
                visiteController.refreshTable();
            }

            // Fermer la fenêtre
            Stage stage = (Stage) nomVisiteField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors de la modification.");
        }
    }

    @FXML
    private void retour() {
        Stage stage = (Stage) nomVisiteField.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}