package com.esprit.controlleurs.sirine;

import com.esprit.entities.sirine.Reponse;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.esprit.services.sirine.ServiceRep;

import java.sql.SQLException;

public class ModifierRepController {

    @FXML
    private TextArea contenuField;

    @FXML
    private TextField idReclamationField;

    @FXML
    private Label messageLabel;

    private Reponse reponse;
    private ServiceRep serviceRep;

    public ModifierRepController() {
        this.serviceRep = new ServiceRep();
    }

    public void setReponse(Reponse reponse) {
        this.reponse = reponse;
        contenuField.setText(reponse.getContenu());
        idReclamationField.setText(String.valueOf(reponse.getIdReclamation()));
    }

    @FXML
    private void saveReponse() {
        try {
            reponse.setContenu(contenuField.getText());
            reponse.setIdReclamation(Integer.parseInt(idReclamationField.getText()));
            serviceRep.modifier(reponse);
            messageLabel.setText("Réponse modifiée avec succès.");
            messageLabel.setStyle("-fx-text-fill: green;");
            Stage stage = (Stage) contenuField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de la modification : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            messageLabel.setText("L'ID de la réclamation doit être un nombre.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) contenuField.getScene().getWindow();
        stage.close();
    }
}