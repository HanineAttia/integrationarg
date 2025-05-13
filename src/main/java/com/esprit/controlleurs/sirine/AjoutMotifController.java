package com.esprit.controlleurs.sirine;

import com.esprit.entities.sirine.Motifs;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import com.esprit.services.sirine.ServiceMotifs;

import java.sql.SQLException;

public class AjoutMotifController {

    @FXML
    private ComboBox<String> nomCombo;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Label messageLabel;

    private ServiceMotifs serviceMotifs = new ServiceMotifs();

    public AjoutMotifController() throws SQLException {
    }

    @FXML
    public void initialize() {
        nomCombo.getItems().addAll(serviceMotifs.getMotifOptions());
    }

    @FXML
    private void addMotif() {
        try {
            if (nomCombo.getValue() == null || descriptionArea.getText().isEmpty()) {
                messageLabel.setText("Tous les champs sont obligatoires !");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            Motifs motif = new Motifs(0, nomCombo.getValue(), descriptionArea.getText());
            serviceMotifs.ajouter(motif);

            messageLabel.setText("Motif ajouté avec succès !");
            messageLabel.setStyle("-fx-text-fill: green;");

            Stage stage = (Stage) nomCombo.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de l'ajout : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) nomCombo.getScene().getWindow();
        stage.close();
    }
}