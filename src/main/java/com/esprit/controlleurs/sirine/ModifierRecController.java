package com.esprit.controlleurs.sirine;

import com.esprit.entities.sirine.Reclamation;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.esprit.services.sirine.ServiceMotifs;
import com.esprit.services.sirine.ServiceRec;

import java.sql.SQLException;
import java.util.List;

public class ModifierRecController {

    @FXML
    private TextField idUserField;

    @FXML
    private ComboBox<String> motifCombo;

    @FXML
    private TextArea contenuArea;

    @FXML
    private TextField statutField;

    @FXML
    private Label messageLabel;

    private Reclamation reclamation;
    private ServiceRec serviceRec;
    private ServiceMotifs serviceMotifs;

    public ModifierRecController() {
        try {
            this.serviceRec = new ServiceRec();
            this.serviceMotifs = new ServiceMotifs();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Charger les motifs dans le ComboBox
        List<String> motifs = serviceMotifs.getMotifOptions();
        motifCombo.getItems().addAll(motifs);
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
        idUserField.setText(String.valueOf(reclamation.getIdUser()));
        motifCombo.setValue(reclamation.getNomMotif());
        contenuArea.setText(reclamation.getContenu());
        statutField.setText(reclamation.getStatut());
    }

    @FXML
    private void saveReclamation() {
        try {
            reclamation.setIdUser(Integer.parseInt(idUserField.getText()));
            reclamation.setNomMotif(motifCombo.getValue());
            reclamation.setContenu(contenuArea.getText());
            reclamation.setStatut(statutField.getText());
            serviceRec.modifier(reclamation);
            messageLabel.setText("Réclamation modifiée avec succès.");
            messageLabel.setStyle("-fx-text-fill: green;");
            Stage stage = (Stage) idUserField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de la modification : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            messageLabel.setText("L'ID utilisateur doit être un nombre.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) idUserField.getScene().getWindow();
        stage.close();
    }
}