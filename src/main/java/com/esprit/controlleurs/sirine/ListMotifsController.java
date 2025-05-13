package com.esprit.controlleurs.sirine;

import com.esprit.entities.sirine.Motifs;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.esprit.services.sirine.ServiceMotifs;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ListMotifsController {

    @FXML
    private TableView<Motifs> motifsTable;

    @FXML
    private TableColumn<Motifs, String> nomColumn;

    @FXML
    private TableColumn<Motifs, String> descriptionColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button modifyButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label messageLabel;

    private ServiceMotifs serviceMotifs;

    @FXML
    public void initialize() {
        serviceMotifs = new ServiceMotifs();

        // Configure les colonnes
        nomColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        descriptionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        loadMotifs();

        // Active/Désactive les boutons modifier et supprimer selon la sélection
        motifsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean disable = newSelection == null;
            modifyButton.setDisable(disable);
            deleteButton.setDisable(disable);
        });
    }

    private void loadMotifs() {
        try {
            List<Motifs> motifs = serviceMotifs.afficher();
            if (motifs.isEmpty()) {
                messageLabel.setText("Aucune donnée à afficher. Ajoutez des motifs.");
                messageLabel.setStyle("-fx-text-fill: orange;");
            } else {
                motifsTable.getItems().setAll(motifs);
                messageLabel.setText("Motifs chargés avec succès : " + motifs.size() + " motifs.");
                messageLabel.setStyle("-fx-text-fill: green;");
            }
        } catch (Exception e) {
            messageLabel.setText("Erreur lors du chargement des motifs : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    private void addMotif() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutMotif.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Motif");
            stage.showAndWait();
            loadMotifs();
        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture du formulaire : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void modifyMotif() {
        Motifs selectedMotif = motifsTable.getSelectionModel().getSelectedItem();
        if (selectedMotif == null) {
            messageLabel.setText("Veuillez sélectionner un motif !");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierMotif.fxml"));
            Parent root = loader.load();
            ModifierMotifController controller = loader.getController();
            controller.setMotif(selectedMotif);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier un Motif");
            stage.showAndWait();
            loadMotifs();
        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture du formulaire : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void deleteMotif() {
        Motifs selectedMotif = motifsTable.getSelectionModel().getSelectedItem();
        if (selectedMotif == null) {
            messageLabel.setText("Veuillez sélectionner un motif !");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment supprimer ce motif ?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceMotifs.supprimer(selectedMotif.getId());
                loadMotifs();
                messageLabel.setText("Motif supprimé avec succès !");
                messageLabel.setStyle("-fx-text-fill: green;");
            } catch (Exception e) {
                messageLabel.setText("Erreur lors de la suppression : " + e.getMessage());
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }
}