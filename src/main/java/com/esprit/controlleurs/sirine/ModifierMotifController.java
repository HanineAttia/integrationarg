package com.esprit.controlleurs.sirine;

import com.esprit.entities.sirine.Motifs;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.esprit.services.sirine.ServiceMotifs;

public class ModifierMotifController {

    @FXML
    private TextField nomField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Label messageLabel;

    private ServiceMotifs serviceMotifs = new ServiceMotifs();
    private Motifs motif;

    public void setMotif(Motifs motif) {
        this.motif = motif;
        nomField.setText(motif.getNom());
        descriptionArea.setText(motif.getDescription());
    }

    @FXML
    private void saveMotif() {
        try {
            if (nomField.getText().isEmpty() || descriptionArea.getText().isEmpty()) {
                messageLabel.setText("Tous les champs sont obligatoires !");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            motif.setNom(nomField.getText());
            motif.setDescription(descriptionArea.getText());
            serviceMotifs.modifier(motif);

            messageLabel.setText("Motif modifié avec succès !");
            messageLabel.setStyle("-fx-text-fill: green;");

            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de la modification : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}