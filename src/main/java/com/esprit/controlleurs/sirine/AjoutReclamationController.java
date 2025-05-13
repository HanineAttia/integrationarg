package com.esprit.controlleurs.sirine;

import com.esprit.entities.sirine.Reclamation;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.esprit.services.sirine.ServiceRec;

import java.sql.SQLException;

public class AjoutReclamationController {

    @FXML
    private TextField idUserField;

    @FXML
    private ComboBox<String> motifCombo;

    @FXML
    private TextArea contenuArea;

    @FXML
    private Label messageLabel;

    private ServiceRec serviceRec;

    @FXML
    public void initialize() {
        try {
            serviceRec = new ServiceRec();
            motifCombo.getItems().addAll(serviceRec.getAllMotifs());
        } catch (SQLException e) {
            messageLabel.setText("Erreur lors du chargement des motifs : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    public void setMotif(String motif) {
        motifCombo.setValue(motif);
    }

    @FXML
    private void addReclamation() {
        try {
            if (idUserField.getText().isEmpty() || motifCombo.getValue() == null || contenuArea.getText().isEmpty()) {
                messageLabel.setText("Tous les champs sont obligatoires !");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            int idUser = Integer.parseInt(idUserField.getText());
            Reclamation reclamation = new Reclamation(
                    0, // ID sera généré automatiquement
                    idUser,
                    motifCombo.getValue(),
                    contenuArea.getText(),
                    "En attente"
            );
            serviceRec.ajouter(reclamation);

            messageLabel.setText("Réclamation ajoutée avec succès ! ID généré : " + reclamation.getId());
            messageLabel.setStyle("-fx-text-fill: green;");

            Stage stage = (Stage) idUserField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            messageLabel.setText("ID Utilisateur doit être un nombre !");
            messageLabel.setStyle("-fx-text-fill: red;");
        } catch (SQLException e) {
            messageLabel.setText("Erreur SQL lors de l'ajout : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) idUserField.getScene().getWindow();
        stage.close();
    }
}