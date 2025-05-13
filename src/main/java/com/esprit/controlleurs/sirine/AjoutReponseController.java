package com.esprit.controlleurs.sirine;

import com.esprit.entities.sirine.Reponse;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.esprit.services.sirine.ServiceRec;
import com.esprit.services.sirine.ServiceRep;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AjoutReponseController {

    @FXML
    private TextField idReclamationField;

    @FXML
    private TextArea contenuArea;

    @FXML
    private Label messageLabel;

    private ServiceRep serviceRep;
    private ServiceRec serviceRec;

    // Liste de suggestions de SMS (simulée pour l'exemple)
    private static final List<String> SMS_SUGGESTIONS = Arrays.asList(
            "Bonjour, merci pour votre réclamation. Nous allons traiter votre demande rapidement.",
            "Nous avons bien reçu votre message. Une solution vous sera proposée sous 48h.",
            "Merci de votre patience. Votre réclamation est en cours de traitement.",
            "Bonjour, nous sommes désolés pour ce désagrément. Nous vous recontactons bientôt.",
            "Votre réclamation a été bien prise en compte. Vous recevrez une réponse sous peu."
    );

    @FXML
    public void initialize() {
        try {
            serviceRep = new ServiceRep();
            serviceRec = new ServiceRec();
        } catch (IllegalStateException | SQLException e) {
            messageLabel.setText("Erreur : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void addReponse() {
        try {
            if (serviceRep == null || serviceRec == null) {
                messageLabel.setText("Service non initialisé !");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            if (idReclamationField.getText().isEmpty() || contenuArea.getText().isEmpty()) {
                messageLabel.setText("Tous les champs sont obligatoires !");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            int idReclamation = Integer.parseInt(idReclamationField.getText());
            // Vérifier si l'ID de réclamation existe
            boolean reclamationExists = serviceRec.afficher().stream()
                    .anyMatch(r -> r.getId() == idReclamation);
            if (!reclamationExists) {
                messageLabel.setText("ID Réclamation " + idReclamation + " n'existe pas !");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            Reponse reponse = new Reponse(
                    0,
                    idReclamation,
                    contenuArea.getText()
            );
            serviceRep.ajouter(reponse);

            messageLabel.setText("Réponse ajoutée avec succès !");
            messageLabel.setStyle("-fx-text-fill: green;");

            Stage stage = (Stage) idReclamationField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            messageLabel.setText("ID Réclamation doit être un nombre !");
            messageLabel.setStyle("-fx-text-fill: red;");
        } catch (SQLException e) {
            messageLabel.setText("Erreur SQL lors de l'ajout : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) idReclamationField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void generateSmsIdea() {
        try {
            // Simuler une API en sélectionnant un SMS aléatoire dans la liste
            Random random = new Random();
            String smsIdea = SMS_SUGGESTIONS.get(random.nextInt(SMS_SUGGESTIONS.size()));

            // Remplir le champ contenuArea avec la suggestion
            contenuArea.setText(smsIdea);
            messageLabel.setText("Idée de SMS générée avec succès !");
            messageLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de la génération de l'idée de SMS : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
}