package com.esprit.controlleurs.sirine;

import com.esprit.entities.sirine.Reponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.esprit.services.sirine.ServiceRep;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Optional;

public class ListRepController {

    @FXML private TableView<Reponse> reponseTable;
    @FXML private TableColumn<Reponse, Integer> idColumn;
    @FXML private TableColumn<Reponse, Integer> idReclamationColumn;
    @FXML private TableColumn<Reponse, String> contenuColumn;
    @FXML private Button addButton;
    @FXML private Button modifyButton;
    @FXML private Button deleteButton;
    @FXML private Label messageLabel;

    private final ServiceRep serviceRep = new ServiceRep();

    @FXML
    public void initialize() {

    }

    private void loadReponses() {
        try {
            reponseTable.getItems().setAll(serviceRep.afficher());
            messageLabel.setText("");
        } catch (Exception e) {
            messageLabel.setText("Erreur chargement : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void addReponse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutReponse.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une Réponse");
            stage.showAndWait();
            loadReponses();
        } catch (IOException e) {
            messageLabel.setText("Erreur formulaire : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void modifyReponse() {
        Reponse selected = reponseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Sélectionnez une réponse !");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReponse.fxml"));
            Parent root = loader.load();
            ModifierRepController controller = loader.getController();
            controller.setReponse(selected);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier une Réponse");
            stage.showAndWait();
            loadReponses();
        } catch (IOException e) {
            messageLabel.setText("Erreur formulaire : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void deleteReponse() {
        Reponse selected = reponseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Sélectionnez une réponse !");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Voulez-vous supprimer ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceRep.supprimer(selected.getId());
                loadReponses();
                messageLabel.setText("Réponse supprimée !");
                messageLabel.setStyle("-fx-text-fill: green;");
            } catch (Exception e) {
                messageLabel.setText("Erreur suppression : " + e.getMessage());
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    private void goToHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/MainInterface.fxml"));
            Stage stage = (Stage) reponseTable.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Accueil");
        } catch (IOException e) {
            messageLabel.setText("Erreur retour accueil : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    public void goToHome(javafx.event.ActionEvent actionEvent) {
    }
}